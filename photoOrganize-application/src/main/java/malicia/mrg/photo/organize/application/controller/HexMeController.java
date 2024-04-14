package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.controller.dto.HexMeRequest;
import malicia.mrg.photo.organize.domain.HexMeImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/hexMe")
public class HexMeController {
    private final HexMeImpl hexMeImpl = new HexMeImpl();

    @PostMapping
    public ResponseEntity<String> testHexMe(@RequestBody HexMeRequest inputHexMe) throws URISyntaxException {
        return ok(hexMeImpl.getMsgReturn(inputHexMe.message));
    }


}