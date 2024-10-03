package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.dto.Analysis;
import malicia.mrg.photo.organize.domain.dto.ElementPhotoFolder;
import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;

import java.util.List;

public interface IPhotoController {
    List<String> getPhysicalFilesNotLogic();

    Analysis analyseFilePhysiqueAndLogic();

    Analysis synchroDatabase();
    Analysis maintenanceDatabase();

    List<String> getSubDirectories(String rootFolderId);

    ElementPhotoFolder getSubDirectory(String rootFolderId, String folderId);

    List<ElementRootFolder> getArrayRepertoirePhotoRepertoire();

    ElementRootFolder getArrayRepertoirePhotoRepertoire(Integer rootFolderId);

    Object getParamsApplication();

}
