package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.application.controller.config.DomainConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoRepertoryController.class)
@Import(DomainConfiguration.class)
class PhotoRepertoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPrcRepertories() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/photo/repertories"));
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"message\" : \"bob\" }"));

        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(
                        content().json(
                                (
                                        "[\"01-Cataloque_Photo\\\\##Shooting 03-05 j\",\"01-Cataloque_Photo\\\\##Events 10-15 j\",\"01-Cataloque_Photo\\\\##Holidays 20-30 sem\",\"01-Cataloque_Photo\\\\##Shooting 03-05 j\",\"99-Rejet\",\"01-Cataloque_Photo\\\\!!Collections\\\\##Sauvegarde 999 j\"]"
                                )
                        )
                )
                .andReturn()
        ;
    }
}