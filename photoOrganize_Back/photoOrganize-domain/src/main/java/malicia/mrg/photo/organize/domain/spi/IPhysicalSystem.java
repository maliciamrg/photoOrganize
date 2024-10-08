package malicia.mrg.photo.organize.domain.spi;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.List;

public interface IPhysicalSystem {
    Collection<String> listFiles(String rootPath, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet);

    int movePhysique(String modifiedSource, String modifiedNewPath);

    void extractZipFile(File file);

    void mkdir(String directoryName);

    FileTime getLastModifiedTime(Path filePath);

    String size(Path filePath);

    String getFileseparator();

    String getFilegetParent(String newPath);

    List<String> listRepertories(String rootPath, List<String> excludeSubdirectoryRejet , String searchFolder);
}
