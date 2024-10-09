package com.maliciamrg.lrcat.service;

import com.maliciamrg.lrcat.model.AgLibraryRootFolder;
import com.maliciamrg.lrcat.repository.AgLibraryRootFolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgLibraryRootFolderService {

    @Autowired
    private AgLibraryRootFolderRepository agLibraryRootFolderRepository;

    public List<AgLibraryRootFolder> getAllAgLibraryRootFolders() {
        return agLibraryRootFolderRepository.findAll();
    }

    public AgLibraryRootFolder getAgLibraryRootFolderById(Long id) {
        return agLibraryRootFolderRepository.findById(id).orElse(null);
    }

    public AgLibraryRootFolder createAgLibraryRootFolder(AgLibraryRootFolder user) {
        return agLibraryRootFolderRepository.save(user);
    }

    public AgLibraryRootFolder updateAgLibraryRootFolder(Long id, AgLibraryRootFolder agLibraryRootFolderDetails) {
        AgLibraryRootFolder agLibraryRootFolder = agLibraryRootFolderRepository.findById(id).orElse(null);
        if (agLibraryRootFolder != null) {
            agLibraryRootFolder.setName(agLibraryRootFolderDetails.getName());
            return agLibraryRootFolderRepository.save(agLibraryRootFolder);
        }
        return null;
    }

    public void deleteAgLibraryRootFolder(Long id) {
        agLibraryRootFolderRepository.deleteById(id);
    }
}
