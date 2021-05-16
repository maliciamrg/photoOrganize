package com.malicia.mrg.app.rep;

import com.malicia.mrg.Context;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.util.WhereIAm;
import com.malicia.mrg.view.RenameRepertoire;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AnalyseGlobalRepertoires {
    private static final Logger LOGGER = LogManager.getLogger(AnalyseGlobalRepertoires.class);
    private List<blocRetourRepertoire> listOfretourNomRepertoire;
    private List<blocRetourRepertoire> listOfretourValRepertoire;
    private static Context ctx;
    private static Database dbLr;

    public AnalyseGlobalRepertoires() {
        this.listOfretourNomRepertoire = new ArrayList<>() ;
        this.listOfretourValRepertoire = new ArrayList<>() ;
    }

    public static void controleChamp(String repertoire, RepertoirePhoto repPhoto , EleChamp ele) throws SQLException, IOException {
        LOGGER.debug("controleChamp : {}", ele.getcChamp());

        ele.setRetourControle(false);

        String[] arrayChamp = ele.getcChamp().split("\\|");
        // iterating over an array
        for (String elechamp : arrayChamp) {

            if (!ele.isRetourControle()) {
                testElementChamp(repertoire, repPhoto, elechamp,ele);
            }
        }
    }

    private static void testElementChamp(String repertoire, RepertoirePhoto repPhoto, String elechamp, EleChamp ele) throws SQLException, IOException {
        int limitemaxfolder = 0;
        int nbSelectionner = 0;
        int nbphotoapurger = 0;
        int nbelements = 0;
        switch (elechamp) {
            case ControleRepertoire.DATE_DATE:
                String date = dbLr.getDate(repertoire);
                ele.setRetourToFalse(date,"changenomrep_dateto_" + date);
                if (date.compareTo(ele.getoValue()) == 0) {
                    ele.setRetourToTrue();
                }
                break;
            case ControleRepertoire.TAG_ACTION:
            case ControleRepertoire.TAG_PIECE:
            case ControleRepertoire.TAG_CHANTIER:
            case ControleRepertoire.TAG_EVENT:
            case ControleRepertoire.TAG_PHOTOGRAPHY:
            case ControleRepertoire.TAG_WHERE:
            case ControleRepertoire.TAG_WHAT:
            case ControleRepertoire.TAG_WHO:
//                ele.setRetourToFalse(prefixAllElementsList(elechamp,dbLr.getValueForKeyword( nettoyageTag(elechamp))),"changenomrep_"+elechamp);
                ele.setRetourToFalse(elechamp,"changenomrep_"+elechamp);
                if (Boolean.TRUE.equals(dbLr.isValueInKeyword(ele.getoValue(), ControleRepertoire.nettoyageTag(elechamp)))) {
                    ele.setRetourToTrue();
                }
                break;
            case ControleRepertoire.NB_STAR_VALUE:
                controleRepertoireNBSTARVALUE(repertoire, repPhoto,ele);
                break;
            case ControleRepertoire.NB_ELEMENTS:
                nbelements = getNbelements(repertoire);
                ele.setRetourToTrue();
                if (nbelements == 0) {
                    ele.setRetourToFalse(String.valueOf(nbelements),"pbrepertoire_zeroelements");
                }
                break;
            case ControleRepertoire.NB_SELECTIONNER:
                nbSelectionner = dbLr.nbPick( repertoire);
                ele.setRetourToTrue();
                if (nbSelectionner == 0) {
                    ele.setRetourToFalse(String.valueOf(nbSelectionner),"pbrepertoire_zeroelements_selectionner");
                }
                break;
            case ControleRepertoire.NB_PHOTOAPURGER:
                limitemaxfolder = (int) ((Double.valueOf(repPhoto.getNbMaxParUniteDeJour()) * dbLr.nbjourfolder( repertoire)) / Double.valueOf(repPhoto.getUniteDeJour()));
                nbSelectionner = dbLr.nbPick( repertoire);
                nbphotoapurger = nbSelectionner - limitemaxfolder;
                ele.setRetourToTrue();
                if (nbphotoapurger > 0) {
                    ele.setRetourToFalse(String.valueOf(nbphotoapurger),"pbrepertoire_purge_" + String.format("%05d",nbphotoapurger));
                }
                break;
            case ControleRepertoire.NB_LIMITEMAXFOLDER:
                limitemaxfolder = (int) ((Double.valueOf(repPhoto.getNbMaxParUniteDeJour()) * dbLr.nbjourfolder( repertoire)) / Double.valueOf(repPhoto.getUniteDeJour()));
                ele.setRetourToTrue();
                if (limitemaxfolder == 0) {
                    ele.setRetourToFalse(String.valueOf(limitemaxfolder),"pbrepertoire_limitemaxazero");
                }
                break;
            default:
                String txt = "elechamp.elechamp=" + elechamp + " inconnu ";
                LOGGER.debug(() -> txt);
                throw new IllegalStateException(txt);
        }
    }


    private static int getNbelements(String repertoire) {
        int nbelements;
        nbelements = 0;
        File[] files = new File(repertoire).listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                nbelements++;
            }
        }
        return nbelements;
    }

    private static void controleRepertoireNBSTARVALUE(String repertoire, RepertoirePhoto repPhoto , EleChamp ele) throws SQLException {
        int nbSelectionner;
        nbSelectionner = dbLr.nbPick( repertoire);
        Map<String, Integer> starValue = dbLr.getStarValue( repertoire);
        List<Integer> ratio = repPhoto.getratioStarMax();
        StringBuilder res = new StringBuilder();
        StringBuilder tag = new StringBuilder();
        boolean allStarGood = true;
        for (int i = 1; i < 6; i++) {
            int nbmax = (int) Math.ceil((ratio.get(i - 1) * Double.valueOf(nbSelectionner)) / 100);
            String s = "(" + i + ")" + " ---star-: " + starValue.get(String.valueOf(i)) + " ---ratio-: " + ratio.get(i - 1) + " ---nbmax-: " + nbmax;
            LOGGER.debug(s);
            String starn = "(" + "S" + i + ")";
            res.append(starn);
            if (starValue.get(String.valueOf(i)) > nbmax) {
                allStarGood = false;
                int i1 = nbmax - starValue.get(String.valueOf(i));
                res.append(i1);
                tag.append(starn + "_-" + String.format("%03d",i1));
            }
            res.append(ControleRepertoire.CARAC_SEPARATEUR);
        }
        ele.setRetourToTrue();
        if (!allStarGood) {
            ele.setRetourToFalse(res.toString(),tag.toString());
        }
    }

    public static blocRetourRepertoire calculateLesEleChampsDuRepertoire(String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        LOGGER.debug("isRepertoireOk : " + repertoire);

        blocRetourRepertoire retourControleRep = new blocRetourRepertoire(repPhoto , repertoire);

        String ancienNomDuRepertoire = new File(repertoire).getName();
        String[] oldChamp = ancienNomDuRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);

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
            controleChamp(repertoire, repPhoto,eChamp);
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
            controleChamp(repertoire, repPhoto,eChamp);
            listOfChampCtrl.add(eChamp);
        }
        retourControleRep.setListOfControleValRepertoire(listOfChampCtrl);


        //Recuperer 4 preview du repertoire
        retourControleRep.previewPhoto=dbLr.getFourRandomPreviewPhoto(repertoire);

        //Recuperer lstphoto du rep
        retourControleRep.lstPhoto=dbLr.getLstPhoto(repertoire);

        return retourControleRep;

    }

    public void add(blocRetourRepertoire retourRepertoire) {
        if (!retourRepertoire.getValOk()) {
            this.listOfretourValRepertoire.add(retourRepertoire);
        }
        if (!retourRepertoire.getNomOk()) {
            if (retourRepertoire.lstPhoto.size()!=0) {
                this.listOfretourNomRepertoire.add(retourRepertoire);
            }
        }
    }

    public static void init(Context ctxIn, Database dbLrIn) {
        ctx = ctxIn;
        dbLr = dbLrIn;
    }

    public void action() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        RenameRepertoire.start(dbLr,ctx, listOfretourNomRepertoire);
    }

    @Override
    public String toString() {
        return "AnalyseGlobalRepertoires{" +
                "nbListOfretourNomRepertoire=" + listOfretourNomRepertoire.size() +
                ", nbListOfretourValRepertoire=" + listOfretourValRepertoire.size() +
                '}';
    }


}
