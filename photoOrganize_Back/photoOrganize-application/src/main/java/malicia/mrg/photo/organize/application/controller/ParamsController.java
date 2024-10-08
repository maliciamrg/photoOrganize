package malicia.mrg.photo.organize.application.controller;


import malicia.mrg.photo.organize.domain.api.IPhotoController;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/params")
public class ParamsController {
    private final IPhotoController photoController;

    public ParamsController(IParams parameter, IPhotoController photoController) {
        this.photoController = photoController;
    }

    @GetMapping("/application")
    public Object getParamsApplication() {
        return photoController.getParamsApplication();
    }


}