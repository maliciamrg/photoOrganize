package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.config.DomainConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(HexMeController.class)
@Import(DomainConfiguration.class)
class HexMeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHexMeNotOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                post("/hexMe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"message\" : \"bob\" }"));

        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Not_Yet_Hex"))
                .andReturn()
        ;
    }

    @Test
    void testHexMeOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                post("/hexMe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"message\" : \"hex?\" }"));

        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Yes_Hex"))
                .andReturn()
        ;
    }
}
