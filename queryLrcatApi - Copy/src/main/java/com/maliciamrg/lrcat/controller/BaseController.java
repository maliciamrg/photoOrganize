package com.maliciamrg.lrcat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class BaseController {
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/file/{pathStr}")
    public String lrcat_files(@PathVariable String pathStr) {
        return Stream.of(new File("/"+pathStr).listFiles())
                .map(File::getName)
                .collect(Collectors.toSet()).toString();
    }

}