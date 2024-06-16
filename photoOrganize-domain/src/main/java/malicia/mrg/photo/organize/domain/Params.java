package malicia.mrg.photo.organize.domain;

import java.util.List;

public class Params {
    private List<String> allowedExtensions;
    private List<String> subdirectoryRejet;

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public List<String> getSubdirectoryRejet() {
        return subdirectoryRejet;
    }

    public void setSubdirectoryRejet(List<String> subdirectoryRejet) {
        this.subdirectoryRejet = subdirectoryRejet;
    }
}
