package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.dto.Analysis;

import java.util.List;

public interface IPoc {
    List<String> getPhysicalFilesNotLogic();

    Analysis analyseFilePhysiqueAndLogic();

    Analysis synchroDatabase();
    Analysis maintenanceDatabase();

}
