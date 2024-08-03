package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.dto.Analysis;
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
    void testGetPhysicalFilesNotLogic() {
        //        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        PhotoControllerImpl pocMe = new PhotoControllerImpl(logicalSystem,physicalSystem,params);

//        When
        List<String> ret = pocMe.getPhysicalFilesNotLogic();

//        Then
        assertThat(ret.toString()).isEqualTo("[afoo01, file3]");
    }

    @Test
    void analyseFilePhysiqueAndLogic() {
    }

    @Test
    void synchroDatabase() {
        //        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        PhotoControllerImpl pocMe = new PhotoControllerImpl(logicalSystem,physicalSystem,params);

//        When
        Analysis ret = new Analysis();
        ret = pocMe.synchroDatabase();

//        Then
        assertThat(ret.getActions().size()).isEqualTo(5);
    }
}