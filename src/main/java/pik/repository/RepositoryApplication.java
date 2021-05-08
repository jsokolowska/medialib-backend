package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@SpringBootApplication
public class RepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @RequestMapping(value="/api/login", method=RequestMethod.POST)
    public String login(@RequestBody Login dane) {
        String email = dane.getEmail();
        String password = dane.getPassword();
        User user = new User(email, password, "Alicja", "Turowska");
        user.setToken("token");
        return "{token:"+user.getToken()+"}";
    }
}


