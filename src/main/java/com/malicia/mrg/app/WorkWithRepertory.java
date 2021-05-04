package com.malicia.mrg.app;

import com.malicia.mrg.app.rep.EleChamp;
import com.malicia.mrg.app.rep.blocRetourRepertoire;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.util.SystemFiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class WorkWithRepertory {

    private static final Logger LOGGER = LogManager.getLogger(WorkWithRepertory.class);

    private WorkWithRepertory() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean deleteEmptyRep(String fileLocation) {
        boolean isFinished = true;
        File folder = new File(fileLocation);
        File[] listFiles = folder.listFiles();
        if (listFiles.length == 0) {
            LOGGER.info("Folder Name :: " + folder.getAbsolutePath() + " is deleted.");
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

    public static blocRetourRepertoire calculateLesEleChampsDuRepertoire(Database dbLr, String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        LOGGER.debug("isRepertoireOk : " + repertoire);

        blocRetourRepertoire retourControleRep = new blocRetourRepertoire(repPhoto , repertoire);

        String oldNameRepertoire = new File(repertoire).getName();
        String[] oldChamp = oldNameRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);

        //controle nom du repertoire
        List<EleChamp> listOfChampNom = new ArrayList<>();
        int i = 0;
        ListIterator<String> nomRepertoireIterator = repPhoto.getZoneValeurAdmise().listIterator();
        while (nomRepertoireIterator.hasNext()) {
            String valeurAdmise = nomRepertoireIterator.next();

            EleChamp eChamp;
            if (i < oldChamp.length) {
                eChamp = new EleChamp(valeurAdmise, oldChamp[i]);
            } else {
                eChamp = new EleChamp(valeurAdmise, "");
            }
            eChamp.controleChamp(dbLr, repertoire, repPhoto);
            listOfChampNom.add(eChamp);
            i++;
        }
        retourControleRep.setListOfControleNom(listOfChampNom);

        //controle contenu du repertoire
        List<EleChamp> listOfChampCtrl = new ArrayList<>();

        ListIterator<String> listControleRepertoireIterator = paramControleRepertoire.getlistControleRepertoire().listIterator();
        while (listControleRepertoireIterator.hasNext()) {
            EleChamp eChamp = new EleChamp();
            String ele = listControleRepertoireIterator.next();
            eChamp.setcChamp(ele);
            eChamp.controleChamp(dbLr, repertoire, repPhoto);
            listOfChampCtrl.add(eChamp);
        }
        retourControleRep.setListOfControleValRepertoire(listOfChampCtrl);

        return retourControleRep;

    }

    public static void renommerRepertoire(String source, String destination) throws IOException {
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
        }
    }


    public static void sqlMkdirRepertory(String directoryName, Database dbLr) throws SQLException {

        File fdirectoryName = new File(directoryName);
        if (!fdirectoryName.exists()) {
            SystemFiles.mkdir(directoryName);
            dbLr.makeRepertory(directoryName);
        }
    }

}
