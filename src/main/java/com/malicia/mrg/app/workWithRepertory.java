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

    public static boolean isRepertoireOk(Database dbLr, String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        long idLocalRep = dbLr.getIdlocalforRep(repertoire);

        String oldNameRepertoire = new File(repertoire).getName();
        String oldcheminRepertoire = new File(repertoire).getParent();
        String[] oldChamp = oldNameRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);

        List<EleChamp> listOfChamp = FXCollections.observableArrayList();

        //controle nom du repertoire
        int i = 0;
        ListIterator<RepertoirePhoto.ParamZone> nomRepertoireIterator = repPhoto.getpZone().listIterator();
        while (nomRepertoireIterator.hasNext()) {
            EleChamp EChamp;
            if (i < oldChamp.length) {
                EChamp = new EleChamp(nomRepertoireIterator.next().valeurAdmise, oldChamp[i]);
            } else {
                EChamp = new EleChamp(nomRepertoireIterator.next().valeurAdmise, "");
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

    private static class EleChamp {
        private String cChamp;
        private String oValue;
        private String infoRetourControle;
        private boolean retourControle;

        public EleChamp() {
            this.cChamp = "";
            this.oValue = "";
            this.infoRetourControle = "";
        }

        public EleChamp(String codeChamp, String oldValue) {
            this.cChamp = codeChamp;
            this.oValue = oldValue;
            this.infoRetourControle = "";
        }

        public boolean isRetourControle() {
            return retourControle;
        }

        public String getInfoRetourControle() {
            return infoRetourControle;
        }

        public void controleChamp(Database dbLr, String repertoire, RepertoirePhoto repPhoto, long idLocalRep) throws SQLException {
            retourControle = false;
            int limitemaxfolder = 0;
            int nbSelectionner = 0;
            int nbphotoapurger = 0;
            int nbelements = 0;
            switch (getcChamp()) {
                case ControleRepertoire.£_DATE_£:
                    String date = dbLr.getDate(idLocalRep);
                    if (date.compareTo(oValue)==0){
                        setRetourToTrue();
                    }
                    setRetourToFalse(date);
                    break;
                case ControleRepertoire.TAG_ACTION:
                case ControleRepertoire.TAG_PIECE:
                case ControleRepertoire.TAG_CHANTIER:
                case ControleRepertoire.TAG_EVENT:
                case ControleRepertoire.TAG_PHOTOGRAPHY:
                case ControleRepertoire.TAG_WHERE:
                case ControleRepertoire.TAG_WHAT:
                case ControleRepertoire.TAG_WHO:
                    if (dbLr.isValueInTag(getoValue(), nettoyageTag(getcChamp()))) {
                        setRetourToTrue();
                    } else {
                        setRetourToFalse(dbLr.getValueForTag(nettoyageTag(getcChamp())).toString());
                    }
                    break;
                case ControleRepertoire.NB_STAR_VALUE:
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    Map<String, Integer> starValue = dbLr.getStarValue(idLocalRep);
                    List<Integer> ratio = repPhoto.getratioStarMax();
                    String res = "";
                    boolean allStarGood = true;
                    for (int i = 0; i < 5; i++) {
                        int nbmax = (ratio.get(i) * nbSelectionner) / 100;
                        LOGGER.info("(" + i + ")" + " ---star-: " + starValue.get(String.valueOf(i)) + " ---ratio-: " + ratio.get(i) + " ---nbmax-: " + nbmax);
                        res = res + "(" + "S" + i + ")";
                        if (starValue.get(String.valueOf(i)) > nbmax) {
                            allStarGood = false;
                            res = res + (nbmax - starValue.get(String.valueOf(i)));
                        }
                        res = res + ControleRepertoire.CARAC_SEPARATEUR;
                    }
                    setRetourToTrue();
                    if (!allStarGood) {
                        setRetourToFalse(res);
                    }
                    break;
                case ControleRepertoire.NB_ELEMENTS:
                    nbelements = 0;
                    File[] files = new File(repertoire).listFiles();
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            nbelements++;
                        }
                    }
                    setRetourToTrue();
                    if (nbelements == 0) {
                        setRetourToFalse(String.valueOf(nbelements));
                    }
                    break;
                case ControleRepertoire.NB_SELECTIONNER:
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    setRetourToTrue();
                    if (nbSelectionner == 0) {
                        setRetourToFalse(String.valueOf(nbSelectionner));
                    }
                    break;
                case ControleRepertoire.NB_PHOTOAPURGER:
                    limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(idLocalRep))) / repPhoto.getUniteDeJour());
                    nbSelectionner = dbLr.nb_pick(idLocalRep);
                    nbphotoapurger = nbSelectionner - limitemaxfolder;
                    setRetourToTrue();
                    if (nbphotoapurger > 0) {
                        setRetourToFalse(String.valueOf(nbphotoapurger));
                    }
                    break;
                case ControleRepertoire.NB_LIMITEMAXFOLDER:
                    limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(idLocalRep))) / repPhoto.getUniteDeJour());
                    setRetourToTrue();
                    if (limitemaxfolder == 0) {
                        setRetourToFalse(String.valueOf(limitemaxfolder));
                    }
                    break;
                default:
                    String txt = "elechamp.getcChamp()=" + getcChamp() + " inconnu ";
                    LOGGER.debug(() -> txt);
                    throw new IllegalStateException(txt);
            }
        }

        private String nettoyageTag(String getcChamp) {
            return getcChamp.replaceAll("@","");
        }

        private void setRetourToFalse(String iretourControle) {
            this.infoRetourControle = iretourControle;
            retourControle = false;
        }
        private void setRetourToTrue() {
            this.infoRetourControle = ControleRepertoire.CARAC_EMPTY;
            retourControle = true;
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
