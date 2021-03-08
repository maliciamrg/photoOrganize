package com.malicia.mrg;

import com.malicia.mrg.model.ElementFichier;

import java.util.ArrayList;
import java.util.List;

public class GrpPhoto {
    public long firstDate;
    List<ElementFichier> lstEleFile = new ArrayList();

    public GrpPhoto() {
        // Do nothing because of X and Y
    }

    public List<ElementFichier> getLstEleFile() {
        return lstEleFile;
    }

    public void setLstEleFile(List<ElementFichier> lstEleFile) {
        this.lstEleFile = lstEleFile;
    }

    public long getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(long firstDate) {
        this.firstDate = firstDate;
    }

    public void add(ElementFichier eleFile) {
        lstEleFile.add(eleFile);
    }

    public long size() {
        return lstEleFile.size();
    }

    public void addAll(GrpPhoto listEletmp) {
        lstEleFile.addAll(listEletmp.lstEleFile);
    }

}
