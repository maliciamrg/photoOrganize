package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.IParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stub
public class ParamsStub implements IParams {
    @Override
    public Object getArrayRepertoirePhotoRepertoire(Integer rootFolderNum) {
        return null;
    }

    @Override
    public String getFolderdelim() {
        return "\\";
    }

    @Override
    public long getGmt01jan200112am() {
        return 978307200;
    }

    @Override
    public List<String> getAllowed_extensions() {
        List<String> fileList = new ArrayList<>();
        fileList.add("jpg");
        fileList.add("bmp");
        return fileList;
    }

    @Override
    public List<String> getExclude_subdirectory_reject() {
        List<String> fileList = new ArrayList<>();
        fileList.add("rejet");
        fileList.add("RepRej");
        return fileList;
    }

    @Override
    public List<String> getExtensionFileRejetSup() {
        List<String> fileList = new ArrayList<>();
        fileList.add("txt");
        fileList.add("tmp");
        fileList.add("pto");
        fileList.add("xmp");
        fileList.add("thm");
        fileList.add("lrv");
        fileList.add("pxd");
        fileList.add("lrbak");
        return fileList;
    }

    @Override
    public List<String> getNomSubdirectoryRejet() {
        List<String> fileList = new ArrayList<>();
        fileList.add("rejet");
        fileList.add("dark");
        fileList.add("offset");
        fileList.add("flat");
        fileList.add("brut");
        fileList.add("image");
        fileList.add("jpg");
        return fileList;
    }

    @Override
    public String getExtFileRejet() {
        return "rejet";
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
        return getTag_action_go() + gettag_rapprochement();
    }

    @Override
    public String getTag_action_go() {
        return "GO" + "->";
    }

    @Override
    public String gettag_rapprochement() {
        return getTag_org() + "RAPPROCHEMENT";
    }

    @Override
    public String getTag_org() {
        return "#ORG#";
    }

    @Override
    public String getCollections() {
        return "!!Collections";
    }

    @Override
    public List<Map<String,String>> getArrayRepertoirePhotoRepertoire() {
        ArrayList<Map<String,String>> ret = new ArrayList<Map<String,String>>();
        HashMap<String,String> retIt = new HashMap<>();
        retIt.put("id","1");
        retIt.put("repertoire","01-Cataloque_Photo\\##Shooting 03-05 j");

       // ret.add("01-Cataloque_Photo\\##Events 10-15 j");
       // ret.add("01-Cataloque_Photo\\##Holidays 20-30 sem");
       // ret.add("01-Cataloque_Photo\\##Shooting 03-05 j");
       // ret.add("99-Rejet");
       // ret.add("01-Cataloque_Photo\\!!Collections\\##Sauvegarde 999 j");
        ret.add(retIt);
        return ret;
    }

}
