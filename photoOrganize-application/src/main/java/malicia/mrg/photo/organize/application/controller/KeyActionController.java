package malicia.mrg.photo.organize.application.controller;


import malicia.mrg.photo.organize.domain.api.IKeyAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keyact")
public class KeyActionController {
    private final IKeyAction keyActionImpl;

    public KeyActionController(IKeyAction keyActionImpl) {
        this.keyActionImpl = keyActionImpl;
    }

    @PostMapping("/putMakeActionRapprochement")
    public Object putMakeActionRapprochement() {
        return keyActionImpl.makeActionRapprochement();
    }

    @PostMapping("/putMakeActionMoveToRepertory")
    public Object putMakeActionMoveToRepertory() {
        return keyActionImpl.makeActionMoveToRepertory();
    }


}