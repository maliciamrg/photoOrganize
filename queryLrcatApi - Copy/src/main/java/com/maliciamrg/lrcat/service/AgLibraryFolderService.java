package com.maliciamrg.lrcat.service;

import com.maliciamrg.lrcat.model.AgLibraryFolder;
import com.maliciamrg.lrcat.repository.AgLibraryFolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgLibraryFolderService {

    private final AgLibraryFolderRepository agLibraryFolderRepository;

    public AgLibraryFolderService(AgLibraryFolderRepository agLibraryFolderRepository) {
        this.agLibraryFolderRepository = agLibraryFolderRepository;
    }

    public List<AgLibraryFolder> getAllAgLibraryFolders() {
        return agLibraryFolderRepository.findAll();
    }

    public AgLibraryFolder getAgLibraryFolderById(Long id) {
        return agLibraryFolderRepository.findById(id).orElse(null);
    }

    public AgLibraryFolder createAgLibraryFolder(AgLibraryFolder agLibraryFolder) {
        return agLibraryFolderRepository.save(agLibraryFolder);
    }

    public AgLibraryFolder updateAgLibraryFolder(Long id, AgLibraryFolder agLibraryFolderDetails) {
        AgLibraryFolder agLibraryFolder = agLibraryFolderRepository.findById(id).orElse(null);
        if (agLibraryFolder != null) {
            agLibraryFolder.setPathFromRoot(agLibraryFolderDetails.getPathFromRoot());
            return agLibraryFolderRepository.save(agLibraryFolder);
        }
        return null;
    }

    public void deleteAgLibraryFolder(Long id) {
        agLibraryFolderRepository.deleteById(id);
    }
}
