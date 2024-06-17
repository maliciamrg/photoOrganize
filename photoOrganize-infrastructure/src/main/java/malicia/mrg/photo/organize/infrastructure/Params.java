package malicia.mrg.photo.organize.infrastructure;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class Params implements IParams {

    @Override
    public List<String> getAllowedExtensions() {
        return null;
    }

    @Override
    public List<String> getSubdirectoryRejet() {
        return null;
    }
}
