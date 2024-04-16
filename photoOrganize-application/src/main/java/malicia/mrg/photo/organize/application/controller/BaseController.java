package malicia.mrg.photo.organize.application.controller;

import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class BaseController {


    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}