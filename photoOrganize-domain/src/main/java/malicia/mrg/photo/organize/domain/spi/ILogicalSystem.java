package malicia.mrg.photo.organize.domain.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILogicalSystem {

    List<String> getAllRootPathsLogiques();

    Collection<String> getAllFilesLogiques(String rootPath);

    int sqlmovefile(long scrFileIdLocal, String newPath);

    String sqlGetSidecarExtensions(long scrFileIdLocal);

    Map<String, Long> getIdlocalforFilePath(String source);

    String getFileByHash(java.lang.String s);

    void makeRepertory(String directoryName);
}
