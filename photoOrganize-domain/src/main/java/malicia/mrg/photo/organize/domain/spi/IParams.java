package malicia.mrg.photo.organize.domain.spi;

import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;

import java.util.List;

public interface IParams {

    String getFolderdelim();

    long getGmt01jan200112am();

    List<String> getAllowed_extensions();

    List<String> getExclude_subdirectory_reject();

    List<String> getExtensionFileRejetSup();

    List<String> getNomSubdirectoryRejet();

    String getExtFileRejet();

    String getRepertoire(String rejet);

    String getRepertoire50Phototheque();

    List<String> getExtensionsUseFile();

    String getTag_action_go_rapprochement();

    String getTag_action_go();

    String gettag_rapprochement();

    String getTag_org();

    String getCollections();

    List<ElementRootFolder> getArrayRepertoirePhotoNmUnique();
    
    ElementRootFolder getArrayRepertoirePhotoNmUnique(Integer rootFolderNum);

    String getRootFolder();

    ElementRootFolder getArrayRepertoirePhotoNmUnique(String rootFolder);
}
