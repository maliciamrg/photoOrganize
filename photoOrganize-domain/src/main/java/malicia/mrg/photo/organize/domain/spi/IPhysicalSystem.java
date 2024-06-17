package malicia.mrg.photo.organize.domain.spi;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPhysicalSystem {
    Collection<String> listFiles(String rootPath, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet);
}
