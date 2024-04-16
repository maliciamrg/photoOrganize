package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.spi.IWritePersistence;
import malicia.mrg.photo.organize.domain.spi.stub.WritePersistenceStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IHexMeTest {

    @Test
    void hex_pass_ok() {
//        Given
        String msg = "hex?";
        IWritePersistence writePersistence = new WritePersistenceStub();
        HexMeImpl hexMe = new HexMeImpl(writePersistence);

//        When
        String ret = hexMe.getMsgReturn(msg);

//        Then
        assertThat(ret).isEqualTo("Yes_Hex");
    }

    @Test
    void hex_pass_not_ok() {
//        Given
        String msg = "bob";
        IWritePersistence writePersistence = new WritePersistenceStub();
        HexMeImpl hexMe = new HexMeImpl(writePersistence);

//        When
        String ret = hexMe.getMsgReturn(msg);

//        Then
        assertThat(hexMe.getMsgReturn(msg)).isEqualTo("Not_Yet_Hex");
    }

    @Test
    void hex_write_persistence() {
//        Given
        String msg = "write?";
        IWritePersistence writePersistence = new WritePersistenceStub();
        HexMeImpl hexMe = new HexMeImpl(writePersistence);

//        When
        String ret = hexMe.getMsgReturn(msg);

//        Then
        assertThat(hexMe.getMsgReturn(msg)).isEqualTo("Hex_write57ceec5b-8f56-4175-8acc-fe161adb3536" );
    }

}