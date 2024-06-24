package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Stub
public class PhysicalSystemStub implements IPhysicalSystem {

    @Override
    public Collection<String> listFiles(String rootPath, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet) {
        List<String> fileList = new ArrayList<>();
        if (rootPath=="root1") {
            fileList.add("file1");
            fileList.add("file3");
        }
        if (rootPath=="root2") {
            fileList.add("fileZZZ22");
            fileList.add("fileazertyuiop12");
            fileList.add("afoo01");
        }
        return fileList;
    }

    @Override
    public int movePhysique(String modifiedSource, String modifiedNewPath) {
        return 0;
    }

    @Override
    public void extractZipFile(File file) {

    }

    @Override
    public void mkdir(String directoryName) {

    }

}
