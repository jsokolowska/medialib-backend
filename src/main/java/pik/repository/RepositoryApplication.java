package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    private LoginUsers loginUsers;

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @RequestMapping("/")
    public String hello(){
        return "Hello!";
    }

    @RequestMapping(value="/api/login", method=RequestMethod.POST)
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

    @RequestMapping(value="/api/signup", method=RequestMethod.POST)
    public ResponseEntity register(@RequestBody @Valid UserDate dane){
        //obsluga rejestracji - sprawdzenie czy w bazie już istnieje, jeśli nie to dodać
        return ResponseEntity.ok("");
    }
}


