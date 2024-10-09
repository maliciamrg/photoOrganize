package com.maliciamrg.lrcat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "git.commit.message.short=Initial commit",
        "git.branch=main",
        "git.commit.id=123456abc"
})
class CommitInfoControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CommitInfoController commitInfoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commitInfoController).build();
    }
    @Disabled
    @Test
    void testGetCommitId() throws Exception {
        // Perform the GET request to /commitId and verify the response
        mockMvc.perform(get("/commitId"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"Commit message\":\"Initial commit\"")))
                .andExpect(content().string(containsString("\"Commit branch\":\"main\"")))
                .andExpect(content().string(containsString("\"Commit id\":\"123456abc\"")));
    }
}
