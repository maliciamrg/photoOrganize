package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.IParams;

import java.util.ArrayList;
import java.util.List;

@Stub
public class ParamsStub implements IParams {


    @Override
    public List<String> getAllowedExtensions() {
        List<String> fileList = new ArrayList<>();
        fileList.add("jpg");
        fileList.add("bmp");
        return fileList;
    }

    @Override
    public List<String> getSubdirectoryRejet() {
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

}
