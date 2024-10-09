package com.maliciamrg.lrcat.controller;

import com.maliciamrg.lrcat.model.AgLibraryFile;
import com.maliciamrg.lrcat.service.AgLibraryFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AgLibraryFileControllerTest {

    @Mock
    private AgLibraryFileService agLibraryFileService;

    @InjectMocks
    private AgLibraryFileController agLibraryFileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAgLibraryFiles() {
        // Arrange
        AgLibraryFile file1 = new AgLibraryFile();
        AgLibraryFile file2 = new AgLibraryFile();
        List<AgLibraryFile> files = Arrays.asList(file1, file2);

        when(agLibraryFileService.getAllAgLibraryFiles()).thenReturn(files);

        // Act
        List<AgLibraryFile> result = agLibraryFileController.getAllAgLibraryFiles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(agLibraryFileService, times(1)).getAllAgLibraryFiles();
    }

    @Test
    void testGetAgLibraryFileByRootFolder_Found() {
        // Arrange
        AgLibraryFile file1 = new AgLibraryFile();
        List<AgLibraryFile> files = Arrays.asList(file1);

        when(agLibraryFileService.getAgLibraryFileByFolder(1)).thenReturn(files);

        // Act
        ResponseEntity<List<AgLibraryFile>> response = agLibraryFileController.getAgLibraryFileByRootFolder(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(agLibraryFileService, times(1)).getAgLibraryFileByFolder(1);
    }

    @Test
    void testGetAgLibraryFileByRootFolder_NotFound() {
        // Arrange
        when(agLibraryFileService.getAgLibraryFileByFolder(1)).thenReturn(null);

        // Act
        ResponseEntity<List<AgLibraryFile>> response = agLibraryFileController.getAgLibraryFileByRootFolder(1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryFileService, times(1)).getAgLibraryFileByFolder(1);
    }

    @Test
    void testGetAgLibraryFileById_Found() {
        // Arrange
        AgLibraryFile file = new AgLibraryFile();
        when(agLibraryFileService.getAgLibraryFileById(1L)).thenReturn(file);

        // Act
        ResponseEntity<AgLibraryFile> response = agLibraryFileController.getAgLibraryFileById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agLibraryFileService, times(1)).getAgLibraryFileById(1L);
    }

    @Test
    void testGetAgLibraryFileById_NotFound() {
        // Arrange
        when(agLibraryFileService.getAgLibraryFileById(1L)).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryFile> response = agLibraryFileController.getAgLibraryFileById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryFileService, times(1)).getAgLibraryFileById(1L);
    }

    @Test
    void testCreateAgLibraryFile() {
        // Arrange
        AgLibraryFile file = new AgLibraryFile();
        when(agLibraryFileService.createAgLibraryFile(file)).thenReturn(file);

        // Act
        AgLibraryFile result = agLibraryFileController.createAgLibraryFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(file, result);
        verify(agLibraryFileService, times(1)).createAgLibraryFile(file);
    }

    @Test
    void testUpdateAgLibraryFile_Found() {
        // Arrange
        AgLibraryFile updatedFile = new AgLibraryFile();
        when(agLibraryFileService.updateAgLibraryFile(eq(1L), any(AgLibraryFile.class))).thenReturn(updatedFile);

        // Act
        ResponseEntity<AgLibraryFile> response = agLibraryFileController.updateAgLibraryFile(1L, updatedFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agLibraryFileService, times(1)).updateAgLibraryFile(eq(1L), any(AgLibraryFile.class));
    }

    @Test
    void testUpdateAgLibraryFile_NotFound() {
        // Arrange
        when(agLibraryFileService.updateAgLibraryFile(eq(1L), any(AgLibraryFile.class))).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryFile> response = agLibraryFileController.updateAgLibraryFile(1L, new AgLibraryFile());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryFileService, times(1)).updateAgLibraryFile(eq(1L), any(AgLibraryFile.class));
    }

    @Test
    void testDeleteAgLibraryFile() {
        // Act
        ResponseEntity<Void> response = agLibraryFileController.deleteAgLibraryFile(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agLibraryFileService, times(1)).deleteAgLibraryFile(1L);
    }
}
