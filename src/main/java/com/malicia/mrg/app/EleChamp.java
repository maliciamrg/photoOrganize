package com.malicia.mrg.app;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importJson.ControleRepertoire;
import com.malicia.mrg.param.importJson.RepertoirePhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

class EleChamp {

    private static final Logger LOGGER = LogManager.getLogger(EleChamp.class);

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

    public void controleChamp(Database dbLr, String repertoire, RepertoirePhoto repPhoto) throws SQLException {
        LOGGER.debug("controleChamp : " + getcChamp());

        retourControle = false;

        String[] arrayChamp = getcChamp().split("\\|");
        // iterating over an array
        for (String elechamp : arrayChamp) {

            if (!retourControle) {

                int limitemaxfolder = 0;
                int nbSelectionner = 0;
                int nbphotoapurger = 0;
                int nbelements = 0;
                switch (elechamp) {
                    case ControleRepertoire.£_DATE_£:
                        String date = dbLr.getDate(repertoire);
                        setRetourToFalse(date);
                        if (date.compareTo(oValue) == 0) {
                            setRetourToTrue();
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
                        if (dbLr.isValueInTag(getoValue(), nettoyageTag(elechamp))) {
                            setRetourToTrue();
                        } else {
                            setRetourToFalse(dbLr.getValueForTag(nettoyageTag(elechamp)).toString());
                        }
                        break;
                    case ControleRepertoire.NB_STAR_VALUE:
                        nbSelectionner = dbLr.nb_pick(repertoire);
                        Map<String, Integer> starValue = dbLr.getStarValue(repertoire);
                        List<Integer> ratio = repPhoto.getratioStarMax();
                        String res = "";
                        boolean allStarGood = true;
                        for (int i = 1; i < 6; i++) {
                            int nbmax = (int) Math.ceil((ratio.get(i - 1) * Double.valueOf(nbSelectionner)) / 100);
                            LOGGER.debug("(" + i + ")" + " ---star-: " + starValue.get(String.valueOf(i)) + " ---ratio-: " + ratio.get(i - 1) + " ---nbmax-: " + nbmax);
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
                        nbSelectionner = dbLr.nb_pick(repertoire);
                        setRetourToTrue();
                        if (nbSelectionner == 0) {
                            setRetourToFalse(String.valueOf(nbSelectionner));
                        }
                        break;
                    case ControleRepertoire.NB_PHOTOAPURGER:
                        limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(repertoire))) / Double.valueOf(repPhoto.getUniteDeJour()));
                        nbSelectionner = dbLr.nb_pick(repertoire);
                        nbphotoapurger = nbSelectionner - limitemaxfolder;
                        setRetourToTrue();
                        if (nbphotoapurger > 0) {
                            setRetourToFalse(String.valueOf(nbphotoapurger));
                        }
                        break;
                    case ControleRepertoire.NB_LIMITEMAXFOLDER:
                        limitemaxfolder = (int) ((repPhoto.getNbMaxParUniteDeJour() * Math.ceil(dbLr.nbjourfolder(repertoire))) / Double.valueOf(repPhoto.getUniteDeJour()));
                        setRetourToTrue();
                        if (limitemaxfolder == 0) {
                            setRetourToFalse(String.valueOf(limitemaxfolder));
                        }
                        break;
                    default:
                        String txt = "elechamp.elechamp=" + elechamp + " inconnu ";
                        LOGGER.debug(() -> txt);
                        throw new IllegalStateException(txt);
                }
            }
        }
    }

    private String nettoyageTag(String getcChamp) {
        return getcChamp.replaceAll("@", "");
    }

    private void setRetourToFalse(String iretourControle) {
        this.infoRetourControle += iretourControle;
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
