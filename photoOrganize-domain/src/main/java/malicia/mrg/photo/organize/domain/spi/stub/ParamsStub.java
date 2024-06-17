package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;

import java.util.ArrayList;
import java.util.Collection;
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
}
