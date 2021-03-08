package com.malicia.mrg.param.importjson;

import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class TriNew extends Serialize {

    public static final String FORMATDATE_YYYY_MM_DD = "yyyy-MM-dd";
    private String repertoire50NEW;
    private String repertoireKidz;
    private String repertoireBazar;
    private long thresholdNew;
    private String tempsAdherence;
    private List<String> listeModelKidz = FXCollections.observableArrayList();


    public TriNew() {
        // Do nothing because of X and Y
    }

    public String getRepertoireBazar() {
        return repertoireBazar;
    }

    public void setRepertoireBazar(String repertoireBazar) {
        this.repertoireBazar = repertoireBazar;
    }

    public String getTempsAdherence() {
        return tempsAdherence;
    }

    public void setTempsAdherence(String tempsAdherence) {
        this.tempsAdherence = tempsAdherence;
    }

    public String getRepertoire50NEW() {
        return repertoire50NEW;
    }

    public void setRepertoire50NEW(String repertoire50NEW) {
        this.repertoire50NEW = repertoire50NEW;
    }

    public String getRepertoireKidz() {
        return repertoireKidz;
    }

    public void setRepertoireKidz(String repertoireKidz) {
        this.repertoireKidz = repertoireKidz;
    }

    public long getThresholdNew() {
        return thresholdNew;
    }

    public void setThresholdNew(long thresholdNew) {
        this.thresholdNew = thresholdNew;
    }

    public List<String> getListeModelKidz() {
        return listeModelKidz;
    }

    public void setListeModelKidz(List<String> listeModelKidz) {
        this.listeModelKidz = listeModelKidz;
    }


}

