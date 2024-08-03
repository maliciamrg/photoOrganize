package malicia.mrg.photo.organize.infrastructure;

import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@ConfigurationProperties(prefix = "application")
public class Params implements IParams {

    private String lightroomdbpath;
    private String version;
    private List<String> exclude_subdirectory_reject;
    private List<String> allowed_extensions;
    private String repertoire_photo_root;
    private List<ElementRootFolder> repertoire_photo_array;

    public String getRepertoire_photo_root() {
        return repertoire_photo_root;
    }

    public void setRepertoire_photo_root(String repertoire_photo_root) {
        this.repertoire_photo_root = repertoire_photo_root;
    }

    public String getLightroomdbpath() {
        return lightroomdbpath;
    }

    public void setLightroomdbpath(String lightroomdbpath) {
        this.lightroomdbpath = lightroomdbpath;
    }

    @Override
    public String getFolderdelim() {
        return "\\";
    }

    @Override
    public long getGmt01jan200112am() {
        return 0;
    }

    @Override
    public List<String> getAllowed_extensions() {
        return allowed_extensions;
    }

    public void setAllowed_extensions(List<String> allowed_extensions) {
        this.allowed_extensions = allowed_extensions;
    }

    @Override
    public List<String> getExclude_subdirectory_reject() {
        return exclude_subdirectory_reject;
    }

    public void setExclude_subdirectory_reject(List<String> exclude_subdirectory_reject) {
        this.exclude_subdirectory_reject = exclude_subdirectory_reject;
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

    @Override
    public List<Map<String, String>> getArrayRepertoirePhotoRepertoire() {
        ArrayList<Map<String, String>> ret = new ArrayList();
        List<ElementRootFolder> listRepertoire = getRepertoire_photo_array();
        for (int i = 0; i < listRepertoire.size(); ++i) {
            HashMap<String, String> retIt = new HashMap<>();
            retIt.put("id", "" + i);
            retIt.put("repertoire", listRepertoire.get(i).getRepertoire());
            ret.add(retIt);
        }
        return ret;
    }

    @Override
    public ElementRootFolder getArrayRepertoirePhotoRepertoire(Integer rootFolderNum) {
        return getRepertoire_photo_array().get(rootFolderNum);
    }

    @Override
    public String getRootFolder() {
        return repertoire_photo_root;
    }


    public List<ElementRootFolder> getRepertoire_photo_array() {
        return repertoire_photo_array;
    }

    public void setRepertoire_photo_array(List<ElementRootFolder> repertoire_photo_array) {
        this.repertoire_photo_array = repertoire_photo_array;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
