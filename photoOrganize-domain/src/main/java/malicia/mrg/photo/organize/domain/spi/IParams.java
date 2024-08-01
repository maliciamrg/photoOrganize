package malicia.mrg.photo.organize.domain.spi;

import java.util.List;
import java.util.Map;

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

    List<Map<String,String>> getArrayRepertoirePhotoRepertoire();

    Object getArrayRepertoirePhotoRepertoire(Integer rootFolderNum);
}
