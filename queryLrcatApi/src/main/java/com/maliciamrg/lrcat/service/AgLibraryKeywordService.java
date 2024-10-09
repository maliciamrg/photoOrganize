package com.maliciamrg.lrcat.service;

import com.maliciamrg.lrcat.model.AgLibraryKeyword;
import com.maliciamrg.lrcat.repository.AgLibraryKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgLibraryKeywordService {

    @Autowired
    private AgLibraryKeywordRepository repository;

    public List<AgLibraryKeyword> getAllKeywords() {
        return repository.findAll();
    }

    public Optional<AgLibraryKeyword> getKeywordById(Long id) {
        return repository.findById(id);
    }

    public Optional<List<AgLibraryKeyword>> getKeywordChildOfId(Long id) {
        return repository.findByParent(id);
    }
    public AgLibraryKeyword saveKeyword(AgLibraryKeyword keyword) {
        return repository.save(keyword);
    }

    public void deleteKeyword(Long id) {
        repository.deleteById(id);
    }

    public Optional<AgLibraryKeyword> getKeywordByLcName(String lcName) {
        return repository.findByLcName(lcName);
    }

    public Optional<AgLibraryKeyword> getKeywordChildOfIdHaveNameChild(Long idLocal, String nameChild) {
        return repository.findByParentAndName(idLocal,nameChild);
    }
}
