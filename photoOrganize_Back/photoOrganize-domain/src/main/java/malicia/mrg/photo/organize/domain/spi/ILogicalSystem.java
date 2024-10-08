package malicia.mrg.photo.organize.domain.spi;

import java.util.*;

public interface ILogicalSystem {

    List<String> getAllRootPathsLogiques();

    Collection<String> getAllFilesLogiques(String rootPath);

    int sqlmovefile(long scrFileIdLocal, String newPath);

    String sqlGetSidecarExtensions(long scrFileIdLocal);

    Map<String, Long> getIdlocalforFilePath(String source);

    String getFileByHash(java.lang.String s);

    void makeRepertory(String directoryName);

    void AdobeImagesWithoutLibraryFile();

    void folderWithoutRoot();

    void fileWithoutFolder();

    void KeywordImageWithoutImages();

    void keywordImageWithoutKeyword();

    Map<String, Map<String, String>> getFileForGoTag(String tagActionGoRapprochement);

    Map<String, String> getNewPathForGoTagandFileIdlocal(String tagOrg, String scrFileIdLocal);

    void removeKeywordImages(String kiIdLocal);

    Map<String, Map<String, String>> sqllistAllFileWithTagtoRep(String key, String s);

    Map<String, String> getFolderCollection(String collections, String tagOrg, String s);

    Date getFolderFirstDate(String folderName);

    boolean isValueInKeyword(String value, String keyword);
}
