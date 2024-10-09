package com.maliciamrg.lrcat.service;

import com.maliciamrg.lrcat.model.AgLibraryFile;
import com.maliciamrg.lrcat.repository.AgLibraryFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgLibraryFileService {

    private AgLibraryFileRepository agLibraryFileRepository;

    public AgLibraryFileService(AgLibraryFileRepository agLibraryFileRepository) {
        agLibraryFileRepository = agLibraryFileRepository;
    }

    public List<AgLibraryFile> getAllAgLibraryFiles() {
        return agLibraryFileRepository.findAll();
    }

    public AgLibraryFile getAgLibraryFileById(Long id) {
        return agLibraryFileRepository.findById(id).orElse(null);
    }

    public AgLibraryFile createAgLibraryFile(AgLibraryFile agLibraryFile) {
        return agLibraryFileRepository.save(agLibraryFile);
    }

    public AgLibraryFile updateAgLibraryFile(Long id, AgLibraryFile agLibraryFileDetails) {
        AgLibraryFile agLibraryFile = agLibraryFileRepository.findById(id).orElse(null);
        if (agLibraryFile != null) {
            agLibraryFile.setBaseName(agLibraryFileDetails.getBaseName());
            return agLibraryFileRepository.save(agLibraryFile);
        }
        return null;
    }

    public void deleteAgLibraryFile(Long id) {
        agLibraryFileRepository.deleteById(id);
    }

    public List<AgLibraryFile> getAgLibraryFileByFolder(Integer rootFolder) {
        return agLibraryFileRepository.findByFolder(rootFolder);
    }
}
