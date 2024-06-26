package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;

import java.util.*;

@Stub
public class LogicalSystemStub implements ILogicalSystem {

    @Override
    public List<String> getAllRootPathsLogiques() {
        List<String> fileList = new ArrayList<>();
        fileList.add("root1");
        fileList.add("root2");
        return fileList;
    }

    @Override
    public Collection<String> getAllFilesLogiques(String rootPath) {
        List<String> fileList = new ArrayList<>();
        if (rootPath=="root1") {
            fileList.add("file1");
            fileList.add("file2");
        }
        if (rootPath=="root2") {
            fileList.add("fileZZZ22");
            fileList.add("fileazertyuiop12");
        }
        return fileList;
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
        return "hashfile";
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
