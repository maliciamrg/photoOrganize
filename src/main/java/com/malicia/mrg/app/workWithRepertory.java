package com.malicia.mrg.app;

import com.malicia.mrg.data.Database;
import com.malicia.mrg.param.NommageRepertoire;
import com.malicia.mrg.param.RepertoirePhoto;
import javafx.collections.FXCollections;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class workWithRepertory {

    private static final Logger LOGGER = LogManager.getLogger(workWithRepertory.class);

    public static boolean deleteEmptyRep(String fileLocation) {
        boolean isFinished = true;
        File folder = new File(fileLocation);
        File[] listFiles = folder.listFiles();
        if (listFiles.length == 0) {
            LOGGER.trace("Folder Name :: " + folder.getAbsolutePath() + " is deleted.");
            folder.delete();
            isFinished = false;
        } else {
            for (int j = 0; j < listFiles.length; j++) {
                File file = listFiles[j];
                if (file.isDirectory()) {
                    isFinished = isFinished && deleteEmptyRep(file.getAbsolutePath());
                }
            }
        }
        return isFinished;
    }

    public static List<String> listRepertoireEligible(String repertoire50Phototheque, RepertoirePhoto repPhoto) {
        List<String> ret = FXCollections.observableArrayList();
        File[] files = new File(repertoire50Phototheque+repPhoto.getRepertoire()).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret.add(file.toString());
            }
        }
        return ret;
    }

    public static String newNameRepertoire(Database dbLr, String repertoire, RepertoirePhoto repPhoto, NommageRepertoire paramNommageRepertoire) throws SQLException {
        long idLocalRep = dbLr.getIdlocalforRep(repertoire);

        Map<String, Integer> starValue = dbLr.getStarValue(idLocalRep);

        int nb = 0;
        List<String> ret = FXCollections.observableArrayList();
        File[] files = new File(repertoire).listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                nb++;
            }
        }

        int limitemaxfolder = (int) (agLibraryRootFolder.nbmaxCat * Math.ceil((double) nbjourfolder / agLibraryRootFolder.nbjouCat));
        nbSelectionner > 0 (nb pick == 1)
        nbphotoapurger = 0 (nbSelectionner - limitemaxfolder)

        //TODO
        return null;
    }

    public static void renommerRepertoire(String source, String destination) throws IOException {
        File fsource = new File(source);
        File fdest = new File(destination);
        if (fsource.compareTo(fdest) != 0) {
            if (!fsource.exists() || !fsource.isDirectory() ) {
                throw new IllegalStateException("non existence : " + fsource.toString());
            }
            if (fdest.exists()) {
                throw new IllegalStateException("existence     : " + fdest.toString());
            }

            LOGGER.debug(() -> "move_repertoire p=" + fsource.toString() + " -> " + fdest.toString());
            Files.move(fsource.toPath(), fdest.toPath());
        }
    }
}
