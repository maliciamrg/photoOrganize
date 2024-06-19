package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.controller.config.DomainConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PocController.class)
@Import(DomainConfiguration.class)
class PocControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFilesNotLogicOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                post("/poc/getFilesNotLogic"));
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"message\" : \"bob\" }"));

        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(
                    content().json(
                        (
                            new ArrayList<String>(
                                Arrays.asList(
                                    "afoo01",
                                    "file3"
                                )
                            ).toString()
                        )
                    )
                )
                .andReturn()
        ;
    }

}
