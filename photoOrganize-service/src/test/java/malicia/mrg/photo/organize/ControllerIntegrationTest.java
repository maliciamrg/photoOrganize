package malicia.mrg.photo.organize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
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
    void greeting() throws Exception {

        var response = restTemplate.getForEntity("http://localhost:%d/".formatted(port), String.class);

        System.out.println(response.toString());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo("Greetings from Spring Boot!");

    }

    @Test
    void Not_Yet_Hex() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var request = new HttpEntity<>("{ \"message\" : \"bob\" }", headers);
        var response = restTemplate.postForEntity("http://localhost:%d/hexMe".formatted(port), request, String.class);

        System.out.println(response.toString());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo("Not_Yet_Hex");

    }

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

}
