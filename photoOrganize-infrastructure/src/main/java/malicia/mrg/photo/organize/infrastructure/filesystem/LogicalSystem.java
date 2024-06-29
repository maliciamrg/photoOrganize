package malicia.mrg.photo.organize.infrastructure.filesystem;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.infrastructure.Params;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class LogicalSystem implements ILogicalSystem {


    private final String lightroomdbpath;

    public Params getParameter() {
        return parameter;
    }

    private final Params parameter;

    public LogicalSystem(Params parameter) {
        this.lightroomdbpath = parameter.getLightroomdbpath();
        this.parameter = parameter;
    }

    @Override
    public List<String> getAllRootPathsLogiques() {
        return null;
    }

    @Override
    public Collection<String> getAllFilesLogiques(String rootPath) {
        return null;
    }

    @Override
    public int sqlmovefile(long scrFileIdLocal, String newPath) {
        return 0;
    }

    @Override
    public String sqlGetSidecarExtensions(long scrFileIdLocal) {
        return null;
    }

    @Override
    public Map<String, Long> getIdlocalforFilePath(String source) {
        return null;
    }

    @Override
    public String getFileByHash(String s) {
        return null;
    }

    @Override
    public void makeRepertory(String directoryName) {

    }

    @Override
    public void AdobeImagesWithoutLibraryFile() {

    }

    @Override
    public void folderWithoutRoot() {

    }

    @Override
    public void fileWithoutFolder() {

    }

    @Override
    public void KeywordImageWithoutImages() {

    }

    @Override
    public void keywordImageWithoutKeyword() {

    }

    @Override
    public Map<String, Map<String, String>> getFileForGoTag(String tagActionGoRapprochement) {
        return null;
    }

    @Override
    public Map<String, String> getNewPathForGoTagandFileIdlocal(String tagOrg, String scrFileIdLocal) {
        return null;
    }

    @Override
    public void removeKeywordImages(String kiIdLocal) {

    }

    @Override
    public Map<String, Map<String, String>> sqllistAllFileWithTagtoRep(String key, String s) {
        return null;
    }

    @Override
    public Map<String, String> getFolderCollection(String collections, String tagOrg, String s) {
        return null;
    }
}
