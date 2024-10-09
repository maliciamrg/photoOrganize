package com.maliciamrg.lrcat.controller;


import com.maliciamrg.lrcat.model.AgLibraryKeyword;
import com.maliciamrg.lrcat.service.AgLibraryKeywordService;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/keywords")
public class AgLibraryKeywordController {

    @Autowired
    private AgLibraryKeywordService service;

    @GetMapping
    public List<AgLibraryKeyword> getAllKeywords() {
        return service.getAllKeywords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgLibraryKeyword> getKeywordById(@PathVariable Long id) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordById(id);
        if (keyword.isPresent()) {
            return ResponseEntity.ok(keyword.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{id}/child")
    public ResponseEntity<List<AgLibraryKeyword>> getKeywordChildOfId(@PathVariable Long id) {
        Optional<List<AgLibraryKeyword>> keyword = service.getKeywordChildOfId(id);
        if (keyword.isPresent()) {
            return ResponseEntity.ok(keyword.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/byName/{lcName}")
    public ResponseEntity<AgLibraryKeyword> getKeywordByLcName(@PathVariable String lcName) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordByLcName(lcName);
        if (keyword.isPresent()) {
            return ResponseEntity.ok(keyword.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/byName/{lcName}/child")
    public ResponseEntity<List<AgLibraryKeyword>> getKeywordChildOfLcName(@PathVariable String lcName) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordByLcName(lcName);
        if (keyword.isPresent()) {
            Optional<List<AgLibraryKeyword>> keywords = service.getKeywordChildOfId(keyword.get().getIdLocal());
            if (keyword.isPresent()) {
                return ResponseEntity.ok(keywords.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/byName/{lcName}/child/nameList")
    public ResponseEntity<String> getKeywordChildOfLcNameNameList(@PathVariable String lcName) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordByLcName(lcName);
        if (keyword.isPresent()) {
            Optional<List<AgLibraryKeyword>> keywords = service.getKeywordChildOfId(keyword.get().getIdLocal());
            if (keyword.isPresent()) {
                List<String> retList = new ArrayList<String>();
                for (AgLibraryKeyword key : keywords.get()) {
                    retList.add(key.getName());
                }
                String json = new Gson().toJson(retList);
                return ResponseEntity.ok(json);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/byName/{lcName}/child/{nameChild}")
    public ResponseEntity<AgLibraryKeyword> getKeywordChildOfLcNameHaveNameChild(@PathVariable String lcName,@PathVariable String nameChild) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordByLcName(lcName);
        if (keyword.isPresent()) {
            Optional<AgLibraryKeyword> keywordChild = service.getKeywordChildOfIdHaveNameChild(keyword.get().getIdLocal(),nameChild);
            if (keyword.isPresent()) {
                return ResponseEntity.ok(keywordChild.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AgLibraryKeyword createKeyword(@RequestBody AgLibraryKeyword keyword) {
        return service.saveKeyword(keyword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgLibraryKeyword> updateKeyword(@PathVariable Long id, @RequestBody AgLibraryKeyword keywordDetails) {
        Optional<AgLibraryKeyword> keyword = service.getKeywordById(id);
        if (keyword.isPresent()) {
            AgLibraryKeyword updatedKeyword = keyword.get();
            updatedKeyword.setIdGlobal(keywordDetails.getIdGlobal());
            updatedKeyword.setDateCreated(keywordDetails.getDateCreated());
            updatedKeyword.setGenealogy(keywordDetails.getGenealogy());
            updatedKeyword.setImageCountCache(keywordDetails.getImageCountCache());
            updatedKeyword.setIncludeOnExport(keywordDetails.getIncludeOnExport());
            updatedKeyword.setIncludeParents(keywordDetails.getIncludeParents());
            updatedKeyword.setIncludeSynonyms(keywordDetails.getIncludeSynonyms());
            updatedKeyword.setKeywordType(keywordDetails.getKeywordType());
            updatedKeyword.setLastApplied(keywordDetails.getLastApplied());
            updatedKeyword.setLcName(keywordDetails.getLcName());
            updatedKeyword.setName(keywordDetails.getName());
            updatedKeyword.setParent(keywordDetails.getParent());
            service.saveKeyword(updatedKeyword);
            return ResponseEntity.ok(updatedKeyword);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        service.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }
}
