package com.malicia.mrg.app;

import com.malicia.mrg.Main;
import com.malicia.mrg.param.nomageRepertoire;
import com.malicia.mrg.param.repertoirePhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class workWithRepertory {

    private static final Logger LOGGER = LogManager.getLogger(workWithRepertory.class);

    public static boolean deleteEmptyRep(String fileLocation) {
        boolean isFinished = true;
        File folder = new File(fileLocation);
        File[] listofFiles = folder.listFiles();
        if (listofFiles.length == 0) {
            LOGGER.trace("Folder Name :: " + folder.getAbsolutePath() + " is deleted.");
            folder.delete();
            isFinished = false;
        } else {
            for (int j = 0; j < listofFiles.length; j++) {
                File file = listofFiles[j];
                if (file.isDirectory()) {
                    isFinished = isFinished && deleteEmptyRep(file.getAbsolutePath());
                }
            }
        }
        return isFinished;
    }

    public static List<String> listRepertoireEligible(repertoirePhoto repPhoto) {
        //TODO
        return null;
    }

    public static String newNameRepertoire(String repertoire, repertoirePhoto repPhoto, nomageRepertoire paramNomageRepertoire) {
        //TODO
        return null;
    }
}
