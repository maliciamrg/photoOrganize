package malicia.mrg.photo.organize.domain.spi;

import java.util.UUID;

public interface IWritePersistence {
    UUID write(String msg);
}
