package com.malicia.mrg.app;

import com.malicia.mrg.data.Database;
import com.malicia.mrg.param.NommageRepertoire;
import com.malicia.mrg.param.RepertoirePhoto;
import javafx.collections.FXCollections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
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
        File[] files = new File(repertoire50Phototheque + repPhoto.getRepertoire()).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret.add(file.toString());
            }
        }
        return ret;
    }

    public static String newNameRepertoire(Database dbLr, String repertoire, RepertoirePhoto repPhoto, NommageRepertoire paramNommageRepertoire) throws SQLException {
        long idLocalRep = dbLr.getIdlocalforRep(repertoire);

        String oldNameRepertoire = new File(repertoire).getName();
        String[] oldChamp = oldNameRepertoire.split(NommageRepertoire.CARAC_SEPARATEUR);

        List<EleChamp> listOfChamp = FXCollections.observableArrayList();
        ListIterator<String> formatNomRepertoireIterator = paramNommageRepertoire.getFormatNomRepertoire().listIterator();
        while (formatNomRepertoireIterator.hasNext()) {
            EleChamp EChamp = new EleChamp();
            String ele = formatNomRepertoireIterator.next();
            EChamp.setcChamp(ele);
            if (ele.compareTo(NommageRepertoire.MACRO_GROUP_NOM_REPERTOIRE) == 0) {
                int i = 0;
                ListIterator<String> nomRepertoireIterator = repPhoto.getNomRepertoire().listIterator();
                while (nomRepertoireIterator.hasNext()) {
                    if (i < oldChamp.length) {
                        listOfChamp.add(new EleChamp(nomRepertoireIterator.next(), oldChamp[i]));
                    } else {
                        listOfChamp.add(new EleChamp(nomRepertoireIterator.next(), ""));
                    }
                    i++;
                }
            } else {
                listOfChamp.add(EChamp);
            }
        }

        int limitemaxfolder = 0;
        int nbSelectionner = 0;
        int nbphotoapurger = 0;
        int nbelements = 0;
        ListIterator<EleChamp> champIte = listOfChamp.listIterator();
        while (champIte.hasNext()) {
            EleChamp elechamp = champIte.next();
            switch (elechamp.getcChamp()) {
                case NommageRepertoire.£_DATE_£:
                    elechamp.setnValue(dbLr.getDate(idLocalRep));
                    break;
                case NommageRepertoire.TAG_ACTION:
                case NommageRepertoire.TAG_PIECE:
                case NommageRepertoire.TAG_CHANTIER:
                case NommageRepertoire.TAG_EVENT:
                case NommageRepertoire.TAG_PHOTOGRAPHY:
                case NommageRepertoire.TAG_WHERE:
                case NommageRepertoire.TAG_WHAT:
                case NommageRepertoire.TAG_WHO:
                    if (dbLr.isValueInTag(elechamp.getoValue(), NommageRepertoire.TAG_ACTION)) {
                        elechamp.setnValue(elechamp.getoValue());
                    } else {
                        elechamp.setnValue("");
                    };
                    break;
                case NommageRepertoire.NB_STAR_VALUE:
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    Map<String, Integer> starValue = dbLr.getStarValue(idLocalRep);
                    break;
                case NommageRepertoire.NB_ELEMENTS:
                    nbelements = 0;
                    File[] files = new File(repertoire).listFiles();
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            nbelements++;
                        }
                    }
                    break;
                case NommageRepertoire.NB_SELECTIONNER:
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    break;
                case NommageRepertoire.NB_PHOTOAPURGER:
                    limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(idLocalRep))) / repPhoto.getUniteDeJour());
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    nbphotoapurger = nbSelectionner - limitemaxfolder;
                    break;
                case NommageRepertoire.NB_LIMITEMAXFOLDER:
                    limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(idLocalRep))) / repPhoto.getUniteDeJour());
                    break;
                default:
                    // code block
            }
        }

        //TODO
        return null;
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

    private static class EleChamp {
        private String cChamp;
        private String oValue;
        private String nValue;

        public EleChamp() {
            this.cChamp = "";
            this.oValue = "";
            this.nValue = "";
        }

        public EleChamp(String codeChamp, String oldValue) {
            this.cChamp = codeChamp;
            this.oValue = oldValue;
            this.nValue = "";
        }

        public String getnValue() {
            return nValue;
        }

        public void setnValue(String nValue) {
            this.nValue = nValue;
        }

        public String getcChamp() {
            return cChamp;
        }

        public void setcChamp(String codeChamp) {
            this.cChamp = codeChamp;
        }

        public String getoValue() {
            return oValue;
        }

        public void setoValue(String oldValue) {
            this.oValue = oldValue;
        }


    }
}
