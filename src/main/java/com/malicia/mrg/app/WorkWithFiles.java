package com.malicia.mrg.app;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.model.ElementFichier;
import com.malicia.mrg.util.SystemFiles;
import com.malicia.mrg.util.UnzipUtility;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.malicia.mrg.util.SystemFiles.normalizePath;

public class WorkWithFiles {

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

    public static void renameFile(String oldName, String newName, Database dbLr) throws IOException, SQLException {
        if (normalizePath(oldName).compareTo(normalizePath(newName)) != 0) {

            WorkWithRepertory.sqlMkdirRepertory(new File(newName).getParent() + File.separator, dbLr);

            SystemFiles.moveFile(oldName, newName);

            dbLr.renameFileLogique(oldName, newName);

        }
    }

    public static void moveFileintoFolder(ElementFichier oldEle, String newPath, Database dbLr) throws IOException, SQLException {
        if (normalizePath(oldEle.getPath()).compareTo(normalizePath(newPath)) != 0) {

            File fsrc = new File(oldEle.getPath());
            if (fsrc.exists()) {
                File fdest = new File(newPath);
                if (fdest.exists()) {
                    newPath = fdest.getParent() + "\\" + Math.floor(Math.random() * (100 - 0 + 1) + 0) + fdest.getName();
                }

                WorkWithRepertory.sqlMkdirRepertory(new File(newPath).getParent() + File.separator, dbLr);

                SystemFiles.moveFile(oldEle.getPath(), newPath);

                dbLr.sqlmovefile(oldEle, newPath);
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

    public static void extractZipFile(File fichier) throws IOException {
        UnzipUtility uZip = new UnzipUtility();
        uZip.unzip(fichier.toString(), fichier.getParent());
    }

}
