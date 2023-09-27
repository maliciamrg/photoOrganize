package com.malicia.mrg.model;

public class ElementFichier {
    private final String absolutePath;
    private final String pathFromRoot;
    private final String lcIdxFilename;
    private final String fileIdLocal;
    private final int numRep;
    private long captureTime;
    private long mint;
    private long maxt;
    public ElementFichier(String absolutePath, String pathFromRoot, String lcIdxFilename, String fileIdLocal) {
        this.absolutePath = absolutePath;
        this.pathFromRoot = pathFromRoot;
        this.lcIdxFilename = lcIdxFilename;
        this.fileIdLocal = fileIdLocal;
        this.numRep = -1;
    }

    public ElementFichier(String absolutePath, String pathFromRoot, String lcIdxFilename, String fileIdLocal, int numRep) {
        this.absolutePath = absolutePath;
        this.pathFromRoot = pathFromRoot;
        this.lcIdxFilename = lcIdxFilename;
        this.fileIdLocal = fileIdLocal;
        this.numRep = numRep;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getPathFromRoot() {
        return pathFromRoot;
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

    public void setcaptureTime(long captureTime) {
        this.captureTime = captureTime;
    }

    public void setmint(long mint) {
        this.mint = mint;
    }

    public void setmaxt(long maxt) {
        this.maxt = maxt;
    }

    @Override
    public String toString() {
        return "ElementFichier{" +
                "mint=" + String.format("%012d", mint) +
                ", " +
                "captureTime=" + String.format("%012d", captureTime) +
                ", " +
                "maxt=" + String.format("%012d", maxt) +
                ", " +
                "lcIdxFilename='" + lcIdxFilename + "'" +
                '}';
    }
}
