package mrg.param.electx;

import com.malicia.mrg.util.Serialize;

import java.util.Arrays;

public class RepertoireFonctionnel {

    public String repertoireDestZip;
    public String nbRotateRepertoireDestZip;
    public String repertoireRoamingAdobeLightroom;
    public String repertoire50Phototheque;
    public String repertoire50NEW;
    public String repertoireMidi2LRsetting;
    public String catalogLrcat;
    public String repertoire00NEW;
    public String repertoireCatalog;
    public String[] rsyncinclude;
    public String[] rsyncexclude;
    public String[] repertoiresyncsource;
    public String repertoiresyncdest;
    public String syncAmountDaysBetween;
    public String syncdestmouchard;

    public RepertoireFonctionnel() {
        // Do nothing because of X and Y
    }

    public String getRepertoireMidi2LRsetting() {
        return repertoireMidi2LRsetting;
    }

    public void setRepertoireMidi2LRsetting(String repertoireMidi2LRsetting) {
        this.repertoireMidi2LRsetting = repertoireMidi2LRsetting;
    }

    public String getSyncAmountDaysBetween() {
        return syncAmountDaysBetween;
    }

    public String getSyncdestmouchard() {
        return syncdestmouchard;
    }

    public String[] getRepertoiresyncsource() {
        return repertoiresyncsource;
    }

    public void setRepertoiresyncsource(String[] repertoiresyncsource) {
        this.repertoiresyncsource = repertoiresyncsource;
    }

    public String getRepertoiresyncdest() {
        return repertoiresyncdest;
    }

    public void setRepertoiresyncdest(String repertoiresyncdest) {
        this.repertoiresyncdest = repertoiresyncdest;
    }

    public String[] getRsyncinclude() {
        return rsyncinclude;
    }

    public void setRsyncinclude(String[] rsyncinclude) {
        this.rsyncinclude = rsyncinclude;
    }

    public String[] getRsyncexclude() {
        return rsyncexclude;
    }

    public void setRsyncexclude(String[] rsyncexclude) {
        this.rsyncexclude = rsyncexclude;
    }

    public String getNbRotateRepertoireDestZip() {
        return nbRotateRepertoireDestZip;
    }

    public void setNbRotateRepertoireDestZip(String nbRotateRepertoireDestZip) {
        this.nbRotateRepertoireDestZip = nbRotateRepertoireDestZip;
    }

    public String getRepertoire50NEW() {
        return repertoire50NEW;
    }

    public void setRepertoire50NEW(String repertoire50NEW) {
        this.repertoire50NEW = repertoire50NEW;
    }

    public String getCatalogLrcat() {
        return catalogLrcat;
    }

    public void setCatalogLrcat(String catalogLrcat) {
        this.catalogLrcat = catalogLrcat;
    }

    public String getRepertoireDestZip() {
        return repertoireDestZip;
    }

    public void setRepertoireDestZip(String repertoireDestZip) {
        this.repertoireDestZip = repertoireDestZip;
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return repertoireRoamingAdobeLightroom;
    }

    public void setRepertoireRoamingAdobeLightroom(String repertoireRoamingAdobeLightroom) {
        this.repertoireRoamingAdobeLightroom = repertoireRoamingAdobeLightroom;
    }

    public String getRepertoire50Phototheque() {
        return repertoire50Phototheque;
    }

    public void setRepertoire50Phototheque(String repertoire50Phototheque) {
        this.repertoire50Phototheque = repertoire50Phototheque;
    }

    public String getRepertoire00NEW() {
        return repertoire00NEW;
    }

    public void setRepertoire00NEW(String repertoire00NEW) {
        this.repertoire00NEW = repertoire00NEW;
    }

    public String getRepertoireCatalog() {
        return repertoireCatalog;
    }

    public void setRepertoireCatalog(String repertoireCatalog) {
        this.repertoireCatalog = repertoireCatalog;
    }

    @Override
    public String toString() {
        return "RepertoireFonctionnel{" +
                "repertoireDestZip='" + repertoireDestZip + '\'' +
                ", nbRotateRepertoireDestZip='" + nbRotateRepertoireDestZip + '\'' +
                ", repertoireRoamingAdobeLightroom='" + repertoireRoamingAdobeLightroom + '\'' +
                ", repertoire50Phototheque='" + repertoire50Phototheque + '\'' +
                ", repertoire50NEW='" + repertoire50NEW + '\'' +
                ", catalogLrcat='" + catalogLrcat + '\'' +
                ", repertoire00NEW='" + repertoire00NEW + '\'' +
                ", repertoireCatalog='" + repertoireCatalog + '\'' +
                ", rsyncinclude=" + Arrays.toString(rsyncinclude) +
                ", rsyncexclude=" + Arrays.toString(rsyncexclude) +
                ", repertoiresyncsource=" + Arrays.toString(repertoiresyncsource) +
                ", repertoiresyncdest='" + repertoiresyncdest + '\'' +
                ", syncAmountDaysBetween='" + syncAmountDaysBetween + '\'' +
                ", syncdestmouchard='" + syncdestmouchard + '\'' +
                '}';
    }
}
