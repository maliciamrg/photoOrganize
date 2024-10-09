package com.maliciamrg.lrcat.controller;


import com.maliciamrg.lrcat.model.AgLibraryFile;
import com.maliciamrg.lrcat.service.AgLibraryFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agLibraryFile")
public class AgLibraryFileController {


    private final AgLibraryFileService agLibraryFileService;

    public AgLibraryFileController(AgLibraryFileService agLibraryFileService) {
        this.agLibraryFileService = agLibraryFileService;
    }

    @GetMapping
    public List<AgLibraryFile> getAllAgLibraryFiles() {
        return agLibraryFileService.getAllAgLibraryFiles();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AgLibraryFile>> getAgLibraryFileByRootFolder(@RequestParam Integer rootFolder) {

        List<AgLibraryFile> agLibraryFileArray = agLibraryFileService.getAgLibraryFileByFolder(rootFolder);
        if (agLibraryFileArray != null) {
            return ResponseEntity.ok(agLibraryFileArray);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgLibraryFile> getAgLibraryFileById(@PathVariable Long id) {
        AgLibraryFile agLibraryFile = agLibraryFileService.getAgLibraryFileById(id);
        if (agLibraryFile != null) {
            return ResponseEntity.ok(agLibraryFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AgLibraryFile createAgLibraryFile(@RequestBody AgLibraryFile agLibraryFile) {
        return agLibraryFileService.createAgLibraryFile(agLibraryFile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgLibraryFile> updateAgLibraryFile(@PathVariable Long id, @RequestBody AgLibraryFile agLibraryFileDetails) {
        AgLibraryFile updatedAgLibraryFile = agLibraryFileService.updateAgLibraryFile(id, agLibraryFileDetails);
        if (updatedAgLibraryFile != null) {
            return ResponseEntity.ok(updatedAgLibraryFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgLibraryFile(@PathVariable Long id) {
        agLibraryFileService.deleteAgLibraryFile(id);
        return ResponseEntity.noContent().build();
    }
}

