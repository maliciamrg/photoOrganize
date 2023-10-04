package com.malicia.mrg;

import com.malicia.mrg.model.ElementFichier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GrpPhoto {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    List<ElementFichier> lstEleFile = new ArrayList<>();
    List<ElementFichier> lstEleFileWithoutDuplicates = new ArrayList<>();
    private long firstDate;
    private final int[] arrayRep = new int[Context.IREP_NEW + 1];

    public GrpPhoto() {
        // Do nothing
    }

    public List<ElementFichier> getLstEleFileWithoutDuplicates() {
        return lstEleFileWithoutDuplicates;
    }

    public void setLstEleFileWithoutDuplicates(List<ElementFichier> lstEleFileWithoutDuplicates) {
        this.lstEleFileWithoutDuplicates = lstEleFileWithoutDuplicates;
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

    public void deBounce() {
        lstEleFileWithoutDuplicates = lstEleFile.stream()
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList());
        if (lstEleFile.size() != lstEleFileWithoutDuplicates.size()) {
            LOGGER.info("lstEleFile debounce de " + lstEleFile.size() + " pour " + lstEleFileWithoutDuplicates.size() + " elements ");
        }
    }
}
