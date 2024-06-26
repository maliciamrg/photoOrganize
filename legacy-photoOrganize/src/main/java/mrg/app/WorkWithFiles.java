package mrg.app;

import mrg.TimeTracker;
import com.malicia.mrg.util.UnzipUtility;

import com.malicia.mrg.util.WhereIAm;
import mrg.Main;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.malicia.mrg.util.SystemFiles.normalizePath;

public class WorkWithFiles {
    private static final Logger LOGGER = LogManager.getLogger(WorkWithFiles.class);


    private WorkWithFiles() {
        throw new IllegalStateException("Utility class");
    }

    // a partir d'un chemin
    //  - recupere tout les fihchier contenue dans les sous repertoire "arraySubDirectoryToIclude"
    //  - qui non pas comme extension extFileToExclude
    public static List<File> getFilesFromRepertoryWithFilter(String repertoryToSearch, List<String> arraySubDirectoryToInclude, List<String> arraySubDirectoryToExclude, String extFileToExclude) throws IOException {
        TimeTracker.startTimer(Thread.currentThread().getStackTrace()[1].getMethodName(), Main.LOGGER_TO_TIME);

        String[] arraySubDirectoryToIncludeArray = arraySubDirectoryToInclude.toArray(new String[0]);
        String[] arraySubDirectoryToExcludeArray = arraySubDirectoryToExclude.toArray(new String[0]);
        List<Path> filesOutList = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(repertoryToSearch))) {
            filesOutList = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(extFileToExclude))
                    .filter(p -> Arrays.stream(arraySubDirectoryToIncludeArray).anyMatch(p.getParent().toString()::contains))
                    .filter(p -> Arrays.stream(arraySubDirectoryToExcludeArray).noneMatch(p.getParent().toString()::contains))
                    .collect(Collectors.toList());
        }

        // Convert List<Path> to List<File>
        List<File> fileList = filesOutList.stream()
                .map(Path::toFile)
                .collect(Collectors.toList());

        TimeTracker.endTimer(Thread.currentThread().getStackTrace()[1].getMethodName(), Main.LOGGER_TO_TIME);
        return fileList;
    }

    public static List<File> getFilesFromRepertoryWithFilter(String repertory, String extFileAInclure) {
        List<File> ret = new ArrayList<>();
        File[] files = new File(repertory).listFiles();
        showFiles(files, ret, new ArrayList<>(), "", extFileAInclure, false);
        return ret;
    }

    private static void showFiles(File[] files, List<File> fileRetour, List<String> arrayFiltreRep, String extFileAExclure, String extFileAInclure, boolean onlyRepIn) {
        for (File file : files) {
            if (file.isDirectory()) {
                boolean onlyRepOut = !stringContainsItemFromList(file.toString().toLowerCase(Locale.ROOT), arrayFiltreRep.toArray(new String[0]));
                showFiles(file.listFiles(), fileRetour, arrayFiltreRep, extFileAExclure, extFileAInclure, onlyRepOut); // Calls same method again.
            } else {
                if (
                        (
                                !onlyRepIn &&
                                        (FilenameUtils.getExtension(file.getName()).toLowerCase().compareTo(extFileAExclure)) != 0
                        )
                                &&
                                (
                                        !onlyRepIn &&
                                                (extFileAInclure.compareTo("") == 0 || FilenameUtils.getExtension(file.getName()).toLowerCase().compareTo(extFileAInclure) == 0)
                                )
                )
                {
                    fileRetour.add(file);
                }
            }
        }
    }

    private static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static void extractZipFile(File fichier) throws IOException {
        UnzipUtility uZip = new UnzipUtility();
        uZip.unzip(fichier.toString(), fichier.getParent());
    }

    public static List<String> getAllPhysicalFiles(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet, JProgressBar progress) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {
            progress.setString(rootPath);

            // Make sure the root path is a directory
            try {
                fileList.addAll(listFiles(rootPath, allowedExtensions,excludeSubdirectoryRejet));
            } catch (IOException e) {
                System.out.println("Error due to: " + e.getMessage());
            }
        }
        progress.setString("");
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
}
