package malicia.mrg.photo.organize.domain.spi;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface IParams {
    List<String> getAllowedExtensions();

    List<String> getSubdirectoryRejet();

    List<String> getExtensionFileRejetSup();

    List<String> getNomSubdirectoryRejet();

    String getExtFileRejet();

    String getRepertoire(String rejet);

    String getRepertoire50Phototheque();

    List<String> getExtensionsUseFile();
}
