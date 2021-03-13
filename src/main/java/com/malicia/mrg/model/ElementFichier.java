package com.malicia.mrg.model;

public class ElementFichier {
    private final String absolutePath;
    private final String pathFromRoot;
    private final String lcIdxFilename;
    private final String fileIdLocal;
    private int numRep;

    public ElementFichier(String absolutePath, String pathFromRoot, String lcIdxFilename, String fileIdLocal) {
        this.absolutePath = absolutePath;
        this.pathFromRoot = pathFromRoot;
        this.lcIdxFilename = lcIdxFilename;
        this.fileIdLocal = fileIdLocal;
        this.numRep = -1;
    }

    public ElementFichier(String absolutePath, String pathFromRoot, String lcIdxFilename, String fileIdLocal , int numRep) {
        this.absolutePath = absolutePath;
        this.pathFromRoot = pathFromRoot;
        this.lcIdxFilename = lcIdxFilename;
        this.fileIdLocal = fileIdLocal;
        this.numRep = numRep;
    }


    public String getLcIdxFilename() {
        return lcIdxFilename;
    }

    public String getFileIdLocal() {
        return fileIdLocal;
    }

    public String getPath() {
        return absolutePath + pathFromRoot + lcIdxFilename;
    }

    public int getNumRep() {
        return numRep;
    }
}
