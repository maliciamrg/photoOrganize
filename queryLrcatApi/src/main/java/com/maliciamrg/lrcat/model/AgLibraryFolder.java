package com.maliciamrg.lrcat.model;


import jakarta.persistence.*;

@Entity
@Table(name = "AgLibraryFolder")
public class AgLibraryFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long idLocal;

    @Column(name = "id_global", unique = true, nullable = false)
    private String idGlobal;

    @Column(name = "parentId", nullable = false)
    private Integer parentId;

    @Column(name = "pathFromRoot")
    private String pathFromRoot;

    @Column(name = "rootFolder")
    private Integer rootFolder;
    @Column(name = "visibility", nullable = false)
    private Integer visibility;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPathFromRoot() {
        return pathFromRoot;
    }

    public void setPathFromRoot(String pathFromRoot) {
        this.pathFromRoot = pathFromRoot;
    }

    public Integer getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(Integer rootFolder) {
        this.rootFolder = rootFolder;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }
}
