package com.malicia.mrg.app;

import com.malicia.mrg.param.ElementsRejet;
import com.malicia.mrg.param.NommageRepertoire;
import com.malicia.mrg.param.RepertoirePhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

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

    public static List<String> listRepertoireEligible(RepertoirePhoto repPhoto) {
        //TODO
        return null;
    }

    public static String newNameRepertoire(String repertoire, RepertoirePhoto repPhoto, NommageRepertoire paramNommageRepertoire) {
        //TODO
        return null;
    }
}
