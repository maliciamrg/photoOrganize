package malicia.mrg.photo.organize.domain.spi;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPhysicalSystem {
    Collection<String> listFiles(String rootPath, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet);

    int movePhysique(String modifiedSource, String modifiedNewPath);

    void extractZipFile(File file);

    void mkdir(String directoryName);
}
