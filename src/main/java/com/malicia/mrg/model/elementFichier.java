package com.malicia.mrg.model;

public class elementFichier {
    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getPathFromRoot() {
        return pathFromRoot;
    }

    public String getLcIdxFilename() {
        return lcIdxFilename;
    }

    public String getFile_id_local() {
        return file_id_local;
    }

    public String getFolder_id_local() {
        return folder_id_local;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public long getCaptureTime() {
        return captureTime;
    }

    public long getMint() {
        return mint;
    }

    public long getMaxt() {
        return maxt;
    }

    public Double getRating() {
        return rating;
    }

    public Double getPick() {
        return pick;
    }

    private final String absolutePath;
    private final String pathFromRoot;
    private final String lcIdxFilename;
    private final String file_id_local;
    private final String folder_id_local;
    private final String cameraModel;
    private final long captureTime;
    private final long mint;
    private final long maxt;
    private final Double rating;
    private final Double pick;

    public elementFichier(String absolutePath, String pathFromRoot, String lcIdxFilename, String file_id_local, String folder_id_local, String cameraModel, long captureTime, long mint, long maxt, Double rating, Double pick) {
        this.absolutePath = absolutePath;
        this.pathFromRoot = pathFromRoot;
        this.lcIdxFilename = lcIdxFilename;
        this.file_id_local = file_id_local;
        this.folder_id_local = folder_id_local;
        this.cameraModel = cameraModel;
        this.captureTime = captureTime;
        this.mint = mint;
        this.maxt = maxt;
        this.rating = rating;
        this.pick = pick;
    }

    public String getPath() {
        return absolutePath + pathFromRoot + lcIdxFilename;
    }
}
