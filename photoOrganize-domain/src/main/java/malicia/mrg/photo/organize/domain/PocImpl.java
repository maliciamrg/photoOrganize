package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.IHexMe;
import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.ddd.DomainService;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DomainService
public class PocImpl implements IPoc {
    private CharSequence defautValue = "hex?";
    private String ok = "Yes_Hex";
    private String notOk = "Not_Yet_Hex";
    private String write = "write?";
    private String hexWrite = "Hex_write";
    private static final Logger LOGGER_TO_SYNC_PH_FILE = LogManager.getLogger("loggerToSyncPhFile");

    List<String> listFilesPh = new ArrayList<>();
    List<String> listFilesLog = new ArrayList<>();
    List<String> listFilesNotInLog = new ArrayList<>();
    private Params params;
    private IWritePersistence writePersistence;

    public PocImpl(IWritePersistence writePersistence) {
        this.writePersistence = writePersistence;
    }

    private List<String> getAllRootPathsLogiques() {
        List<String> fileList = new ArrayList<>();
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    private List<String> getAllFilesLogiques() {
        List<String> fileList = new ArrayList<>();
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    public List<String> getAllPhysicalFiles(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet){
        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {

            // Make sure the root path is a directory
            try {
                fileList.addAll(listFiles(rootPath, allowedExtensions,excludeSubdirectoryRejet));
            } catch (IOException e) {
                System.out.println("Error due to: " + e.getMessage());
            }
        }
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }


    private static List<String> listFiles(String directoryPath, List<String> extension, List<String> excludeSubdirectoryRejet) throws IOException {
        Path start = Paths.get(directoryPath);
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            return stream
                    .map(name -> normalizePath(name.toString()))//.toLowerCase())
                    .filter(fileName -> {
                        return extension.contains( FilenameUtils.getExtension(fileName).toLowerCase());
                    })
                    .filter(fileName -> {
                        // Assuming subfolder name is just before the file name in the path
                        Path parent = Paths.get(fileName).getParent();
                        return parent != null && !excludeSubdirectoryRejet.contains(parent.getFileName().toString());
                    })
//                    .filter(Files::isRegularFile)   // is a file
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static String normalizePath(String path) {
        return path.replace("\\", "/").replace("//", "/");
    }

    private void generateListFilesNotInLog(List<String> listFilesPh,List<String> listFilesLog){
        int koPhy = 0;
        int koLog = 0;

//        int posLog = 0;
        int nbLog = listFilesLog.size();

//        int posPhy = 0;
        int nbPhy = listFilesPh.size();

        LOGGER_TO_SYNC_PH_FILE.info("Differences between listFilesPh and listFilesLog:");

        Iterator<String> iterPh = listFilesPh.listIterator();
        Iterator<String> iterLo = listFilesLog.listIterator();

        String elemPh = iterPh.hasNext() ? iterPh.next() : null;
        String elemLo = iterLo.hasNext() ? iterLo.next() : null;

        while (elemPh != null || elemLo != null) {
            int compareResult;

            if (elemPh == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemLo == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemPh.compareTo(elemLo);
            }

            if (compareResult < 0) {
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesLog - " + elemPh + "");
                listFilesNotInLog.add(elemPh);
                koPhy++;
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
            } else if (compareResult > 0) {
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesPh  - " + elemLo + " ");
                koLog++;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            }
        }
    }

    @Override
    public List<String> getPhysicalFilesNotLogic() {
        List<String> fileList = new ArrayList<>();

        List<String> rootPaths = getAllRootPathsLogiques();
        List<String> allowedExtensions = params.getAllowedExtensions();
        List<String> excludeSubdirectoryRejet = params.getSubdirectoryRejet();

        listFilesPh = getAllPhysicalFiles(rootPaths,allowedExtensions,excludeSubdirectoryRejet);
        listFilesLog = getAllFilesLogiques();

        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    @Override
    public String getMsgReturn(String msg) {
        if (msg.contains(write)) {
            UUID uuid_persistence = writePersistence.write(msg);
            return hexWrite + uuid_persistence.toString();
        }
        if (msg.contains(defautValue)) {
            return ok;
        }
        return notOk;
    }
}
