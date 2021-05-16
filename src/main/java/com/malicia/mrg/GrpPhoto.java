package com.malicia.mrg;

import com.malicia.mrg.model.ElementFichier;

import java.util.ArrayList;
import java.util.List;


public class GrpPhoto {
    private long firstDate;
    List<ElementFichier> lstEleFile = new ArrayList<>();
    private int[] arrayRep = new int[Context.IREP_NEW+1];

    public GrpPhoto() {
        // Do nothing
    }

    public int getArrayRep(int i) {
        return arrayRep[i];
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
        int numRep = eleFile.getNumRep();
        if (numRep > -1) {
            arrayRep[numRep] += 1;
        }

        lstEleFile.add(eleFile);
    }

    public long size() {
        return lstEleFile.size();
    }

    public void addAll(GrpPhoto listEletmp) {
        lstEleFile.addAll(listEletmp.lstEleFile);
    }

}
