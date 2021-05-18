package pik.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import pik.repository.mysqlDAOs.UserDAO;
import pik.repository.openstack.MediaFile;
import pik.repository.openstack.MediaFileDAO;
import pik.repository.openstack.SwiftMediaFileDAO;

import java.util.List;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    private static final LoginUsers loginUsers = new LoginUsers();
    private static final MediaFileDAO mediaFileDAO = new SwiftMediaFileDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String HEADER_TOKEN = "X-API-TOKEN";

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
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
        return ResponseEntity.ok("{\"token\":\""+token+"\"}");
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

    @CrossOrigin
    @GetMapping(value = "/api/{email}/image/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllImages(@PathVariable String email, @RequestHeader(HEADER_TOKEN) String token){

        if(loginUsers.checkUser(email, token) ){
            return getAll(email, "image");

        }
        return ResponseEntity.status(401).body("unauthorized");
    }

    private ResponseEntity getAll(String email, String type){
        List<MediaFile> all = mediaFileDAO.getAllByUserAndType(email, type);

        if(all == null) return ResponseEntity.ok("[]");
        if(all.size() == 0) return ResponseEntity.ok("[]");

        String json;
        try{
            json = objMapper.writeValueAsString(all);

        }catch(JsonProcessingException ex){
            return ResponseEntity.status(500).body("parsing error");
        }

        return ResponseEntity.ok(json);
    }
}


