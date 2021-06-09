package pik.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pik.repository.mysqlDAOs.UserDAO;
import pik.repository.oauth.LoginUsers;
import pik.repository.util.*;
import pik.repository.openstack.MediaFileDAO;
import pik.repository.openstack.SwiftMediaFileDAO;
import pik.repository.util.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    public LoginUsers loginUsers;
    private final MediaFileDAO mediaFileDAO;
    private static final UserDAO userDAO = new UserDAO();
    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String HEADER_TOKEN = "X-API-TOKEN";
    private static final String KEY = "pikKey";

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @Autowired
    public RepositoryApplication(SwiftMediaFileDAO swiftMediaFileDAO, LoginUsers loginUsers){
        this.loginUsers = loginUsers;
        mediaFileDAO = swiftMediaFileDAO;
    }

    private String checkJwt(String tokenJwt){
        String nick;
        if (tokenJwt == null) {
            return "";
        } else {
            try {
                Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(tokenJwt).getBody();
                nick = claims.getSubject();
            } catch (final SignatureException e) {
                return "";
            }
        }
        if(loginUsers.checkUser(nick)){
            return nick;
        }
        return "";
    }

    @CrossOrigin
    @RequestMapping(value = "/oauth/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody @Valid Login dane) {
        String email = dane.getEmail();
        String password = dane.getPassword();
        if (!userDAO.isUserExist(email)) {
            return ResponseEntity.notFound().build();
        }
        if (!userDAO.isPasswordMatch(email, password)) {
            return ResponseEntity.status(401).body("error password");
        }
        String token = loginUsers.addUser(email);
        return ResponseEntity.ok(Jwts.builder().setSubject(email).claim("token", token).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1200000)).signWith(SignatureAlgorithm.HS512, KEY).compact());
    }

    @CrossOrigin
    @RequestMapping(value = "/oauth/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody @Valid UserData dane) {
        if (userDAO.isUserExist(dane.getEmail())) {
            return ResponseEntity.status(401).body("just exist");
        }
        userDAO.insertUser(dane.getName(), dane.getSurname(), dane.getEmail(), dane.getPassword());
        return ResponseEntity.ok("");
    }

    @CrossOrigin
    @GetMapping(value = "/api/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@RequestHeader(HEADER_TOKEN) String token, @RequestParam("type") String type) {
        String email = checkJwt(token);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (!(type.equals("any") || type.equals("video") || type.equals("image"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<MediaFile> all = mediaFileDAO.getAllByUserAndType(email, type);

        if (all == null) return ResponseEntity.ok("[]");
        if (all.size() == 0) return ResponseEntity.ok("[]");

        return parseOrError(all);
    }

    @CrossOrigin
    @GetMapping(value = "/api/file/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getOneById(@RequestHeader(HEADER_TOKEN) String token, @PathVariable String fileId) {
        String email = checkJwt(token);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        return getOne(email, fileId, null);
    }

    private ResponseEntity getOne(String email, String fileId, String displayName) {
        MediaFile result;
        if (fileId != null) {
            result = mediaFileDAO.getMediaFile(email, fileId);
        } else if (displayName != null) {
            result = mediaFileDAO.getMediaFileByDisplayName(email, displayName);
        } else {
            return ResponseEntity.status(400).body("File id or display name must be provided");
        }
        if (result == null) {
            return ResponseEntity.status(404).body("does not exist");
        }
        return parseOrError(result);
    }

    private ResponseEntity parseOrError(Object result) {
        String json;
        try {
            json = objMapper.writeValueAsString(result);

        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(500).body("parsing error");
        }

        return ResponseEntity.ok(json);
    }

    @CrossOrigin
    @DeleteMapping("/api/file/{fileId}")
    public ResponseEntity deleteFile(@RequestHeader(HEADER_TOKEN) String token, @PathVariable String fileId) {
        String email = checkJwt(token);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        boolean success = mediaFileDAO.deleteMediaFile(email, fileId);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(404).body("file not found");
    }

    @CrossOrigin
    @PostMapping("/api/file/{fileId}")
    public ResponseEntity updateMetadata(@RequestHeader(HEADER_TOKEN)  String token, @PathVariable String fileId,
                                                                                        @RequestBody @Valid MetadataChange data) {
        String email = checkJwt(token);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        boolean success = mediaFileDAO.updateMediaFile(email, fileId, data);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(404).body("file not found");
    }

    @CrossOrigin
    @GetMapping(value = "/api/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@RequestHeader(HEADER_TOKEN)  String token, @RequestParam("name") String fileName) {
        String email = checkJwt(token);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        List<MediaFile> results = mediaFileDAO.getAllContaining(email, fileName);
        return parseOrError(results);
    }

    @CrossOrigin
    @GetMapping(value = "/api/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity uploadObject(@RequestHeader(HEADER_TOKEN)  String tokenJwt, @RequestParam("fileId") String fileName) {
        String email = checkJwt(tokenJwt);
        if(email.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (fileName.contains(" ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FileId cannot contain spaces");
        }
        if(mediaFileDAO.exists(email, fileName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FileId already exists");
        }

        mediaFileDAO.uploadMediaFile(email, fileName);

        if(mediaFileDAO.exists(email, fileName)){
            return ResponseEntity.status(400).build();
        }
        ResourceBundle bundle = ResourceBundle.getBundle("swift");
        try {
            URL url = new URL(bundle.getString("authURL"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Storage-User", bundle.getString("user"));
            con.setRequestProperty("X-Storage-Pass", bundle.getString("pass"));
            int status = con.getResponseCode();
            if (status != 200) {
                return ResponseEntity.status(status).build();
            }
            String token = con.getHeaderField("X-AUTH-TOKEN");
            String upload_url = con.getHeaderField("X-Storage-Url") + "/" + email + "/" + fileName;
            con.disconnect();
            UploadEndpoint uploadEndpoint = new UploadEndpoint(token, upload_url);
            return ResponseEntity.ok(uploadEndpoint.toJson());

        } catch (IOException ex) {
            return ResponseEntity.status(500).build();
        }
    }

}
