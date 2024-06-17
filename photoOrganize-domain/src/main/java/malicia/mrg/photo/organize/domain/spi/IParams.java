package malicia.mrg.photo.organize.domain.spi;

import java.util.List;

public interface IParams {
    List<String> getAllowedExtensions();

    List<String> getSubdirectoryRejet();
}
