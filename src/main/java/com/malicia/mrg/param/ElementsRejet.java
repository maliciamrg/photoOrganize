package com.malicia.mrg.param;

import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class ElementsRejet extends Serialize {

    private List<String> arrayNomSubdirectoryRejet = FXCollections.observableArrayList();
    private List<String> arrayNomFileRejet = FXCollections.observableArrayList();
    private String extFileRejet;

    public ElementsRejet() {
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

}
