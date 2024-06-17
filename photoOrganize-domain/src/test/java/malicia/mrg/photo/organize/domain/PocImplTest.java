package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import malicia.mrg.photo.organize.domain.spi.stub.LogicalSystemStub;
import malicia.mrg.photo.organize.domain.spi.stub.ParamsStub;
import malicia.mrg.photo.organize.domain.spi.stub.PhysicalSystemStub;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class PocImplTest {
    @Test
    void getAllRootPathsLogiques() {
//        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        PocImpl pocMe = new PocImpl(logicalSystem,physicalSystem,params);

//        When
        List<String> ret = pocMe.getPhysicalFilesNotLogic();

//        Then
        assertThat(ret.toString()).isEqualTo("[afoo01, file3]");
    }

}