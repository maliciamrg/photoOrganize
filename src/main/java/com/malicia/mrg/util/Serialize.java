package com.malicia.mrg.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Serialize {

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileNameIn) {
        fileName = fileNameIn;
    }

    public static void writeJSON(Object o, File fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(fileName, o);
    }

    public static void writeJSON(Object o, String fileName) throws IOException {

        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        File f = new File(rootPath + "objJson\\" + fileName);
        File f = new File(rootPath + fileName);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(f, o);
    }

    public static void reWriteJSON(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(((Serialize)o).fileName), o);
    }

    public static Object readJSON(Class aClass, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Object objret = mapper.readValue(new File(fileName), aClass);
        ((Serialize)objret).fileName = fileName;
        return objret ;
    }

    public static Object readJSON(Class aClass, InputStream stream) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if (stream==null){
            Constructor con = aClass.getConstructor();
            Object xyz = con.newInstance();
            writeJSON(xyz,aClass.getSimpleName()+".json");
            return xyz;
        }
        ObjectMapper mapper = new ObjectMapper();
        Object objret = mapper.readValue(stream, aClass);
        ((Serialize)objret).fileName = stream.toString();
        return objret ;
    }
}
