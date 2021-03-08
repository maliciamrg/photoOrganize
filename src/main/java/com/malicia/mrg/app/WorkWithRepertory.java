package com.malicia.mrg.app;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.util.Serialize;
import com.malicia.mrg.util.SystemFiles;
import javafx.collections.FXCollections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

public class WorkWithRepertory {

    private WorkWithRepertory() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = LogManager.getLogger(WorkWithRepertory.class);

    public static boolean deleteEmptyRep(String fileLocation) {
        boolean isFinished = true;
        File folder = new File(fileLocation);
        File[] listFiles = folder.listFiles();
        if (listFiles.length == 0) {
            LOGGER.debug("Folder Name :: " + folder.getAbsolutePath() + " is deleted.");
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

    public static boolean isRepertoireOk(Database dbLr, String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        LOGGER.debug("isRepertoireOk : " + repertoire);

        String oldNameRepertoire = new File(repertoire).getName();
        String[] oldChamp = oldNameRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);

        List<EleChamp> listOfChamp = FXCollections.observableArrayList();

        //controle nom du repertoire
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
            listOfChamp.add(eChamp);


            i++;
        }


        //controle contenu du repertoire
        ListIterator<String> listControleRepertoireIterator = paramControleRepertoire.getlistControleRepertoire().listIterator();
        while (listControleRepertoireIterator.hasNext()) {
            EleChamp eChamp = new EleChamp();
            String ele = listControleRepertoireIterator.next();
            eChamp.setcChamp(ele);
            eChamp.controleChamp(dbLr, repertoire, repPhoto);
            listOfChamp.add(eChamp);
        }


        //Resutlat analyse reprertoire
        boolean retour = true;
        ListIterator<EleChamp> champIte = listOfChamp.listIterator();
        while (champIte.hasNext()) {
            EleChamp elechamp = champIte.next();
            retour = retour && elechamp.isRetourControle();
        }

        File f = new File(repertoire + "\\" + "photoOrganizeAnalyse.json");
        if (!retour) {
            Serialize.writeJSON(listOfChamp, f);
            LOGGER.debug("ecriture fichier ->" + f.toString());
        } else {
            f.delete();
        }

        return retour;
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
