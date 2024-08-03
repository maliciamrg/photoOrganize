package malicia.mrg.photo.organize.infrastructure;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

//@TestPropertySource(
//        properties = {
//                "application.version=versionIT"
//        }
//)
//@TestPropertySource("classpath:application.yml")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = YamlFileApplicationContextInitializer.class)
@EnableConfigurationProperties(value = Params.class)
class ParamsTest {
    @Autowired
    private Params params;

    @Test
    void getVersion() {
        assertEquals("1.0", params.getVersion());
    }
    @Test
    void getRepertoire_photo_array() {
        assertEquals(6, params.getRepertoire_photo_array().size());
    }
    @Test
    void getArrayRepertoirePhotoRepertoire() {
        assertEquals(6, params.getArrayRepertoirePhotoRepertoire().size());
    }
}