package com.maliciamrg.lrcat.controller;

import com.maliciamrg.lrcat.model.AgLibraryRootFolder;
import com.maliciamrg.lrcat.service.AgLibraryRootFolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgLibraryRootFolderControllerTest {

    @Mock
    private AgLibraryRootFolderService agLibraryRootFolderService;

    @InjectMocks
    private AgLibraryRootFolderController agLibraryRootFolderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAgLibraryRootFolders() {
        // Arrange
        AgLibraryRootFolder rootFolder1 = new AgLibraryRootFolder();
        AgLibraryRootFolder rootFolder2 = new AgLibraryRootFolder();
        List<AgLibraryRootFolder> rootFolderList = Arrays.asList(rootFolder1, rootFolder2);

        when(agLibraryRootFolderService.getAllAgLibraryRootFolders()).thenReturn(rootFolderList);

        // Act
        List<AgLibraryRootFolder> result = agLibraryRootFolderController.getAllAgLibraryRootFolders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(agLibraryRootFolderService, times(1)).getAllAgLibraryRootFolders();
    }

    @Test
    void testGetAgLibraryRootFolderById_Found() {
        // Arrange
        AgLibraryRootFolder rootFolder = new AgLibraryRootFolder();
        when(agLibraryRootFolderService.getAgLibraryRootFolderById(1L)).thenReturn(rootFolder);

        // Act
        ResponseEntity<AgLibraryRootFolder> response = agLibraryRootFolderController.getAgLibraryRootFolderById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(agLibraryRootFolderService, times(1)).getAgLibraryRootFolderById(1L);
    }

    @Test
    void testGetAgLibraryRootFolderById_NotFound() {
        // Arrange
        when(agLibraryRootFolderService.getAgLibraryRootFolderById(1L)).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryRootFolder> response = agLibraryRootFolderController.getAgLibraryRootFolderById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryRootFolderService, times(1)).getAgLibraryRootFolderById(1L);
    }

    @Test
    void testCreateAgLibraryRootFolder() {
        // Arrange
        AgLibraryRootFolder rootFolder = new AgLibraryRootFolder();
        when(agLibraryRootFolderService.createAgLibraryRootFolder(rootFolder)).thenReturn(rootFolder);

        // Act
        AgLibraryRootFolder result = agLibraryRootFolderController.createAgLibraryRootFolder(rootFolder);

        // Assert
        assertNotNull(result);
        assertEquals(rootFolder, result);
        verify(agLibraryRootFolderService, times(1)).createAgLibraryRootFolder(rootFolder);
    }

    @Test
    void testUpdateAgLibraryRootFolder_Found() {
        // Arrange
        AgLibraryRootFolder rootFolderDetails = new AgLibraryRootFolder();
        AgLibraryRootFolder updatedRootFolder = new AgLibraryRootFolder();
        when(agLibraryRootFolderService.updateAgLibraryRootFolder(1L, rootFolderDetails)).thenReturn(updatedRootFolder);

        // Act
        ResponseEntity<AgLibraryRootFolder> response = agLibraryRootFolderController.updateAgLibraryRootFolder(1L, rootFolderDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRootFolder, response.getBody());
        verify(agLibraryRootFolderService, times(1)).updateAgLibraryRootFolder(1L, rootFolderDetails);
    }

    @Test
    void testUpdateAgLibraryRootFolder_NotFound() {
        // Arrange
        AgLibraryRootFolder rootFolderDetails = new AgLibraryRootFolder();
        when(agLibraryRootFolderService.updateAgLibraryRootFolder(1L, rootFolderDetails)).thenReturn(null);

        // Act
        ResponseEntity<AgLibraryRootFolder> response = agLibraryRootFolderController.updateAgLibraryRootFolder(1L, rootFolderDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryRootFolderService, times(1)).updateAgLibraryRootFolder(1L, rootFolderDetails);
    }

    @Test
    void testDeleteAgLibraryRootFolder() {
        // Act
        ResponseEntity<Void> response = agLibraryRootFolderController.deleteAgLibraryRootFolder(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agLibraryRootFolderService, times(1)).deleteAgLibraryRootFolder(1L);
    }
}
