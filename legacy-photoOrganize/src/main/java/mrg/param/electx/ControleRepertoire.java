package mrg.param.electx;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControleRepertoire {

    public static final String DATE_DATE = "£DATE£";
    public static final String TAG_ACTION = "@10_Action@";
    public static final String TAG_PIECE = "@10_Piece@";
    public static final String TAG_CHANTIER = "@10_Chantier@";
    public static final String TAG_EVENT = "@00_EVENT@";
    public static final String TAG_PHOTOGRAPHY = "@00_PHOTOGRAPHY@";
    public static final String TAG_WHERE = "@00_WHERE@";
    public static final String TAG_WHAT = "@00_WHAT@";
    public static final String TAG_WHO = "@00_WHO@";
    public static final String NB_STAR_VALUE = "starValue";
    public static final String NB_SELECTIONNER = "nbSelectionner";
    public static final String NB_NONSELECTIONNER = "nbNonSelectionner";
    public static final String NB_PHOTOAPURGER = "nbphotoapurger";
    public static final String NB_ELEMENTS = "nbelements";
    public static final String NB_LIMITEMAXFOLDER = "limitemaxfolder";
    public static final String PREVIEW_PHOTO = "previewphoto";
    public static final String CARAC_SEPARATEUR = "_";
    public static final String CARAC_EMPTY = "#";
    public static final String CARAC_VOID = "-";
    public static final String FORMATDATE_YYYY_MM_DD = "yyyy-MM-dd";

    private static final String[] lstTAG = {
            TAG_ACTION,
            TAG_PIECE,
            TAG_CHANTIER,
            TAG_EVENT,
            TAG_PHOTOGRAPHY,
            TAG_WHERE,
            TAG_WHAT,
            TAG_WHO
    };

    public List<String> listControleRepertoire = new ArrayList<>();

    public static String nettoyageTag(String getcChamp) {
        return getcChamp.replace("@", "");
    }

    public static boolean isTagContains(String lblTag) {
        return Arrays.asList(lstTAG).contains(lblTag);
    }

    public List<String> getlistControleRepertoire() {
        return listControleRepertoire;
    }

    @Override
    public String toString() {
        return  "\n" + "ControleRepertoire{" + "\n" +
                "listControleRepertoire=" + listControleRepertoire + "\n" +
                '}' + "\n" ;
    }
}

