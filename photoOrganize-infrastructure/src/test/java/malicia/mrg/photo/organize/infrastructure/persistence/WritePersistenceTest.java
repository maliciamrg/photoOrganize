package malicia.mrg.photo.organize.infrastructure.persistence;

import malicia.mrg.photo.organize.domain.spi.IWritePersistence;
import malicia.mrg.photo.organize.infrastructure.persistence.repository.MasterTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@AutoConfigureTestDatabase
class WritePersistenceTest {

    @Autowired
    MasterTableRepository masterTableRepository;

    @Test
    void write() {
//        Given
        String msg = "write?";
        IWritePersistence writePersistence = new WritePersistence(masterTableRepository);

//        When
        UUID ret = writePersistence.write(msg);

//        Then
        UUID uuid = null;
        try {
            uuid = UUID.fromString(ret.toString());
            //do something
        } catch (IllegalArgumentException exception) {
            //handle the case where string is not valid UUID
        }
        assertThat(ret.toString()).isEqualTo(uuid.toString());
    }

}