package com.maliciamrg.lrcat.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AgLibraryFile")
public class AgLibraryFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long idLocal;

    @Column(name = "id_global", unique = true, nullable = false)
    private String idGlobal;

    @Column(name = "baseName", nullable = false)
    private String baseName;

    @Column(name = "errorMessage")
    private String errorMessage;

    @Column(name = "errorTime")
    private LocalDateTime errorTime;

    @Column(name = "extension", nullable = false)
    private String extension;

    @Column(name = "externalModTime")
    private LocalDateTime externalModTime;

    @Column(name = "folder", nullable = false)
    private Integer folder;

    @Column(name = "idx_filename", nullable = false)
    private String idxFilename;

    @Column(name = "importHash")
    private String importHash;

    @Column(name = "lc_idx_filename", nullable = false)
    private String lcIdxFilename;

    @Column(name = "lc_idx_filenameExtension", nullable = false)
    private String lcIdxFilenameExtension;

    @Column(name = "md5")
    private String md5;

    @Column(name = "modTime")
    private LocalDateTime modTime;

    @Column(name = "originalFilename", nullable = false)
    private String originalFilename;

    @Column(name = "sidecarExtensions")
    private String sidecarExtensions;

    // Getters and Setters

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdGlobal() {
        return idGlobal;
    }

    public void setIdGlobal(String idGlobal) {
        this.idGlobal = idGlobal;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public LocalDateTime getExternalModTime() {
        return externalModTime;
    }

    public void setExternalModTime(LocalDateTime externalModTime) {
        this.externalModTime = externalModTime;
    }

    public Integer getFolder() {
        return folder;
    }

    public void setFolder(Integer folder) {
        this.folder = folder;
    }

    public String getIdxFilename() {
        return idxFilename;
    }

    public void setIdxFilename(String idxFilename) {
        this.idxFilename = idxFilename;
    }

    public String getImportHash() {
        return importHash;
    }

    public void setImportHash(String importHash) {
        this.importHash = importHash;
    }

    public String getLcIdxFilename() {
        return lcIdxFilename;
    }

    public void setLcIdxFilename(String lcIdxFilename) {
        this.lcIdxFilename = lcIdxFilename;
    }

    public String getLcIdxFilenameExtension() {
        return lcIdxFilenameExtension;
    }

    public void setLcIdxFilenameExtension(String lcIdxFilenameExtension) {
        this.lcIdxFilenameExtension = lcIdxFilenameExtension;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public LocalDateTime getModTime() {
        return modTime;
    }

    public void setModTime(LocalDateTime modTime) {
        this.modTime = modTime;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getSidecarExtensions() {
        return sidecarExtensions;
    }

    public void setSidecarExtensions(String sidecarExtensions) {
        this.sidecarExtensions = sidecarExtensions;
    }
}
