package malicia.mrg.photo.organize.infrastructure;

import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<ElementRootFolder> getArrayRepertoirePhotoNmUnique() {
        return getRepertoirePhotoArray();
    }

    @Override
    public ElementRootFolder getArrayRepertoirePhotoNmUnique(Integer rootFolderNum) {
        return getRepertoirePhotoArray().get(rootFolderNum);
    }

    @Override
    public String getRootFolder() {
        return repertoire_photo_root;
    }

    @Override
    public ElementRootFolder getArrayRepertoirePhotoNmUnique(String rootFolder) {
        ElementRootFolder eleRootFolderEmpty = new ElementRootFolder();
        for (ElementRootFolder eleRootFolder : getRepertoirePhotoArray()) {
            if (eleRootFolder.getNomunique().compareTo(rootFolder)==0){
                return eleRootFolder;
            }
        }
        return eleRootFolderEmpty;
    }


    public List<ElementRootFolder> getRepertoirePhotoArray() {
        return repertoire_photo_array;
    }

    public void setRepertoirePhotoArray(List<ElementRootFolder> repertoire_photo_array) {
        this.repertoire_photo_array = repertoire_photo_array;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
