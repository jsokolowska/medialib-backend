package pik.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import pik.repository.mysqlDAOs.UserDAO;
import pik.repository.openstack.MediaFile;
import pik.repository.openstack.MediaFileDAO;
import pik.repository.openstack.SwiftMediaFileDAO;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    @Autowired
    public LoginUsers loginUsers;
    private static final MediaFileDAO mediaFileDAO = new SwiftMediaFileDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String HEADER_LOGIN = "LOGIN";
    private static final String KEY = "pikKey";

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new JWTFilter(KEY));
        ArrayList<String> urls = new ArrayList<>();
        //dodanie adresów url, które są dostępne dopiero po zalogowaniu
        urls.add("/hello");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }

    @CrossOrigin
    @RequestMapping(value="/api/login", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody @Valid Login dane){
        String email = dane.getEmail();
        String password = dane.getPassword();
        if(!userDAO.isUserExist(email)){
            return ResponseEntity.notFound().build();
        }
        if(!userDAO.isPasswordMatch(email, password)){
            return ResponseEntity.status(401).body("error password");
        }
        String token = loginUsers.addUser(email);
        return ResponseEntity.ok(Jwts.builder().setSubject(email).claim("token", token).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1200000)).signWith(SignatureAlgorithm.HS512, KEY).compact());
    }

    @CrossOrigin
    @RequestMapping(value="/api/signup", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody @Valid UserData dane){
        if(userDAO.isUserExist(dane.getEmail())){
            return ResponseEntity.status(401).body("just exist");
        }
        userDAO.insertUser(dane.getName(), dane.getSurname(), dane.getEmail(), dane.getPassword());
        return ResponseEntity.ok("");
    }
/*
    @CrossOrigin
    @GetMapping(value = "api/{email}/all", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@PathVariable String email, @RequestParam("type") String type, @RequestHeader(HEADER_TOKEN) String token){
        if(!type.equals("any") && !type.equals("image") && !type.equals("video")){
            System.out.print ("Type: " + type  + type.getClass());
            return ResponseEntity.status(400).body("Wrong type");
        }
        if(loginUsers.checkUser(email, token) ){
            List<MediaFile> all = mediaFileDAO.getAllByUserAndType(email, type);

            if(all == null) return ResponseEntity.ok("[]");
            if(all.size() == 0) return ResponseEntity.ok("[]");

            return parseOrError(all);

        }
        return ResponseEntity.status(401).body("unauthorized");
    }


//    private ResponseEntity getAll(String email, String type){
//        List<MediaFile> all = mediaFileDAO.getAllByUserAndType(email, type);
//
//        if(all == null) return ResponseEntity.ok("[]");
//        if(all.size() == 0) return ResponseEntity.ok("[]");
//
//        return parseOrError(all);
//    }

    @CrossOrigin
    @GetMapping(value = "/api/{email}/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getOneById(@PathVariable String email, @PathVariable String fileId, @RequestHeader(HEADER_TOKEN) String token){

        if(loginUsers.checkUser(email, token) ){
            return getOne(email, fileId, null);

        }
        return ResponseEntity.status(401).body("unauthorized");
    }

    private ResponseEntity getOne(String email, String fileId, String displayName){
        MediaFile result;
        if(fileId!=null){
            result = mediaFileDAO.getMediaFile(email, fileId);
        }else if(displayName != null){
            result = mediaFileDAO.getMediaFileByDisplayName(email, displayName);
        }else{
            return ResponseEntity.status(400).body("File id or display name must be provided");
        }
        if(result == null){
            return ResponseEntity.status(404).body("does not exist");
        }
        return parseOrError(result);
    }

    private ResponseEntity parseOrError(Object result){
        String json;
        try{
            json = objMapper.writeValueAsString(result);

        }catch(JsonProcessingException ex){
            return ResponseEntity.status(500).body("parsing error");
        }

        return ResponseEntity.ok(json);
    }

    @CrossOrigin
    @DeleteMapping("/api/{email}/{fileId}")
    public ResponseEntity deleteFile (@PathVariable String email, @PathVariable String fileId, @RequestHeader(HEADER_TOKEN) String token){
        if(loginUsers.checkUser(email, token) ){
            boolean success = mediaFileDAO.deleteMediaFile(email, fileId);
            if(success){
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(404).body("file not found");

        }
        return ResponseEntity.status(401).body("unauthorized");
    }
*/
}


