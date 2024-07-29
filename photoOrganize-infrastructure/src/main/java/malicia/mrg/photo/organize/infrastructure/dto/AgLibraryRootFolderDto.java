package malicia.mrg.photo.organize.infrastructure.dto;

public class AgLibraryRootFolderDto {


    private Long idLocal;


    private String idGlobal;


    private String absolutePath;


    private String name;


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
