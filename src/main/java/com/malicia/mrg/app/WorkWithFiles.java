package com.malicia.mrg.app;

import com.malicia.mrg.Main;
import com.malicia.mrg.util.UnzipUtility;

import com.malicia.mrg.util.WhereIAm;
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

    public static List<File> getFilesFromRepertoryWithFilter(String repertory, List<String> arrayFiltreDeNomDeSubdirectory, String extFileAExclure) {
        List<File> ret = new ArrayList<>();
        File[] files = new File(repertory).listFiles();
        showFiles(files, ret, arrayFiltreDeNomDeSubdirectory, extFileAExclure, "", true);
        return ret;
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

    public static String changeExtensionTo(String filename, String extension) {
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        filename += "." + extension;
        return filename;
    }

    public static void extractZipFile(File fichier) throws IOException {
        UnzipUtility uZip = new UnzipUtility();
        uZip.unzip(fichier.toString(), fichier.getParent());
    }

    public static List<String> getAllFiles(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet, JProgressBar progress) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {
            progress.setString(rootPath);
            File rootDirectory = new File(rootPath);

            // Make sure the root path is a directory
            if (rootDirectory.isDirectory()) {
                // List all files in the directory and its subdirectories
                List<String> files = listFiles(rootDirectory, allowedExtensions,excludeSubdirectoryRejet, progress);
                fileList.addAll(files);
            } else {
                System.out.println("Invalid directory: " + rootPath);
            }
        }
        progress.setString("");
        // Sort the file list alphabetically
        Collections.sort(fileList);

        return fileList;
    }

    private static List<String> listFiles(File directory, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet, JProgressBar progress) {
        List<String> fileList = new ArrayList<>();

        int numRow = 0;
        // List all files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {

                Main.visuProgress(progress, file.getParent(),numRow++,files.length);

                if (file.isDirectory()) {
                    if (!excludeSubdirectoryRejet.contains(file.getName())) {
                        // Recursively list files in subdirectories
                        List<String> subdirectoryFiles = listFiles(file, allowedExtensions, excludeSubdirectoryRejet, progress);
                        fileList.addAll(subdirectoryFiles);
                    }
                } else {
                    // Check if the file has an allowed extension
                    if (hasAllowedExtension(file, allowedExtensions)) {
                        // Add the file path to the list
                        fileList.add(normalizePath(file.getAbsolutePath()));
                    }
                }
            }
        }

        return fileList;
    }

    private static boolean hasAllowedExtension(File file, List<String> allowedExtensions) {
        for (String extension : allowedExtensions) {
            if (file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAllFiles2(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet, JProgressBar progress) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {
            progress.setString(rootPath);

            // Make sure the root path is a directory
            try {
                fileList.addAll(listFiles2(rootPath, allowedExtensions,excludeSubdirectoryRejet));
            } catch (IOException e) {
                System.out.println("Error due to: " + e.getMessage());
            }
        }
        progress.setString("");
        // Sort the file list alphabetically
        Collections.sort(fileList);

        return fileList;
    }


    private static List<String> listFiles2(String directoryPath, List<String> extension, List<String> excludeSubdirectoryRejet) throws IOException {
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
