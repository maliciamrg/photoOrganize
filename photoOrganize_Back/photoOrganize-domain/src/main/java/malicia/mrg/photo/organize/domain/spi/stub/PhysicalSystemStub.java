package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.LocalTime.now;

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

    @Override
    public FileTime getLastModifiedTime(Path filePath) {
        long milis = 0;
        try {
            milis = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse("01.01.2013 10:00:10").getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        FileTime fileTime = FileTime.fromMillis(milis);
        return fileTime;
    }

    @Override
    public String size(Path filePath) {
        return "200";
    }

    @Override
    public String getFileseparator() {
        return null;
    }

    @Override
    public String getFilegetParent(String newPath) {
        return null;
    }

    @Override
    public List<String> listRepertories(String rootPath, List<String> excludeSubdirectoryRejet, String searchFolder) {
        return null;
    }

}
