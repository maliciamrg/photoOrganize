package com.maliciamrg.lrcat.repository;

import com.maliciamrg.lrcat.model.AgLibraryKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgLibraryKeywordRepository extends JpaRepository<AgLibraryKeyword, Long> {
    Optional<List<AgLibraryKeyword>> findByParent(Long id);
    Optional<AgLibraryKeyword> findByLcName(String lcName);
    Optional<AgLibraryKeyword> findByParentAndName(Long id,String Name);
}
