package malicia.mrg.photo.organize.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HexMeTest {
    @Test
    void hex_pass_ok() {
//        Given
        String msg = "hex?";

//        When
        HexMeImpl hexMe = new HexMeImpl();

//        Then
        assertThat(hexMe.getMsgReturn(msg)).isEqualTo("Yes_Hex");
    }

    @Test
    void hex_pass_not_ok() {
//        Given
        String msg = "bob";

//        When
        HexMeImpl hexMe = new HexMeImpl();

//        Then
        assertThat(hexMe.getMsgReturn(msg)).isEqualTo("Not_Yet_Hex");
    }
//    @Test
//    void hex_write_persistence() {
////        Given
//        String msg = "write?";
//
////        When
//        HexMeImpl hexMe = new HexMeImpl();
//
////        Then
//        assertThat(hexMe.getMsgReturn(msg)).isEqualTo("Hex_write and UUID persistence");
//    }

}