package com.maliciamrg.lrcat.controller;

import com.maliciamrg.lrcat.model.AgLibraryFolder;
import com.maliciamrg.lrcat.service.AgLibraryFolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AgLibraryFolderControllerTest {

    @Mock
    private AgLibraryFolderService agLibraryFolderService;

    @InjectMocks
    private AgLibraryFolderController agLibraryFolderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAgLibraryFolders() {
        // Arrange
        AgLibraryFolder folder1 = new AgLibraryFolder();
        AgLibraryFolder folder2 = new AgLibraryFolder();
        List<AgLibraryFolder> folders = Arrays.asList(folder1, folder2);

        when(agLibraryFolderService.getAllAgLibraryFolders()).thenReturn(folders);

        // Act
        List<AgLibraryFolder> result = agLibraryFolderController.getAllAgLibraryFolders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(agLibraryFolderService, times(1)).getAllAgLibraryFolders();
    }

    @Test
    void testGetAgLibraryFolderById_Found() {
        // Arrange
        AgLibraryFolder folder = new AgLibraryFolder();
        when(agLibraryFolderService.getAgLibraryFolderById(1L)).thenReturn(folder);

        // Act
        ResponseEntity<AgLibraryFolder> response = agLibraryFolderController.getAgLibraryFolderById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agLibraryFolderService, times(1)).getAgLibraryFolderById(1L);
    }

    @Test
    void testGetAgLibraryFolderById_NotFound() {
        // Arrange
        when(agLibraryFolderService.getAgLibraryFolderById(1L)).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryFolder> response = agLibraryFolderController.getAgLibraryFolderById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryFolderService, times(1)).getAgLibraryFolderById(1L);
    }

    @Test
    void testCreateAgLibraryFolder() {
        // Arrange
        AgLibraryFolder folder = new AgLibraryFolder();
        when(agLibraryFolderService.createAgLibraryFolder(folder)).thenReturn(folder);

        // Act
        AgLibraryFolder result = agLibraryFolderController.createAgLibraryFolder(folder);

        // Assert
        assertNotNull(result);
        assertEquals(folder, result);
        verify(agLibraryFolderService, times(1)).createAgLibraryFolder(folder);
    }

    @Test
    void testUpdateAgLibraryFolder_Found() {
        // Arrange
        AgLibraryFolder updatedFolder = new AgLibraryFolder();
        when(agLibraryFolderService.updateAgLibraryFolder(eq(1L), any(AgLibraryFolder.class))).thenReturn(updatedFolder);

        // Act
        ResponseEntity<AgLibraryFolder> response = agLibraryFolderController.updateAgLibraryFolder(1L, updatedFolder);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agLibraryFolderService, times(1)).updateAgLibraryFolder(eq(1L), any(AgLibraryFolder.class));
    }

    @Test
    void testUpdateAgLibraryFolder_NotFound() {
        // Arrange
        when(agLibraryFolderService.updateAgLibraryFolder(eq(1L), any(AgLibraryFolder.class))).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryFolder> response = agLibraryFolderController.updateAgLibraryFolder(1L, new AgLibraryFolder());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryFolderService, times(1)).updateAgLibraryFolder(eq(1L), any(AgLibraryFolder.class));
    }

    @Test
    void testDeleteAgLibraryFolder() {
        // Act
        ResponseEntity<Void> response = agLibraryFolderController.deleteAgLibraryFolder(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agLibraryFolderService, times(1)).deleteAgLibraryFolder(1L);
    }
}
