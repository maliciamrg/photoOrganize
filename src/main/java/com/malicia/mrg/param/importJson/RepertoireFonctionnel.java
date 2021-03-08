package com.malicia.mrg.param.importJson;

import com.malicia.mrg.util.Serialize;

public class RepertoireFonctionnel extends Serialize {

    private String repertoireDestZip;
    private String repertoireRoamingAdobeLightroom;
    private String repertoire50Phototheque;
    private String repertoire50NEW;
    private String catalogLrcat;

    private String repertoire00NEW;
    private String repertoireCatalog;

    public RepertoireFonctionnel() {
//        this.repertoireDestZip = null; //"D:\95_Boite_a_outils\LightroomConfigSauve\LightroomSauve-00%num%.zip"
//        this.repertoireRoamingAdobeLightroom = null; //"C:\Users\professorX\AppData\Roaming\Adobe\Lightroom"
//        this.repertoire50Phototheque = null; //"D:\50_Phototheque\"
//        this.repertoire00NEW = null; //"P:\00_NEW" "P:\50_Phototheque" "P:\20_Portfolio"
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
