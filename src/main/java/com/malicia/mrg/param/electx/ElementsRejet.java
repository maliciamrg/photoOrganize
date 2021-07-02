package com.malicia.mrg.param.electx;

import com.malicia.mrg.util.Serialize;


import java.util.ArrayList;
import java.util.List;

public class ElementsRejet {

    public List<String> arrayNomSubdirectoryRejet = new ArrayList<>();
    public List<String> arrayNomFileRejet = new ArrayList<>();
    public List<String> arrayNomFileRejetSup = new ArrayList<>();
    public String extFileRejet;

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

    @Override
    public String toString() {
        return "ElementsRejet{" +
                "arrayNomSubdirectoryRejet=" + arrayNomSubdirectoryRejet +
                ", arrayNomFileRejet=" + arrayNomFileRejet +
                ", arrayNomFileRejetSup=" + arrayNomFileRejetSup +
                ", extFileRejet='" + extFileRejet + '\'' +
                '}';
    }
}
