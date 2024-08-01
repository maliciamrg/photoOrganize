package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.controller.dto.HexMeRequest;
import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.dto.Analysis;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/poc")
public class PocController {
    private final IPoc pocImpl;

    public PocController(IPoc pocImpl) {
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