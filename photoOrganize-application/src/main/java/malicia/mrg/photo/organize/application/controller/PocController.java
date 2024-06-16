package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.controller.dto.HexMeRequest;
import malicia.mrg.photo.organize.domain.api.IHexMe;
import malicia.mrg.photo.organize.domain.api.IPoc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/poc")
public class PocController {
    private final IPoc pocImpl;

    public PocController(IPoc pocImpl) {
        this.pocImpl = pocImpl;
    }

    @PostMapping
    public ResponseEntity<String> testHexMe(@RequestBody HexMeRequest inputHexMe) throws URISyntaxException {
        return ok(pocImpl.getMsgReturn(inputHexMe.message));
    }


}