package com.maliciamrg.lrcat.controller;


import com.maliciamrg.lrcat.model.AgLibraryRootFolder;
import com.maliciamrg.lrcat.service.AgLibraryRootFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agLibraryRootFolder")
public class AgLibraryRootFolderController {


    @Autowired
    private AgLibraryRootFolderService agLibraryRootFolderService;

    @GetMapping
    public List<AgLibraryRootFolder> getAllAgLibraryRootFolders() {
        return agLibraryRootFolderService.getAllAgLibraryRootFolders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgLibraryRootFolder> getAgLibraryRootFolderById(@PathVariable Long id) {
        AgLibraryRootFolder user = agLibraryRootFolderService.getAgLibraryRootFolderById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AgLibraryRootFolder createAgLibraryRootFolder(@RequestBody AgLibraryRootFolder user) {
        return agLibraryRootFolderService.createAgLibraryRootFolder(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgLibraryRootFolder> updateAgLibraryRootFolder(@PathVariable Long id, @RequestBody AgLibraryRootFolder userDetails) {
        AgLibraryRootFolder updatedAgLibraryRootFolder = agLibraryRootFolderService.updateAgLibraryRootFolder(id, userDetails);
        if (updatedAgLibraryRootFolder != null) {
            return ResponseEntity.ok(updatedAgLibraryRootFolder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgLibraryRootFolder(@PathVariable Long id) {
        agLibraryRootFolderService.deleteAgLibraryRootFolder(id);
        return ResponseEntity.noContent().build();
    }
}

