package com.malicia.mrg.param.importjson;

import com.malicia.mrg.util.Serialize;


import java.util.ArrayList;
import java.util.List;

public class ElementsRejet extends Serialize {

    private List<String> arrayNomSubdirectoryRejet = new ArrayList<>();
    private List<String> arrayNomFileRejet = new ArrayList<>();
    private List<String> arrayNomFileRejetSup = new ArrayList<>();
    private String extFileRejet;

    public ElementsRejet() {
        // Do nothing because of X and Y
    }

    public List<String> getArrayNomFileRejet() {
        return arrayNomFileRejet;
    }

    public void setArrayNomFileRejet(List<String> arrayNomFileRejet) {
        this.arrayNomFileRejet = arrayNomFileRejet;
    }

    public String getExtFileRejet() {
        return extFileRejet;
    }

    public void setExtFileRejet(String extFileRejet) {
        this.extFileRejet = extFileRejet;
    }

    public List<String> getArrayNomSubdirectoryRejet() {
        return arrayNomSubdirectoryRejet;
    }

    public void setArrayNomSubdirectoryRejet(List<String> arrayNomSubdirectoryRejet) {
        this.arrayNomSubdirectoryRejet = arrayNomSubdirectoryRejet;
    }

    public List<String> getArrayNomFileRejetSup() {
        return arrayNomFileRejetSup;
    }

    public void setArrayNomFileRejetSup(List<String> arrayNomFileRejetSup) {
        this.arrayNomFileRejetSup = arrayNomFileRejetSup;
    }
}
