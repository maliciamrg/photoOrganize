package malicia.mrg.photo.organize.application.controller.dto;

import malicia.mrg.photo.organize.domain.api.IPhotoController;
import malicia.mrg.photo.organize.domain.dto.Analysis;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/poc")
public class PocController {
    private final IPhotoController pocImpl;

    public PocController(IPhotoController pocImpl) {
        this.pocImpl = pocImpl;
    }

    @PostMapping("/getFilesNotLogic")
    public List<String> getFilesNotLogic() {
        return pocImpl.getPhysicalFilesNotLogic();
    }

    @PostMapping("/getAnalyseResult")
    public Analysis getAnalyseResult() {
        return pocImpl.analyseFilePhysiqueAndLogic();
    }

    @PostMapping("/putSynchroDatabase")
    public Object putSynchroDatabase() { return pocImpl.synchroDatabase(); }

    @PostMapping("/putMaintenanceDatabase")
    public Object putMaintenanceDatabase() { return pocImpl.maintenanceDatabase(); }

}