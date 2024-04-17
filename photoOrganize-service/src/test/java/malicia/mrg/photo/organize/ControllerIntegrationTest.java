package malicia.mrg.photo.organize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ControllerIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void Hex_Ok() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var request = new HttpEntity<>("{ \"message\" : \"hex?\" }", headers);
        var response = restTemplate.postForEntity("http://localhost:%d/hexMe".formatted(port), request, String.class);

        System.out.println(response.toString());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo("Yes_Hex");

    }

    @Test
    void Hex_Msg() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var request = new HttpEntity<>("{ \"message\" : \"write?\" }", headers);
        var response = restTemplate.postForEntity("http://localhost:%d/hexMe".formatted(port), request, String.class);

        //        Then
        System.out.println(response.toString());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        UUID uuid = null;
        try{
            uuid = UUID.fromString(response.getBody().toString().substring(9));
        } catch (IllegalArgumentException exception){
        }
        assertNotNull(uuid);
        assertThat(response.getBody().toString().substring(0,9)).isEqualTo("Hex_write");

        assertThat(response.getBody().toString().replace("Hex_write","")).isNotEqualTo("57ceec5b-8f56-4175-8acc-fe161adb3536");
        assertThat(response.getBody().toString().replace("Hex_write","")).isEqualTo(uuid.toString() );

    }
}
