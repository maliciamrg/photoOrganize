package malicia.mrg.photo.organize.infrastructure.filesystem;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.infrastructure.Params;
import malicia.mrg.photo.organize.infrastructure.dto.AgLibraryRootFolderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.*;

@Service
public class LogicalSystem implements ILogicalSystem {

    private static final Logger logger = LoggerFactory.getLogger(LogicalSystem.class);
    private final String lightroomdbpath;
    private final Params parameter;

    public LogicalSystem(Params parameter) {
        this.lightroomdbpath = parameter.getLightroomdbpath();
        this.parameter = parameter;
    }

    public Params getParameter() {
        return parameter;
    }

    @Override
    public List<String> getAllRootPathsLogiques() {
        List<String> outReturn = new ArrayList<>();
        WebClient webClient = WebClient.create();
        AgLibraryRootFolderDto[] responseJson = webClient.get()
                .uri("http://localhost:8091/api/agLibraryRootFolder")
                .exchange()
                .block()
                .bodyToMono(AgLibraryRootFolderDto[].class)
                .block();
        logger.info(responseJson.toString());
        for (AgLibraryRootFolderDto agLibraryRootFolder : responseJson) {
            outReturn.add(agLibraryRootFolder.getAbsolutePath());
        }
        return outReturn;
    }

    @Override
    public Collection<String> getAllFilesLogiques(String rootPath) {
        List<String> outReturn = new ArrayList<>();
        WebClient webClient = WebClient.create();
        AgLibraryRootFolderDto[] responseJson = webClient.get()
                .uri("http://localhost:8091/api/agLibraryRootFolder")
                .exchange()
                .block()
                .bodyToMono(AgLibraryRootFolderDto[].class)
                .block();
        logger.info(responseJson.toString());
        for (AgLibraryRootFolderDto agLibraryRootFolder : responseJson) {
            outReturn.add(agLibraryRootFolder.getAbsolutePath());
        }
        return outReturn;
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

    @Override
    public Date getFolderFirstDate(String folderName) {
        return new Date();
    }

    @Override
    public boolean isValueInKeyword(String value, String keyword) {
        List<String> listTag = getValueForKeyword(keyword);
        return listTag.contains(value);
    }

    private List<String> getValueForKeyword(String keyword) {
        WebClient webClient = WebClient.create();
        String responseJson = webClient.get()
                .uri("http://localhost:8091/api/keywords/byName/"+keyword.toLowerCase()+"/child/nameList")
                .exchange()
                .block()
                .bodyToMono(String.class)
                .block();
        logger.info(responseJson);
        Type listType = new TypeToken<List<String>>(){}.getType();
        Gson gson = new Gson();
        List<String> list = gson.fromJson(responseJson, listType);
        return list;
    }
}
