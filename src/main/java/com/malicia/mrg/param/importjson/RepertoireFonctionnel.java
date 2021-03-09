package com.malicia.mrg.param.importjson;

import com.malicia.mrg.util.Serialize;

public class RepertoireFonctionnel extends Serialize {

    private String repertoireDestZip;
    private String nbRotateRepertoireDestZip;
    private String repertoireRoamingAdobeLightroom;
    private String repertoire50Phototheque;
    private String repertoire50NEW;
    private String catalogLrcat;
    private String repertoire00NEW;
    private String repertoireCatalog;
    private String[] rsyncinclude;
    private String[] rsyncexclude;
    private String[] repertoiresyncsource;
    private String repertoiresyncdest;

    public RepertoireFonctionnel() {
        // Do nothing because of X and Y
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

}
