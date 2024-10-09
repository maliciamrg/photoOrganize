package com.maliciamrg.lrcat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BaseControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BaseController baseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(baseController).build();
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Greetings from Spring Boot!"));
    }

    @Disabled
    @Test
    void testLrcat_files() throws Exception {
        // Mocking File class instance, not static methods
        File mockDirectory = mock(File.class);
        File[] mockFiles = {new File("file1.txt"), new File("file2.txt")};

        // Mocking listFiles() behavior of the File instance
        when(mockDirectory.listFiles()).thenReturn(mockFiles);

        // Simulate pathStr "/mockDirectory" (doesn't have to exist since it's mocked)
        String pathStr = "mockDirectory";
        try (MockedStatic<File> mockedFile = mockStatic(File.class)) {
            mockedFile.when(() -> new File("/" + pathStr)).thenReturn(mockDirectory);

            mockMvc.perform(get("/file/" + pathStr))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("[file1.txt, file2.txt]")));
        }
    }
}
