package malicia.mrg.photo.organize.infrastructure.filesystem;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogicalSystem implements ILogicalSystem {


    @Override
    public List<String> getAllRootPathsLogiques() {
        return null;
    }

    @Override
    public Collection<String> getAllFilesLogiques(String rootPath) {
        return null;
    }

    @Override
    public int sqlmovefile(long scrFileIdLocal, String newPath) {
        return 0;
    }

    @Override
    public String sqlGetSidecarExtensions(long scrFileIdLocal) {
        return null;
    }

    @Override
    public Map<String, Long> getIdlocalforFilePath(String source) {
        return null;
    }

    @Override
    public String getFileByHash(String s) {
        return null;
    }

    @Override
    public void makeRepertory(String directoryName) {

    }
}
