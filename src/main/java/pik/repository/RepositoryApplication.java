package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import pik.repository.openstack.MediaFile;
import pik.repository.openstack.MediaFileDAO;
import pik.repository.openstack.SwiftMediaFileDAO;

import java.util.List;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    private LoginUsers loginUsers;

    private static final MediaFileDAO mediaFileDAO = new SwiftMediaFileDAO();

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @CrossOrigin
    @RequestMapping(value="/api/login", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody @Valid Login dane){
        //tego nie będzie
        String email = dane.getEmail();
        String password = dane.getPassword();
        String token = "token";
        //obsluga logowania:

        //odczytać z bazy czy jest użytkownik o podanym emailu i haśle

        //jeśli jest to odkomentować to co jest niżej
        //String token = loginUsers.addUser(email);

        return ResponseEntity.ok("{\"token\":\""+token+"\"}");
    }

    @CrossOrigin
    @RequestMapping(value="/api/signup", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody @Valid UserDate dane){
        //obsluga rejestracji: sprzwdzić czy istnieje, jeśli nie to dodać
        return ResponseEntity.ok("");
    }

    @CrossOrigin
    //@RequestMapping(value = "/api/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/api/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllImages(@PathVariable String userId){
        String token;       //get token from http header
                            //get user based on token
        String userFromToken = "1";
        if(userId.equals(userFromToken)){
            List<MediaFile> all = mediaFileDAO.getAllUserFiles(userId);
            if(all.size()> 0){
                String json = all.get(0).toJson();
                return ResponseEntity.ok(json);
            }
            return ResponseEntity.ok("{\"success\":\"ok\"}");
        }

        return ResponseEntity.ok("{\"success\":\"" + userId + "\"}");
    }
}


