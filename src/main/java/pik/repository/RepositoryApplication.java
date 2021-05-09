package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@RestController
@SpringBootApplication
public class RepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @RequestMapping("/")
    public String hello(){
        return "Hello!";
    }

    @RequestMapping(value="/api/login", method=RequestMethod.POST)
    public ResponseEntity login(@RequestBody @Valid Login dane){
        String email = dane.getEmail();
        String password = dane.getPassword();
        User user = new User(email, password, "Alicja", "Turowska");
        user.setToken("token");
        //obsluga logowania

        return ResponseEntity.ok("{\"token\":\""+user.getToken()+"\"}");
    }

    @RequestMapping(value="/api/signup", method=RequestMethod.POST)
    public ResponseEntity register(@RequestBody @Valid Register dane){
        //obsluga rejestracji
        return ResponseEntity.ok("");
    }
}


