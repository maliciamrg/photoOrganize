package com.malicia.mrg.app;

import com.malicia.mrg.data.Database;
import com.malicia.mrg.param.ControleRepertoire;
import com.malicia.mrg.param.RepertoirePhoto;
import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

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
        File[] files = new File(repertoire50Phototheque + repPhoto.getRepertoire()).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret.add(file.toString());
            }
        }
        return ret;
    }

    public static boolean isRepertoireOk(Database dbLr, String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        LOGGER.info("isRepertoireOk : " + repertoire);

        long idLocalRep = dbLr.getIdlocalforRep(repertoire);

        String oldNameRepertoire = new File(repertoire).getName();
        String oldcheminRepertoire = new File(repertoire).getParent();
        String[] oldChamp = oldNameRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);

        List<EleChamp> listOfChamp = FXCollections.observableArrayList();

        //controle nom du repertoire
        int i = 0;
        ListIterator<String> nomRepertoireIterator = repPhoto.getZoneValeurAdmise().listIterator();
        while (nomRepertoireIterator.hasNext()) {
            String valeurAdmise = nomRepertoireIterator.next();

            EleChamp EChamp;
            if (i < oldChamp.length) {
                EChamp = new EleChamp(valeurAdmise, oldChamp[i]);
            } else {
                EChamp = new EleChamp(valeurAdmise, "");
            }
            EChamp.controleChamp(dbLr, repertoire, repPhoto, idLocalRep);
            listOfChamp.add(EChamp);


            i++;
        }


        //controle contenu du repertoire
        ListIterator<String> listControleRepertoireIterator = paramControleRepertoire.getlistControleRepertoire().listIterator();
        while (listControleRepertoireIterator.hasNext()) {
            EleChamp EChamp = new EleChamp();
            String ele = listControleRepertoireIterator.next();
            EChamp.setcChamp(ele);
            EChamp.controleChamp(dbLr, repertoire, repPhoto, idLocalRep);
            listOfChamp.add(EChamp);
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
            LOGGER.info("ecriture fichier ->" + f.toString());
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

}
