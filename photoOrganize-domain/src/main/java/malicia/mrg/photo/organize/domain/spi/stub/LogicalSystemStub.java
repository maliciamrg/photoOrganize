package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Stub
public class LogicalSystemStub implements ILogicalSystem {

    @Override
    public List<String> getAllRootPathsLogiques() {
        List<String> fileList = new ArrayList<>();
        fileList.add("root1");
        fileList.add("root2");
        return fileList;
    }

    @Override
    public Collection<String> getAllFilesLogiques(String rootPath) {
        List<String> fileList = new ArrayList<>();
        if (rootPath=="root1") {
            fileList.add("file1");
            fileList.add("file2");
        }
        if (rootPath=="root2") {
            fileList.add("fileZZZ22");
            fileList.add("fileazertyuiop12");
        }
        return fileList;
    }
}
