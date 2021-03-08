package com.malicia.mrg;

import com.malicia.mrg.model.elementFichier;

import java.util.ArrayList;
import java.util.List;

public class grpPhoto {
    public long firstDate;
    List<elementFichier> lstEleFile = new ArrayList();

    public grpPhoto() {
    }

    public long getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(long firstDate) {
        this.firstDate = firstDate;
    }

    public void add(elementFichier eleFile) {
        lstEleFile.add(eleFile);
    }

    public long size() {
        return lstEleFile.size();
    }

    public void addAll(grpPhoto listEletmp) {
        lstEleFile.addAll(listEletmp.lstEleFile);
    }

}
