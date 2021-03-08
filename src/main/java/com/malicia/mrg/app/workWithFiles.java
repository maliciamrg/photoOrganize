package com.malicia.mrg.app;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.model.elementFichier;
import com.malicia.mrg.util.SystemFiles;
import com.malicia.mrg.util.UnzipUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

    public static void renameFile(String oldName, String newName , Database dbLr) throws IOException, SQLException {
        if (oldName.compareTo(newName) != 0) {

            SystemFiles.moveFile(oldName,newName);

            dbLr.renameFileLogique(oldName, newName);

        }
    }

    public static void moveFileintoFolder(elementFichier oldEle, String newPath , Database dbLr) throws IOException, SQLException {
        if (oldEle.getPath().compareTo(newPath) != 0) {

            workWithRepertory.sqlMkdirRepertory(new File(newPath).getParent() + File.separator,dbLr);

            SystemFiles.moveFile(oldEle.getPath(),newPath);

            dbLr.sqlmovefile(oldEle, newPath);

        }
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
        uZip.unzip(fichier.toString(),fichier.getParent());
    }
}
