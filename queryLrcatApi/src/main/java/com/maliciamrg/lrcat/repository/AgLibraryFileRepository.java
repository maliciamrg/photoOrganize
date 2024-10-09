package com.maliciamrg.lrcat.repository;

import com.maliciamrg.lrcat.model.AgLibraryFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgLibraryFileRepository extends JpaRepository<AgLibraryFile, Long> {

    List<AgLibraryFile> findByFolder(Integer rootFolder);
}
