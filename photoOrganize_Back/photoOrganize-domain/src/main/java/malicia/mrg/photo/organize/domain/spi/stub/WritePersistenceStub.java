package malicia.mrg.photo.organize.domain.spi.stub;

import malicia.mrg.photo.organize.domain.ddd.Stub;
import malicia.mrg.photo.organize.domain.spi.IWritePersistence;

import java.util.UUID;

@Stub
public class WritePersistenceStub implements IWritePersistence {
    @Override
    public UUID write(String msg) {
        return UUID.fromString("57ceec5b-8f56-4175-8acc-fe161adb3536");
//        return UUID.randomUUID();
    }
}
