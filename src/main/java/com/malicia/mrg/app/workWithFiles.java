package com.malicia.mrg.app;

import com.malicia.mrg.data.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class workWithFiles {

    public static List<File> getFilesFromRepertoryWithFilter(String Repertory, List<String> arrayFiltreDeNomDeSubdirectory, String extFileFilter) {
        ObservableList<File> ret = FXCollections.observableArrayList();
        File[] files = new File(Repertory).listFiles();
        showFiles(files, ret, arrayFiltreDeNomDeSubdirectory, extFileFilter, true);
        return ret;
    }

    private static void showFiles(File[] files, List<File> fileRetour, List<String> arrayFiltreRep, String extFileFilter, boolean onlyRepIn) {
        for (File file : files) {
            if (file.isDirectory()) {
                boolean onlyRepOut = true;
                if (stringContainsItemFromList(file.toString(), arrayFiltreRep.toArray(new String[0]))) {
                    onlyRepOut = false;
                }
                showFiles(file.listFiles(), fileRetour, arrayFiltreRep, extFileFilter, onlyRepOut); // Calls same method again.
            } else {
                if (!onlyRepIn) {
                    if (FilenameUtils.getExtension(file.getName()).toLowerCase().compareTo(extFileFilter) != 0) {
                        fileRetour.add(file);
                    }
                }
            }
        }
    }

    private static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static void renameFile(String oldName, String newName) throws IOException {
        if (oldName.compareTo(newName) != 0) {
            System.out.println("oldName:" + oldName);
            System.out.println("newName:" + newName);

            // File (or directory) with old name
            File file = new File(oldName);
            // File (or directory) with new name
            File file2 = new File(newName);

            if (file2.exists()) {
                throw new java.io.IOException("file exists");
            }

            // Rename file (or directory)
            boolean success = file.renameTo(file2);
            if (!success) {
                // File was not successfully renamed
                throw new java.io.IOException("file was not successfully renamed");
            }
        }
    }

    public static String changeExtensionTo(String filename, String extension) {
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        filename += "." + extension;
        return filename;
    }

    public static void extractZipFile(File fichier) {
        //TODO
    }
}
