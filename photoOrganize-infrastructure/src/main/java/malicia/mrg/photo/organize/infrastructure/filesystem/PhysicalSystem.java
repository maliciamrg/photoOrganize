package malicia.mrg.photo.organize.infrastructure.filesystem;

import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;
import malicia.mrg.photo.organize.infrastructure.persistence.model.MasterTable;
import malicia.mrg.photo.organize.infrastructure.persistence.repository.MasterTableRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PhysicalSystem implements IPhysicalSystem {


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
    private static String normalizePath(String path) {
        return path.replace("\\", "/").replace("//", "/");
    }

}
