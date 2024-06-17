package malicia.mrg.photo.organize.domain.spi;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ILogicalSystem {

    List<String> getAllRootPathsLogiques();

    Collection<String> getAllFilesLogiques(String rootPath);
}
