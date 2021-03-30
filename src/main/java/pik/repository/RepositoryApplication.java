package pik.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class RepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @RequestMapping("/")
    public String hello() {
        return "Hello Word!";
    }
    @RequestMapping("/hello")
    public String hello2() {
        return "Hello2 Word!";
    }
}


