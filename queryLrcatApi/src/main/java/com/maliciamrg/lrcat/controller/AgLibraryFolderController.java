package com.maliciamrg.lrcat.controller;


import com.maliciamrg.lrcat.model.AgLibraryFolder;
import com.maliciamrg.lrcat.service.AgLibraryFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/AgLibraryFolder")
public class AgLibraryFolderController {


    private AgLibraryFolderService agLibraryFolderService;

    public AgLibraryFolderController(AgLibraryFolderService agLibraryFolderService) {
        this.agLibraryFolderService = agLibraryFolderService;
    }

    @GetMapping
    public List<AgLibraryFolder> getAllAgLibraryFolders() {
        return agLibraryFolderService.getAllAgLibraryFolders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgLibraryFolder> getAgLibraryFolderById(@PathVariable Long id) {
        AgLibraryFolder user = agLibraryFolderService.getAgLibraryFolderById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AgLibraryFolder createAgLibraryFolder(@RequestBody AgLibraryFolder agLibraryFolder) {
        return agLibraryFolderService.createAgLibraryFolder(agLibraryFolder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgLibraryFolder> updateAgLibraryFolder(@PathVariable Long id, @RequestBody AgLibraryFolder agLibraryFolderDetails) {
        AgLibraryFolder updatedAgLibraryFolder = agLibraryFolderService.updateAgLibraryFolder(id, agLibraryFolderDetails);
        if (updatedAgLibraryFolder != null) {
            return ResponseEntity.ok(updatedAgLibraryFolder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgLibraryFolder(@PathVariable Long id) {
        agLibraryFolderService.deleteAgLibraryFolder(id);
        return ResponseEntity.noContent().build();
    }
}

