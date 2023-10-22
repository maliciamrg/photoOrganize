package com.malicia.mrg.param.electx;

import java.util.ArrayList;
import java.util.List;

public class ElementsRejet {

    private List<String> arrayNomSubdirectoryRejet = new ArrayList<>();
    private List<String> arrayExtensionFileRejetSup = new ArrayList<>();
    private String extFileRejet;

    public ElementsRejet() {
        // Do nothing because of X and Y
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

    public List<String> getarrayExtensionFileRejetSup() {
        return arrayExtensionFileRejetSup;
    }

    public void setarrayExtensionFileRejetSup(List<String> arrayExtensionFileRejetSup) {
        this.arrayExtensionFileRejetSup = arrayExtensionFileRejetSup;
    }

    @Override
    public String toString() {
        return "ElementsRejet{" +
                "arrayNomSubdirectoryRejet=" + arrayNomSubdirectoryRejet +
                ", arrayExtensionFileRejetSup=" + arrayExtensionFileRejetSup +
                ", extFileRejet='" + extFileRejet + '\'' +
                '}';
    }
}
