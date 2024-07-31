package malicia.mrg.photo.organize.infrastructure;

import java.util.List;

public class ParamRepertoire {
    private String repertoire;
    private String nomunique;
    private Integer uniteDeJour;
    private Integer nbMaxParUniteDeJour;
    private List<Integer> ratioStarMax;
    private List<String> zoneValeurAdmise;
    private Boolean rapprochermentNewOk;

    public String getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(String repertoire) {
        this.repertoire = repertoire;
    }

    public String getNomunique() {
        return nomunique;
    }

    public void setNomunique(String nomunique) {
        this.nomunique = nomunique;
    }

    public Integer getUniteDeJour() {
        return uniteDeJour;
    }

    public void setUniteDeJour(Integer uniteDeJour) {
        this.uniteDeJour = uniteDeJour;
    }

    public Integer getNbMaxParUniteDeJour() {
        return nbMaxParUniteDeJour;
    }

    public void setNbMaxParUniteDeJour(Integer nbMaxParUniteDeJour) {
        this.nbMaxParUniteDeJour = nbMaxParUniteDeJour;
    }

    public List<Integer> getRatioStarMax() {
        return ratioStarMax;
    }

    public void setRatioStarMax(List<Integer> ratioStarMax) {
        this.ratioStarMax = ratioStarMax;
    }

    public List<String> getZoneValeurAdmise() {
        return zoneValeurAdmise;
    }

    public void setZoneValeurAdmise(List<String> zoneValeurAdmise) {
        this.zoneValeurAdmise = zoneValeurAdmise;
    }

    public Boolean getRapprochermentNewOk() {
        return rapprochermentNewOk;
    }

    public void setRapprochermentNewOk(Boolean rapprochermentNewOk) {
        this.rapprochermentNewOk = rapprochermentNewOk;
    }

}
