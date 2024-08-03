package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.dto.Analysis;
import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;

import java.util.List;
import java.util.Map;

public interface IPhotoController {
    List<String> getPhysicalFilesNotLogic();

    Analysis analyseFilePhysiqueAndLogic();

    Analysis synchroDatabase();
    Analysis maintenanceDatabase();

    List<String> getSubDirectory(Integer rootFolderId);

    String getSubDirectory(Integer rootFolderId, Integer folderId);

    List<Map<String, String>> getArrayRepertoirePhotoRepertoire();

    ElementRootFolder getArrayRepertoirePhotoRepertoire(Integer rootFolderId);

    Object getParamsApplication();
}
