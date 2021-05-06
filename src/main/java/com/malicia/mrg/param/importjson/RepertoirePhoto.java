package com.malicia.mrg.param.importjson;


import com.malicia.mrg.util.Serialize;


import java.util.ArrayList;
import java.util.List;

public class RepertoirePhoto extends Serialize {

    private boolean rapprochermentNewOk;
    private String repertoire;
    private int uniteDeJour;
    private int nbMaxParUniteDeJour;
    private List<Integer> ratioStarMax;
    private List<String> zoneValeurAdmise;
    private List<String> nomRepertoire;
    public RepertoirePhoto() {
        super();
    }

    public RepertoirePhoto(String repertoireIn, int uniteDeJourIn, int nbMaxParUniteDeJourIn) {
        repertoire = repertoireIn;
        uniteDeJour = uniteDeJourIn;
        nbMaxParUniteDeJour = nbMaxParUniteDeJourIn;
        ratioStarMax = new ArrayList<>();
        zoneValeurAdmise = new ArrayList<>();
        nomRepertoire = new ArrayList<>();
        rapprochermentNewOk = false;
    }

    public boolean isRapprochermentNewOk() {
        return rapprochermentNewOk;
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

    public List<String> getZoneValeurAdmise() {
        return zoneValeurAdmise;
    }

    public void setZoneValeurAdmise(List<String> zoneValeurAdmise) {
        this.zoneValeurAdmise = zoneValeurAdmise;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(String repertoire) {
        this.repertoire = repertoire;
    }

    @Override
    public String toString() {
        return repertoire;
    }
}
