package com.maliciamrg.lrcat.model;


import jakarta.persistence.*;


@Entity
@Table(name = "AgLibraryRootFolder")
public class AgLibraryRootFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long idLocal;

    @Column(name = "id_global", unique = true, nullable = false)
    private String idGlobal;

    @Column(name = "absolutePath", unique = true, nullable = false)
    private String absolutePath;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "relativePathFromCatalog")
    private String relativePathFromCatalog;

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

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelativePathFromCatalog() {
        return relativePathFromCatalog;
    }

    public void setRelativePathFromCatalog(String relativePathFromCatalog) {
        this.relativePathFromCatalog = relativePathFromCatalog;
    }
}
