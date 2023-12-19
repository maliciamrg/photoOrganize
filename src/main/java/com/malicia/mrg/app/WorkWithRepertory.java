package com.malicia.mrg.app;

import com.malicia.mrg.Main;
import com.malicia.mrg.TimeTracker;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.electx.RepertoirePhoto;
import com.malicia.mrg.util.SystemFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.malicia.mrg.Main.LOGGER_TO_TIME;

public class WorkWithRepertory {

    private static final Logger LOGGER = LogManager.getLogger(WorkWithRepertory.class);

    private WorkWithRepertory() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean deleteEmptyRep(File directory, JProgressBar progress) throws IOException {
        TimeTracker.startTimer(Thread.currentThread().getStackTrace()[1].getMethodName() + " -- " + directory, LOGGER_TO_TIME);

        FileFilter fileFilter = new FileFilter(){
            public boolean accept(File dir) {
                if (dir.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        // Check if the directory is empty
        if (directory.isDirectory() && directory.list().length == 0) {
            LOGGER.debug("Deleting empty directory: " + directory.getAbsolutePath());
            if (directory.delete()) {
                LOGGER.debug("Directory deleted successfully.");
            } else {
                LOGGER.debug("Failed to delete the directory.");
            }
        } else {
            // If not empty, iterate over its contents
            File[] subDirectories = directory.listFiles(fileFilter);

            if (subDirectories != null) {
                int j = 0;
                for (File subDirectory : subDirectories) {
                    j++;
                    Main.visuProgress(progress,directory.toString(),j,subDirectories.length);
                    // Recursive call for subdirectories
                    deleteEmptyRep(subDirectory,progress);
                }
            }
        }
        TimeTracker.endTimer(Thread.currentThread().getStackTrace()[1].getMethodName() + " -- " + directory, LOGGER_TO_TIME);

        return true;
    }
    public static boolean deleteEmptyRep_old(String fileLocation, JProgressBar progress) throws IOException {
        FileFilter fileFilter = new FileFilter(){
            public boolean accept(File dir) {
                if (dir.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        boolean isFinished = true;
        File folder = new File(fileLocation);
        File[] listFiles = folder.listFiles();
        if (listFiles.length == 0) {
            LOGGER.debug("Folder Name :: {} is deleted.",  folder.getAbsolutePath() );
            Files.delete(folder.toPath());
            isFinished = false;
        } else {
            listFiles = folder.listFiles(fileFilter);
            for (int j = 0; j < listFiles.length; j++) {
                File file = listFiles[j];
                Main.visuProgress(progress,fileLocation,j,listFiles.length);
                if (file.isDirectory()) {
                    isFinished = isFinished & deleteEmptyRep_old(file.getAbsolutePath(), progress);
                }
            }

        }
        return isFinished;
    }

    public static List<String> listRepertoireEligible(String repertoire50Phototheque, RepertoirePhoto repPhoto) {
        List<String> ret = new ArrayList<>();
        String pathname = repertoire50Phototheque + repPhoto.getRepertoire();
        File[] files = new File(pathname).listFiles();

        SystemFiles.mkdir(pathname);

        for (File file : files) {
            if (file.isDirectory()) {
                ret.add(file.toString());
            }
        }
        return ret;
    }

    public static void renommerRepertoire(String source, String destination, Database dbLr) throws IOException, SQLException {
        File fsource = new File(source);
        File fdest = new File(destination);
        if (fsource.compareTo(fdest) != 0) {
            if (!fsource.exists() || !fsource.isDirectory()) {
                throw new IllegalStateException("non existence : " + fsource.toString());
            }
            if (fdest.exists()) {
                throw new IllegalStateException("existence     : " + fdest.toString());
            }

            LOGGER.debug(() -> "move_repertoire p=" + fsource.toString() + " -> " + fdest.toString());
            Files.move(fsource.toPath(), fdest.toPath());

            int ret = dbLr.moveRepertory(source, destination);
            LOGGER.debug(() -> "dbLr.moveRepertory(source, destination)=" + ret);

        }
    }


}
