package com.malicia.mrg.app.rep;

import com.malicia.mrg.param.importjson.RepertoirePhoto;

import java.util.List;
import java.util.ListIterator;

public class BlocRetourRepertoire {

    public List<String> getPreviewPhoto() {
        return previewPhoto;
    }

    public void setPreviewPhoto(List<String> previewPhoto) {
        this.previewPhoto = previewPhoto;
    }

    public List<String> getLstPhoto() {
        return lstPhoto;
    }

    public void setLstPhoto(List<String> lstPhoto) {
        this.lstPhoto = lstPhoto;
    }

    private List<String> previewPhoto;
    private List<String> lstPhoto;

    public RepertoirePhoto getRepPhoto() {
        return repPhoto;
    }

    private RepertoirePhoto repPhoto;
    private String repertoire;
    private List<EleChamp> listOfControleValRepertoire;
    private List<EleChamp> listOfControleNom;
    private Boolean nomOk;
    private Boolean valOk;

    public BlocRetourRepertoire(RepertoirePhoto repPhoto, String repertoire) {
        this.repPhoto = repPhoto;
        this.repertoire = repertoire;
        nomOk = true;
        valOk = true;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public List<EleChamp> getListOfControleValRepertoire() {
        return listOfControleValRepertoire;
    }

    public void setListOfControleValRepertoire(List<EleChamp> listOfControleValRepertoire) {
        this.listOfControleValRepertoire = listOfControleValRepertoire;

        valOk = true;

        //Resutlat analyse reprertoire
        ListIterator<EleChamp> champIte = listOfControleValRepertoire.listIterator();
        while (champIte.hasNext()) {
            EleChamp elechamp = champIte.next();
            valOk = valOk && elechamp.isRetourControle();
        }


    }

    public List<EleChamp> getListOfControleNom() {
        return listOfControleNom;
    }

    public void setListOfControleNom(List<EleChamp> listOfControleNom) {

        this.listOfControleNom = listOfControleNom;

        nomOk = true;

        //Resutlat analyse nom reprertoire
        ListIterator<EleChamp> champIteNom = listOfControleNom.listIterator();
        while (champIteNom.hasNext()) {
            EleChamp elechamp = champIteNom.next();
            nomOk = nomOk && elechamp.isRetourControle();
        }


    }

    public Boolean getNomOk() {
        return nomOk;
    }

    public Boolean getValOk() {
        return valOk;
    }

    public Boolean isRepertoireValide() {
        return nomOk && valOk;
    }
}