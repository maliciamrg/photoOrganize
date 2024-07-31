package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prc")
public class PhotoRepertoryController {
    private final IParams parameter;

    public PhotoRepertoryController(IParams parameter) {
        this.parameter = parameter;
    }
    @GetMapping("/repertories")
    public List<String> getRepertories() {
        return parameter.getArrayRepertoirePhotoRepertoire();
    }


}