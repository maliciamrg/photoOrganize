package com.maliciamrg.lrcat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AgLibraryKeyword")
public class AgLibraryKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long idLocal;

    @Column(name = "id_global", unique = true, nullable = false)
    private String idGlobal;

    @Column(name = "dateCreated", nullable = false)
    private String dateCreated;

    @Column(name = "genealogy", nullable = false)
    private String genealogy;

    @Column(name = "imageCountCache")
    private Integer imageCountCache = -1;

    @Column(name = "includeOnExport", nullable = false)
    private Integer includeOnExport = 1;

    @Column(name = "includeParents", nullable = false)
    private Integer includeParents = 1;

    @Column(name = "includeSynonyms", nullable = false)
    private Integer includeSynonyms = 1;

    @Column(name = "keywordType")
    private String keywordType;

    @Column(name = "lastApplied")
    private String lastApplied;

    @Column(name = "lc_name")
    private String lcName;

    @Column(name = "name")
    private String name;

    @Column(name = "parent")
    private Integer parent;

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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getGenealogy() {
        return genealogy;
    }

    public void setGenealogy(String genealogy) {
        this.genealogy = genealogy;
    }

    public Integer getImageCountCache() {
        return imageCountCache;
    }

    public void setImageCountCache(Integer imageCountCache) {
        this.imageCountCache = imageCountCache;
    }

    public Integer getIncludeOnExport() {
        return includeOnExport;
    }

    public void setIncludeOnExport(Integer includeOnExport) {
        this.includeOnExport = includeOnExport;
    }

    public Integer getIncludeParents() {
        return includeParents;
    }

    public void setIncludeParents(Integer includeParents) {
        this.includeParents = includeParents;
    }

    public Integer getIncludeSynonyms() {
        return includeSynonyms;
    }

    public void setIncludeSynonyms(Integer includeSynonyms) {
        this.includeSynonyms = includeSynonyms;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(String lastApplied) {
        this.lastApplied = lastApplied;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }
}
