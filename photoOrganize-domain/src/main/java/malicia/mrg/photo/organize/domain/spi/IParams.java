package malicia.mrg.photo.organize.domain.spi;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface IParams {

    String getFolderdelim();
    long getGmt01jan200112am();

    List<String> getAllowedExtensions();

    List<String> getSubdirectoryRejet();

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
}
