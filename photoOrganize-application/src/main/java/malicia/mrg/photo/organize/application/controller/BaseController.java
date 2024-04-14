package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.domain.HexMeImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class BaseController {


    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}