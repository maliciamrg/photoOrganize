package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.spi.IParams;

import java.util.List;

public class Params implements IParams {
    private List<String> allowedExtensions;
    private List<String> subdirectoryRejet;

    @Override
    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    @Override
    public List<String> getSubdirectoryRejet() {
        return subdirectoryRejet;
    }

    public void setSubdirectoryRejet(List<String> subdirectoryRejet) {
        this.subdirectoryRejet = subdirectoryRejet;
    }
}
