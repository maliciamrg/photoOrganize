package com.malicia.mrg.param.importjson;

import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class ControleRepertoire extends Serialize {

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
    public static final String NB_PHOTOAPURGER = "nbphotoapurger";
    public static final String NB_ELEMENTS = "nbelements";
    public static final String NB_LIMITEMAXFOLDER = "limitemaxfolder";
    public static final String CARAC_SEPARATEUR = "_";
    public static final String CARAC_EMPTY = "#";
    public static final String CARAC_VOID = "-";
    public static final String FORMATDATE_YYYY_MM_DD = "yyyy-MM-dd";

    private final List<String> listControleRepertoire;

    public ControleRepertoire() {
        listControleRepertoire = FXCollections.observableArrayList();
        listControleRepertoire.add(NB_STAR_VALUE);
        listControleRepertoire.add(NB_SELECTIONNER);
        listControleRepertoire.add(NB_PHOTOAPURGER);
        listControleRepertoire.add(NB_LIMITEMAXFOLDER);
    }

    public List<String> getlistControleRepertoire() {
        return listControleRepertoire;
    }
}

