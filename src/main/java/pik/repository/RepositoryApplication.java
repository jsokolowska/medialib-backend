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
    public User login(@RequestBody Login dane) {
        User uzytkownik = new User();
        uzytkownik.email = dane.email;
        uzytkownik.hash = dane.hash;
        return uzytkownik;
    }
}


