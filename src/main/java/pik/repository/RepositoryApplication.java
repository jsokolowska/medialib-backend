package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.http.MediaType;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    private LoginUsers loginUsers;

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
}


