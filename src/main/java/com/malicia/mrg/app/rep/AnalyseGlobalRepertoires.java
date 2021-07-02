package com.malicia.mrg.app.rep;

import com.malicia.mrg.Context;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.electx.ControleRepertoire;
import com.malicia.mrg.param.electx.RepertoirePhoto;
import com.malicia.mrg.util.WhereIAm;
import com.malicia.mrg.view.RenameRepertoire;
import org.apache.commons.io.FilenameUtils;
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
    private static Context ctx;
    private static Database dbLr;
    private final List<BlocRetourRepertoire> listOfretourNomRepertoire;
    private final List<BlocRetourRepertoire> listOfretourValRepertoire;

    public AnalyseGlobalRepertoires() {
        this.listOfretourNomRepertoire = new ArrayList<>();
        this.listOfretourValRepertoire = new ArrayList<>();
    }

    public static void controleChamp(String repertoire, RepertoirePhoto repPhoto, EleChamp ele) throws SQLException {
        LOGGER.debug("controleChamp : {}", ele.getcChamp());

        ele.setRetourControle(false);

        String[] arrayChamp = ele.getcChamp().split("\\|");
        // iterating over an array
        for (String elechamp : arrayChamp) {

            if (!ele.isRetourControle()) {
                testElementChamp(repertoire, repPhoto, elechamp, ele);
            }
        }
    }

    private static void testElementChamp(String repertoire, RepertoirePhoto repPhoto, String elechamp, EleChamp ele) throws SQLException {
        int limitemaxfolder = 0;
        int nbSelectionner = 0;
        int nbNonSelectionner =0;
        int nbphotoapurger = 0;
        int nbelements = 0;
        switch (elechamp) {
            case ControleRepertoire.DATE_DATE:
                String date = dbLr.getDate(repertoire);
                ele.setRetourToFalse(date, "changenomrep_dateto_" + date);
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
                ele.setRetourToFalse(elechamp, "changenomrep_" + elechamp);
                if (Boolean.TRUE.equals(dbLr.isValueInKeyword(ele.getoValue(), ControleRepertoire.nettoyageTag(elechamp)))) {
                    ele.setRetourToTrue();
                }
                break;
            case ControleRepertoire.NB_STAR_VALUE:
                controleRepertoireNBSTARVALUE(repertoire, repPhoto, ele);
                break;
            case ControleRepertoire.NB_ELEMENTS:
                nbelements = getNbelementsPhysiqueNonRejet(repertoire);
                ele.setRetourToTrue();
                if (nbelements == 0) {
                    ele.setRetourToFalse(String.valueOf(nbelements), "Phase0-pbrepertoire_zeroelements_" + "(0ele)" + repertoire.replace(repPhoto.getRepertoire() + "\\", "").replace(ctx.getRepertoire50Phototheque(), "").replace("_", " "));
                }
                break;
            case ControleRepertoire.NB_SELECTIONNER:
                nbSelectionner = dbLr.nbPickNoVideo(repertoire);
                ele.setRetourToTrue();
                if (nbSelectionner == 0) {
                    ele.setRetourToFalse(String.valueOf(nbSelectionner), "Phase1_1-zeroElePhotoSelectionner_" + "(0select)" + repertoire.replace(repPhoto.getRepertoire() + "\\", "").replace(ctx.getRepertoire50Phototheque(), "").replace("_", " "));
                }
                break;
            case ControleRepertoire.NB_NONSELECTIONNER:
                nbNonSelectionner = dbLr.nbNoPickNoVideo(repertoire);
                ele.setRetourToTrue();
                if (nbNonSelectionner != 0) {
                    ele.setRetourToFalse(String.valueOf(nbNonSelectionner), "Phase1_1-nbPhotoNonSelectionner_"  +  "(nbNselect)" +String.format("%05d", nbNonSelectionner));
                }
                break;
            case ControleRepertoire.NB_PHOTOAPURGER:
                limitemaxfolder = (int) ((Double.valueOf(repPhoto.getNbMaxParUniteDeJour()) * dbLr.nbJourFolderNoVideo(repertoire)) / Double.valueOf(repPhoto.getUniteDeJour()));
                nbSelectionner = dbLr.nbPickNoVideo(repertoire);
                nbphotoapurger = nbSelectionner - limitemaxfolder;
                ele.setRetourToTrue();
                if (nbphotoapurger > 0) {
                    ele.setRetourToFalse(String.valueOf(nbphotoapurger), "Phase1_2-nbPhotoAPurge_" + "(nbpurge)" + String.format("%05d", nbphotoapurger));
                }
                break;
            case ControleRepertoire.NB_LIMITEMAXFOLDER:
                limitemaxfolder = (int) ((Double.valueOf(repPhoto.getNbMaxParUniteDeJour()) * dbLr.ecartJourFolder(repertoire)) / Double.valueOf(repPhoto.getUniteDeJour()));
                ele.setRetourToTrue();
                if (limitemaxfolder == 0) {
                    ele.setRetourToFalse(String.valueOf(limitemaxfolder), "Phase0-pbrepertoire_limitemaxazero_" + "(noLMax)" + repertoire);
                }
                break;
            default:
                String txt = "elechamp.elechamp=" + elechamp + " inconnu ";
                LOGGER.debug(() -> txt);
                throw new IllegalStateException(txt);
        }
    }

    private static int getNbelementsPhysiqueNonRejet(String repertoire) {
        int nbelements;
        nbelements = 0;
        File[] files = new File(repertoire).listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                String fileExt = FilenameUtils.getExtension(file.getName()).toLowerCase();
                if (ctx.getParamElementsRejet().getArrayNomFileRejet().contains(fileExt.toLowerCase())) {
                    nbelements++;
                }
            }
        }
        return nbelements;
    }

    private static void controleRepertoireNBSTARVALUE(String repertoire, RepertoirePhoto repPhoto, EleChamp ele) throws SQLException {
        //todo
        int nbSelectionner;
        nbSelectionner = dbLr.nbPickNoVideo(repertoire);
        Map<String, Integer> starValue = dbLr.getStarValueNoVideo(repertoire);
        List<Integer> ratio = repPhoto.getratioStarMax();

        StringBuilder res = new StringBuilder();

        List<String> tag = new ArrayList<>();
        boolean allStarGood = true;
        for (int i = 1; i < 6; i++) {
            int nbmin = (int) Math.round((ratio.get(i - 1) * Double.valueOf(nbSelectionner)) / 100 / 2);
            int nbmax = (int) Math.ceil((ratio.get(i - 1) * Double.valueOf(nbSelectionner)) / 100);

            String strInfo = "(" + "S" + i + "){" + String.format("%03d", starValue.get(String.valueOf(i))) + "{" + String.format("%03d", nbmin) + "/" + String.format("%03d", nbmax) + "}" + "}";

            String s = "(" + i + ")" + " ---star-: " + starValue.get(String.valueOf(i)) + " ---ratio-: " + ratio.get(i - 1) + " ---nbmin-: " + nbmin + " ---nbmax-: " + nbmax;
            LOGGER.debug(s);

            res.append(strInfo);

            if (nbmin > starValue.get(String.valueOf(i)) || starValue.get(String.valueOf(i)) > nbmax) {
                allStarGood = false;

                tag.add("Phase2_nbStarErreur" + i + "_" + strInfo + " ");

            }

            res.append(ControleRepertoire.CARAC_SEPARATEUR);
        }
        ele.setRetourToTrue();
        if (!allStarGood) {
            ele.setRetourToFalse(res.toString(), tag);
        }
    }

    public static BlocRetourRepertoire calculateLesEleChampsDuRepertoire(String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
        LOGGER.debug("isRepertoireOk :  {}", repertoire);

        BlocRetourRepertoire retourControleRep = new BlocRetourRepertoire(repPhoto, repertoire);

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
            controleChamp(repertoire, repPhoto, eChamp);
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
            controleChamp(repertoire, repPhoto, eChamp);
            listOfChampCtrl.add(eChamp);
        }
        retourControleRep.setListOfControleValRepertoire(listOfChampCtrl);


        //Recuperer 4 preview du repertoire
        retourControleRep.setPreviewPhoto(dbLr.getFourRandomPreviewPhoto(repertoire));

        //Recuperer lstphoto du rep
        retourControleRep.setLstPhoto(dbLr.getLstPhoto(repertoire));

        return retourControleRep;

    }

    public static void init(Context ctxIn, Database dbLrIn) {
        ctx = ctxIn;
        dbLr = dbLrIn;
    }

    public List<BlocRetourRepertoire> getListOfretourValRepertoire() {
        return listOfretourValRepertoire;
    }

    public void add(BlocRetourRepertoire retourRepertoire) {
        if (Boolean.FALSE.equals(retourRepertoire.getValOk())) {
            this.listOfretourValRepertoire.add(retourRepertoire);
        }
        if (Boolean.FALSE.equals(retourRepertoire.getNomOk()) && !retourRepertoire.getLstPhoto().isEmpty()) {
            this.listOfretourNomRepertoire.add(retourRepertoire);
        }

    }

    public void action() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        if (!listOfretourNomRepertoire.isEmpty()) {
            RenameRepertoire.start(dbLr, ctx, listOfretourNomRepertoire);
        }
    }

    @Override
    public String toString() {
        return "AnalyseGlobalRepertoires{" +
                "nbListOfretourNomRepertoire=" + listOfretourNomRepertoire.size() +
                ", nbListOfretourValRepertoire=" + listOfretourValRepertoire.size() +
                '}';
    }


}
