package com.malicia.mrg.param.electx;


import com.malicia.mrg.util.Serialize;


import java.util.ArrayList;
import java.util.List;

public class RepertoirePhoto {

    public boolean rapprochermentNewOk;
    public String nomunique;
    public String repertoire;
    public int uniteDeJour;
    public int nbMaxParUniteDeJour;
    public List<Integer> ratioStarMax;
    public List<String> zoneValeurAdmise;
    private List<String> nomRepertoire;

    public RepertoirePhoto() {
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
        return "RepertoirePhoto{" +
                "rapprochermentNewOk=" + rapprochermentNewOk +
                ", nomunique='" + nomunique + '\'' +
                ", repertoire='" + repertoire + '\'' +
                ", uniteDeJour=" + uniteDeJour +
                ", nbMaxParUniteDeJour=" + nbMaxParUniteDeJour +
                ", ratioStarMax=" + ratioStarMax +
                ", zoneValeurAdmise=" + zoneValeurAdmise +
                ", nomRepertoire=" + nomRepertoire +
                '}';
    }
}
