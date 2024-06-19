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
    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    @Override
    public List<String> getSubdirectoryRejet() {
        return subdirectoryRejet;
    }


}
