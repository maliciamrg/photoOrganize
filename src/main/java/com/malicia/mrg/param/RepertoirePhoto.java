package com.malicia.mrg.param;


import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class RepertoirePhoto extends Serialize {

    private String repertoire;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> maxStar;
    private List<ParamZone> pZone;

    public RepertoirePhoto() {
        super();
    }

    public RepertoirePhoto(String repertoireIn, int uniteDeJourIn, int nbMaxParUniteDeJourIn) {
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

    public List<ParamZone> getpZone() {
        return pZone;
    }

    public void setpZone(List<ParamZone> pZone) {
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
        pZone.add(new ParamZone(isEditable, valeurParDefaut, isValditationFacultative));
    }

    public void addParamZone(String isEditableVirgule, String valeurParDefautVirgule, String isValditationFacultativeVirgule) {
        String[] arrEditableVirgule = isEditableVirgule.split(",");
        String[] arrvaleurParDefautVirgule = valeurParDefautVirgule.split(",");
        String[] arrisValditationFacultativeVirgule = isValditationFacultativeVirgule.split(",");
        for (int i = 0; i < arrEditableVirgule.length; i++) {
            pZone.add(new ParamZone(
                    arrEditableVirgule[i].compareTo("Close") == 0 ? false : true,
                    arrvaleurParDefautVirgule[i],
                    arrisValditationFacultativeVirgule[i].compareTo("Facul") == 0 ? false : true));
        }

    }

    public static class ParamZone {

        public String valeurDefault;
        public Boolean validationFacultative;
        public Boolean editable;

        public ParamZone() {
            super();
        }

        public ParamZone(Boolean isEditable, String valeurParDefaut, Boolean isValidatationFacultative) {
            editable = isEditable;
            valeurDefault = valeurParDefaut;
            validationFacultative = isValidatationFacultative;
        }
    }
}
