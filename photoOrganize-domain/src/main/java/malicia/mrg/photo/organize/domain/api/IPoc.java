package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.Analysis;

import java.util.List;

public interface IPoc {
    List<String> getPhysicalFilesNotLogic();

    Analysis analyseFileFPhysiqueAndLogic();

}
