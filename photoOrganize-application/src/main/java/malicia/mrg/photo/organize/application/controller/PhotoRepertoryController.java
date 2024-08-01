package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prc")
public class PhotoRepertoryController {
    private final IParams parameter;

    public PhotoRepertoryController(IParams parameter) {
        this.parameter = parameter;
    }
    @GetMapping("/repertories")
    public List<Map<String,String>> getRepertories() {
        return parameter.getArrayRepertoirePhotoRepertoire();
    }
    @GetMapping("/repertory")
    public Object getRepertories(@RequestParam Integer rootFolderNum) {
        return parameter.getArrayRepertoirePhotoRepertoire(rootFolderNum);
    }


}