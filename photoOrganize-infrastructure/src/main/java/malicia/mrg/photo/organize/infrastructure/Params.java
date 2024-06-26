package malicia.mrg.photo.organize.infrastructure;

import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Params implements IParams {

    private String lightroomDBPath;
    private List<String> subdirectoryRejet;
    private List<String> allowedExtensions;

    public String getLightroomDBPath() {
        return lightroomDBPath;
    }

    @Override
    public String getFolderdelim() {
        return null;
    }

    @Override
    public long getGmt01jan200112am() {
        return 0;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    @Override
    public List<String> getSubdirectoryRejet() {
        return subdirectoryRejet;
    }

    @Override
    public List<String> getExtensionFileRejetSup() {
        return null;
    }

    @Override
    public List<String> getNomSubdirectoryRejet() {
        return null;
    }

    @Override
    public String getExtFileRejet() {
        return null;
    }

    @Override
    public String getRepertoire(String rejet) {
        return null;
    }

    @Override
    public String getRepertoire50Phototheque() {
        return null;
    }

    @Override
    public List<String> getExtensionsUseFile() {
        return null;
    }

    @Override
    public String getTag_action_go_rapprochement() {
        return null;
    }

    @Override
    public String getTag_action_go() {
        return null;
    }

    @Override
    public String gettag_rapprochement() {
        return null;
    }

    @Override
    public String getTag_org() {
        return null;
    }

    @Override
    public String getCollections() {
        return null;
    }


}
