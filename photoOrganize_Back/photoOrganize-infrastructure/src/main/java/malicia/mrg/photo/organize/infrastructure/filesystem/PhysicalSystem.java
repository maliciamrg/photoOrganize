package malicia.mrg.photo.organize.infrastructure.filesystem;

import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PhysicalSystem implements IPhysicalSystem {
    private static final Logger logger = LoggerFactory.getLogger(PhysicalSystem.class);

    private static String normalizePath(String path) {
        return path.replace("\\", "/").replace("//", "/");
    }

    @Override
    public Collection<String> listFiles(String rootPath, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet) {

        Path start = Paths.get(rootPath);
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            return stream
                    .map(name -> normalizePath(name.toString()))//.toLowerCase())
                    .filter(fileName -> {
                        return allowedExtensions.contains(FilenameUtils.getExtension(fileName).toLowerCase());
                    })
                    .filter(fileName -> {
                        // Assuming subfolder name is just before the file name in the path
                        Path parent = Paths.get(fileName).getParent();
                        return parent != null && !excludeSubdirectoryRejet.contains(parent.getFileName().toString());
                    })
//                    .filter(Files::isRegularFile)   // is a file
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return null;
    }

    @Override
    public String size(Path filePath) {
        return null;
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
        logger.info(rootPath);
        Path directory = Paths.get(rootPath); // Specify the path to your directory

        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(Files::isDirectory) // Filter only directories
                    .map(name -> normalizePath(name.toString()))//.toLowerCase())
                    .filter(fileName -> {
                        // Assuming subfolder name is just before the file name in the path
                        Path parent = Paths.get(fileName).getParent();
                        return parent != null && !excludeSubdirectoryRejet.contains(parent.getFileName().toString());
                    })
                    .filter(fileName -> {
                        String name = Paths.get(fileName).getFileName().toString();
                        return searchFolder == "" || name.compareTo(searchFolder)==0;
                    })
//                    .forEach(System.out::println)// Print each subdirectory
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
