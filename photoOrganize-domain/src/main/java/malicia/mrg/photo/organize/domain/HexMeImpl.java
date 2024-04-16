package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.IHexMe;
import malicia.mrg.photo.organize.domain.ddd.DomainService;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;

import java.util.UUID;

@DomainService
public class HexMeImpl implements IHexMe {
    private CharSequence defautValue = "hex?";
    private String ok = "Yes_Hex";
    private String notOk = "Not_Yet_Hex";
    private String write = "write?";
    private String hexWrite = "Hex_write";
    private IWritePersistence writePersistence;

    public HexMeImpl(IWritePersistence writePersistence) {
        this.writePersistence = writePersistence;
    }

    public String getMsgReturn(String msg) {
        if (msg.contains(write)) {
            UUID uuid_persistence = writePersistence.write(msg);
            return hexWrite + uuid_persistence.toString();
        }
        if (msg.contains(defautValue)) {
            return ok;
        }
        return notOk;
    }

}
