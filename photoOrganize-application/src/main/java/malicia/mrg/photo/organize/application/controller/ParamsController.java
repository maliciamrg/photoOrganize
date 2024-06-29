package malicia.mrg.photo.organize.application.controller;


import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/params")
public class ParamsController {
    private final IParams parameter;

    public ParamsController(IParams parameter) {
        this.parameter = parameter;
    }

    @GetMapping("/getparams")
    public Object getparams() {
        return parameter;
    }


}