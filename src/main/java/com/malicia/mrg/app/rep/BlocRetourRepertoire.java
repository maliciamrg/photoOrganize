package com.malicia.mrg.app.rep;

import com.malicia.mrg.Context;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.electx.ControleRepertoire;
import com.malicia.mrg.param.electx.RepertoirePhoto;
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

public class BlocRetourRepertoire {
    private static final Logger LOGGER = LogManager.getLogger(BlocRetourRepertoire.class);

    private int nbphotoapurger;
    private int nbNonSelectionner;
    private int nbelements;
    private int limitemaxfolderphoto;
    private int limitemaxfoldervideo;
    private int nbSelectionner;

    private String date;
    private List<String> previewPhoto;
    private List<String> lstPhoto;
    private final RepertoirePhoto repPhoto;
    private final String repertoire;
    private List<EleChamp> listOfControleValRepertoire;
    private List<EleChamp> listOfControleNom;
    private Boolean nomOk;
    private Boolean valOk;
    private Context ctx;
    private Database dbLr;

    @Override
    public String toString() {
        return "BlocRetourRepertoire{"  + "\n"+
                "repertoire='" + repertoire + '\''  + "\n"+
                ", date='" + date + '\''  + "\n"+
                ", nbelements=" + nbelements  + "\n"+
                ", nbSelectionner=" + nbSelectionner  + "\n"+
                ", limitemaxfolderphoto=" + limitemaxfolderphoto + "\n"+
                ", limitemaxfoldervideo=" + limitemaxfoldervideo + "\n"+
                ", nbphotoapurger=" + nbphotoapurger  + "\n"+
                ", nbNonSelectionner=" + nbNonSelectionner  + "\n"+
//                ", listOfControleValRepertoire=" + listOfControleValRepertoire.toString()  + "\n"+
//                ", listOfControleNom=" + listOfControleNom.toString()  + "\n"+
                '}' + "\n";
    }

    public BlocRetourRepertoire(String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire, Context ctxIn, Database dbLrIn) throws SQLException, IOException {
        LOGGER.debug("isRepertoireOk :  {}", repertoire);

        this.ctx = ctxIn;
        this.dbLr = dbLrIn;
        this.repPhoto = repPhoto;
        this.repertoire = repertoire;
        nomOk = true;
        valOk = true;

        limitemaxfolderphoto = (int) ((Double.valueOf(repPhoto.getNbMaxParUniteDeJour()) * dbLr.nbJourFolderNoVideo(repertoire)) / Double.valueOf(repPhoto.getUniteDeJour()));
        //si le reprtoire n'a pas de photo mais que des video , il est legit
        limitemaxfoldervideo = (int) ((1 * dbLr.nbJourFolderVideo(repertoire)) / 1);

        nbSelectionner = dbLr.nbPickNoVideo(repertoire);
        date = dbLr.getDate(repertoire);
        nbelements = getNbelementsPhysiqueNonRejet(repertoire);
        nbNonSelectionner = dbLr.nbNoPickNoVideo(repertoire);
        nbphotoapurger = (nbSelectionner + nbNonSelectionner) - limitemaxfolderphoto;

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
        setListOfControleNom(listOfChampNom);

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
        setListOfControleValRepertoire(listOfChampCtrl);


        //Recuperer 4 preview du repertoire
        setPreviewPhoto(dbLr.getFourRandomPreviewPhoto(repertoire));

        //Recuperer lstphoto du rep
        setLstPhoto(dbLr.getLstPhoto(repertoire));

    }

    public BlocRetourRepertoire(RepertoirePhoto repPhoto, String repertoire) {
        this.repPhoto = repPhoto;
        this.repertoire = repertoire;
        nomOk = true;
        valOk = true;
    }

    private int getNbelementsPhysiqueNonRejet(String repertoire) {
        int nbelementsin;
        nbelementsin = 0;
        File[] files = new File(repertoire).listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                String fileExt = FilenameUtils.getExtension(file.getName()).toLowerCase();
                if (ctx.getExtensionsUseFile().contains(fileExt.toLowerCase())) {
                    nbelementsin++;
                }
            }
        }
        return nbelementsin;
    }

    private void controleRepertoireNBSTARVALUE(String repertoire, RepertoirePhoto repPhoto, EleChamp ele) throws SQLException {
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

    public void controleChamp(String repertoire, RepertoirePhoto repPhoto, EleChamp ele) throws SQLException {
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

    private void testElementChamp(String repertoire, RepertoirePhoto repPhoto, String elechamp, EleChamp ele) throws SQLException {
        switch (elechamp) {
            case ControleRepertoire.DATE_DATE:
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
                ele.setRetourToTrue();
                if (nbelements == 0) {
                    ele.setRetourToFalse(String.valueOf(nbelements), "Phase0-pbrepertoire_zeroelements_" + "(0ele)"
                            + repertoire.replace(repPhoto.getRepertoire() + "\\", "").replace(ctx.getRepertoire50Phototheque(), "").replace("_", " "));
                }
                break;
            case ControleRepertoire.NB_SELECTIONNER:
                ele.setRetourToTrue();
                if (nbSelectionner == 0) {
                    ele.setRetourToFalse(String.valueOf(nbSelectionner), "Phase1_1-zeroElePhotoSelectionner_" + "(0select)"
                            + "/" + String.format("%05d", limitemaxfolderphoto) + " : "
                            + repertoire.replace(repPhoto.getRepertoire() + "\\", "").replace(ctx.getRepertoire50Phototheque(), "").replace("_", " "));
                }
                break;
            case ControleRepertoire.NB_NONSELECTIONNER:
                controleMutualise(ele, nbNonSelectionner != 0, nbNonSelectionner, "Phase1_1-nbPhotoNonSelectionner_", "(nbNselect)");
                break;
            case ControleRepertoire.NB_PHOTOAPURGER:
                controleMutualise(ele, nbphotoapurger > 0, nbphotoapurger, "Phase1_2-nbPhotoAPurge_", "(nbpurge)");
                break;
            case ControleRepertoire.NB_LIMITEMAXFOLDER:
                ele.setRetourToTrue();
                if (limitemaxfolderphoto == 0 && limitemaxfoldervideo == 0 ) {
                    ele.setRetourToFalse(String.valueOf(limitemaxfolderphoto), "Phase0-pbrepertoire_limitemaxazero_" + "(noLMax)"
                            + repertoire.replace(repPhoto.getRepertoire() + "\\", "").replace(ctx.getRepertoire50Phototheque(), "").replace("_", " "));
                }
                break;
            default:
                String txt = "elechamp.elechamp=" + elechamp + " inconnu ";
                LOGGER.debug(() -> txt);
                throw new IllegalStateException(txt);
        }
    }

    private void controleMutualise(EleChamp ele, boolean b, int nbphotoapurger, String s, String s2) {
        ele.setRetourToTrue();
        if (b) {
            ele.setRetourToFalse(String.valueOf(nbphotoapurger), s + s2
                    + String.format("%05d", nbphotoapurger) + "/" + String.format("%05d", limitemaxfolderphoto));
        }
    }

    public List<String> getPreviewPhoto() {
        return previewPhoto;
    }

    public void setPreviewPhoto(List<String> previewPhoto) {
        this.previewPhoto = previewPhoto;
    }

    public List<String> getLstPhoto() {
        return lstPhoto;
    }

    public void setLstPhoto(List<String> lstPhoto) {
        this.lstPhoto = lstPhoto;
    }

    public RepertoirePhoto getRepPhoto() {
        return repPhoto;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public List<EleChamp> getListOfControleValRepertoire() {
        return listOfControleValRepertoire;
    }

    public void setListOfControleValRepertoire(List<EleChamp> listOfControleValRepertoire) {
        this.listOfControleValRepertoire = listOfControleValRepertoire;

        valOk = true;

        //Resutlat analyse reprertoire
        ListIterator<EleChamp> champIte = listOfControleValRepertoire.listIterator();
        while (champIte.hasNext()) {
            EleChamp elechamp = champIte.next();
            valOk = valOk && elechamp.isRetourControle();
        }


    }

    public List<EleChamp> getListOfControleNom() {
        return listOfControleNom;
    }

    public void setListOfControleNom(List<EleChamp> listOfControleNom) {

        this.listOfControleNom = listOfControleNom;

        nomOk = true;

        //Resutlat analyse nom reprertoire
        ListIterator<EleChamp> champIteNom = listOfControleNom.listIterator();
        while (champIteNom.hasNext()) {
            EleChamp elechamp = champIteNom.next();
            nomOk = nomOk && elechamp.isRetourControle();
        }


    }

    public Boolean getNomOk() {
        return nomOk;
    }

    public Boolean getValOk() {
        return valOk;
    }

    public Boolean isRepertoireValide() {
        return nomOk && valOk;
    }

}
