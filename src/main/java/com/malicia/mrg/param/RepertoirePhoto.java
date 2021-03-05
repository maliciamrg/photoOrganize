package com.malicia.mrg.param;


import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class RepertoirePhoto extends Serialize {

    private String repertoire;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> ratioStarMax;
    private List<ParamZone> pZone;
    private List<String> nomRepertoire;

    public RepertoirePhoto() {
        super();
    }

    public RepertoirePhoto(String repertoireIn, int uniteDeJourIn, int nbMaxParUniteDeJourIn) {
        repertoire = repertoireIn;
        uniteDeJour = uniteDeJourIn;
        nbMaxParUniteDeJour = nbMaxParUniteDeJourIn;
        ratioStarMax = FXCollections.observableArrayList();
        pZone = FXCollections.observableArrayList();
        nomRepertoire = FXCollections.observableArrayList();
    }

    public List<String> getNomRepertoire() {
        return nomRepertoire;
    }

    public void setNomRepertoire(List<String> nomRepertoire) {
        this.nomRepertoire = nomRepertoire;
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

    public List<Integer> getratioStarMax() {
        return ratioStarMax;
    }

    public void setratioStarMax(List<Integer> ratioStarMax) {
        this.ratioStarMax = ratioStarMax;
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

    public void addratioStarMax(int ratioStarMaxIn) {
        ratioStarMax.add(ratioStarMaxIn);
    }

    public void addratioStarMax(String ratioStarMaxInVirgule) {
        String[] ratioratioStarMax = ratioStarMaxInVirgule.split(",");
        for (int i = 0; i < ratioratioStarMax.length; i++) {
            ratioStarMax.add(Integer.parseInt(ratioratioStarMax[i]));
        }
    }

    public void addParamZone(String valeurParDefaut, Boolean isValditationFacultative) {
        pZone.add(new ParamZone(valeurParDefaut, isValditationFacultative));
    }

    public void addParamZone( String valeurParDefautVirgule, String isValditationFacultativeVirgule) {
        String[] arrvaleurParDefautVirgule = valeurParDefautVirgule.split(",");
        String[] arrisValditationFacultativeVirgule = isValditationFacultativeVirgule.split(",");
        for (int i = 0; i < arrvaleurParDefautVirgule.length; i++) {
            pZone.add(new ParamZone(
                    arrvaleurParDefautVirgule[i],
                    arrisValditationFacultativeVirgule[i].compareTo("Facul") != 0));
        }

    }

    public static class ParamZone {

        public String valeurAdmise;

        public Boolean getObligatoire() {
            return obligatoire;
        }

        public void setObligatoire(Boolean obligatoire) {
            this.obligatoire = obligatoire;
        }

        public Boolean obligatoire;

        public ParamZone() {
            super();
        }

        public ParamZone(String valeurParDefaut, Boolean isValidatationFacultative) {
            valeurAdmise = valeurParDefaut;
            obligatoire = isValidatationFacultative;
        }
    }
}
