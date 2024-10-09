package com.maliciamrg.lrcat.controller;

import com.maliciamrg.lrcat.model.AgLibraryKeyword;
import com.maliciamrg.lrcat.service.AgLibraryKeywordService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgLibraryKeywordControllerTest {

    @Mock
    private AgLibraryKeywordService agLibraryKeywordService;

    @InjectMocks
    private AgLibraryKeywordController agLibraryKeywordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllKeywords() {
        // Arrange
        AgLibraryKeyword keyword1 = new AgLibraryKeyword();
        AgLibraryKeyword keyword2 = new AgLibraryKeyword();
        List<AgLibraryKeyword> keywordList = Arrays.asList(keyword1, keyword2);

        when(agLibraryKeywordService.getAllKeywords()).thenReturn(keywordList);

        // Act
        List<AgLibraryKeyword> result = agLibraryKeywordController.getAllKeywords();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(agLibraryKeywordService, times(1)).getAllKeywords();
    }

    @Test
    void testGetKeywordById_Found() {
        // Arrange
        AgLibraryKeyword keyword = new AgLibraryKeyword();
        when(agLibraryKeywordService.getKeywordById(1L)).thenReturn(Optional.of(keyword));

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.getKeywordById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(agLibraryKeywordService, times(1)).getKeywordById(1L);
    }

    @Test
    void testGetKeywordById_NotFound() {
        // Arrange
        when(agLibraryKeywordService.getKeywordById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.getKeywordById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).getKeywordById(1L);
    }

    @Test
    void testGetKeywordByLcName_Found() {
        // Arrange
        AgLibraryKeyword keyword = new AgLibraryKeyword();
        when(agLibraryKeywordService.getKeywordByLcName("example")).thenReturn(Optional.of(keyword));

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.getKeywordByLcName("example");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(agLibraryKeywordService, times(1)).getKeywordByLcName("example");
    }

    @Test
    void testGetKeywordByLcName_NotFound() {
        // Arrange
        when(agLibraryKeywordService.getKeywordByLcName("example")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.getKeywordByLcName("example");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).getKeywordByLcName("example");
    }

    @Test
    void testCreateKeyword() {
        // Arrange
        AgLibraryKeyword keyword = new AgLibraryKeyword();
        when(agLibraryKeywordService.saveKeyword(keyword)).thenReturn(keyword);

        // Act
        AgLibraryKeyword result = agLibraryKeywordController.createKeyword(keyword);

        // Assert
        assertNotNull(result);
        assertEquals(keyword, result);
        verify(agLibraryKeywordService, times(1)).saveKeyword(keyword);
    }

    @Test
    void testUpdateKeyword_Found() {
        // Arrange
        AgLibraryKeyword existingKeyword = new AgLibraryKeyword();
        AgLibraryKeyword keywordDetails = new AgLibraryKeyword();
        when(agLibraryKeywordService.getKeywordById(1L)).thenReturn(Optional.of(existingKeyword));
        when(agLibraryKeywordService.saveKeyword(existingKeyword)).thenReturn(existingKeyword);

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.updateKeyword(1L, keywordDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).getKeywordById(1L);
        verify(agLibraryKeywordService, times(1)).saveKeyword(existingKeyword);
    }

    @Test
    void testUpdateKeyword_NotFound() {
        // Arrange
        AgLibraryKeyword keywordDetails = new AgLibraryKeyword();
        when(agLibraryKeywordService.getKeywordById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<AgLibraryKeyword> response = agLibraryKeywordController.updateKeyword(1L, keywordDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).getKeywordById(1L);
    }

    @Test
    void testDeleteKeyword() {
        // Act
        ResponseEntity<Void> response = agLibraryKeywordController.deleteKeyword(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).deleteKeyword(1L);
    }

    @Test
    void testGetKeywordChildOfId_Found() {
        // Arrange
        AgLibraryKeyword child1 = new AgLibraryKeyword();
        AgLibraryKeyword child2 = new AgLibraryKeyword();
        List<AgLibraryKeyword> children = Arrays.asList(child1, child2);

        when(agLibraryKeywordService.getKeywordChildOfId(1L)).thenReturn(Optional.of(children));

        // Act
        ResponseEntity<List<AgLibraryKeyword>> response = agLibraryKeywordController.getKeywordChildOfId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(agLibraryKeywordService, times(1)).getKeywordChildOfId(1L);
    }

    @Test
    void testGetKeywordChildOfId_NotFound() {
        // Arrange
        when(agLibraryKeywordService.getKeywordChildOfId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<AgLibraryKeyword>> response = agLibraryKeywordController.getKeywordChildOfId(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(agLibraryKeywordService, times(1)).getKeywordChildOfId(1L);
    }
}
