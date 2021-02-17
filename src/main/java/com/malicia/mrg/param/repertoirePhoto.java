package com.malicia.mrg.param;


import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class repertoirePhoto extends Serialize {

    private String repertoire;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> maxStar;
    private List<paramZone> pZone;

    public repertoirePhoto() {
        super();
    }

    public repertoirePhoto(String repertoireIn, int uniteDeJourIn, int nbMaxParUniteDeJourIn) {
        repertoire = repertoireIn;
        uniteDeJour = uniteDeJourIn;
        nbMaxParUniteDeJour = nbMaxParUniteDeJourIn;
        maxStar = FXCollections.observableArrayList();
        pZone = FXCollections.observableArrayList();
    }

    public int getUniteDeJour() {
        return uniteDeJour;
    }

    public void setUniteDeJour(int uniteDeJour) {
        this.uniteDeJour = uniteDeJour;
    }

    public int getNbMaxParUniteDeJour() {
        return nbMaxParUniteDeJour;
    }

    public void setNbMaxParUniteDeJour(int nbMaxParUniteDeJour) {
        this.nbMaxParUniteDeJour = nbMaxParUniteDeJour;
    }

    public List<Integer> getMaxStar() {
        return maxStar;
    }

    public void setMaxStar(List<Integer> maxStar) {
        this.maxStar = maxStar;
    }

    public List<paramZone> getpZone() {
        return pZone;
    }

    public void setpZone(List<paramZone> pZone) {
        this.pZone = pZone;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(String repertoire) {
        this.repertoire = repertoire;
    }

    public void addMaxStar(int maxStarIn) {
        maxStar.add(maxStarIn);
    }

    public void addMaxStar(String maxStarInVirgule) {
        String[] ratioMaxStar = maxStarInVirgule.split(",");
        for (int i = 0; i < ratioMaxStar.length; i++) {
            maxStar.add(Integer.parseInt(ratioMaxStar[i]));
        }
    }

    public void addParamZone(Boolean isEditable, String valeurParDefaut, Boolean isValditationFacultative) {
        pZone.add(new paramZone(isEditable, valeurParDefaut, isValditationFacultative));
    }

    public void addParamZone(String isEditableVirgule, String valeurParDefautVirgule, String isValditationFacultativeVirgule) {
        String[] arrEditableVirgule = isEditableVirgule.split(",");
        String[] arrvaleurParDefautVirgule = valeurParDefautVirgule.split(",");
        String[] arrisValditationFacultativeVirgule = isValditationFacultativeVirgule.split(",");
        for (int i = 0; i < arrEditableVirgule.length; i++) {
            pZone.add(new paramZone(
                    arrEditableVirgule[i].compareTo("Close") == 0 ? false : true,
                    arrvaleurParDefautVirgule[i],
                    arrisValditationFacultativeVirgule[i].compareTo("Facul") == 0 ? false : true));
        }

    }

    public static class paramZone {

        public String valeurDefaut;
        public Boolean validationFacultative;
        public Boolean editable;

        public paramZone() {
            super();
        }

        public paramZone(Boolean isEditable, String valeurParDefaut, Boolean isValidatationFacultative) {
            editable = isEditable;
            valeurDefaut = valeurParDefaut;
            validationFacultative = isValidatationFacultative;
        }
    }
}
