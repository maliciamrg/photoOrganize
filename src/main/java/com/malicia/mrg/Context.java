package com.malicia.mrg;

import com.malicia.mrg.param.NommageRepertoire;
import com.malicia.mrg.param.RepertoireFonctionnel;
import com.malicia.mrg.param.RepertoirePhoto;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Context {
    private NommageRepertoire paramNommageRepertoire;
    private List<RepertoirePhoto> arrayRepertoirePhoto = FXCollections.observableArrayList();
    private String repertoireRoamingAdobeLightroom;
    private String repertoireDestZip;
    private List<String> arrayNomSubdirectoryRejet = FXCollections.observableArrayList();
    private String repertoire50Phototheque;
    private String repertoire00NEW;
    private RepertoireFonctionnel RepFonctionnel;

    public Context() throws IOException {
        chargeArrayNomSubdirectoryRejet();
        chargeRepertoirePhoto();
        chargeRepertoireFonctionnel();
        chargeNommageRepertoire();
    }

    private void chargeRepertoirePhoto() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith("repertoirePhoto") && name.endsWith(".json"));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            arrayRepertoirePhoto.add((RepertoirePhoto)  RepertoirePhoto.readJSON(RepertoirePhoto.class ,files[i].toString()));
        }
    }

    private void chargeNommageRepertoire() {
        //todo
        paramNommageRepertoire = null;
    }

    public static Context chargeParam() throws IOException {
        return new Context();
    }

    public NommageRepertoire getParamNommageRepertoire() {
        return paramNommageRepertoire;
    }

    public List<RepertoirePhoto> getArrayRepertoirePhoto() {
        return arrayRepertoirePhoto;
    }

    public String getRepertoireDestZip() {
        int n = (int) Math.floor(Math.random() * 100000 + 1);
        NumberFormat formatter = new DecimalFormat("00000");
        String number = formatter.format(n);
        return repertoireDestZip.replace("%num%", number);
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return repertoireRoamingAdobeLightroom;
    }


    private void chargeRepertoireFonctionnel() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith("RepertoireFonctionnel") && name.endsWith(".json"));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            RepFonctionnel = (RepertoireFonctionnel)  RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class ,files[i].toString());
        }
        if (RepFonctionnel ==null){
            RepFonctionnel = new RepertoireFonctionnel();
            RepertoireFonctionnel.writeJSON(RepFonctionnel,"RepertoireFonctionnel.json");
        } else {
            RepertoireFonctionnel.reWriteJSON(RepFonctionnel);
        }
    }

    public List<String> getArrayNomSubdirectoryRejet() {
        return arrayNomSubdirectoryRejet;
    }

    private void chargeArrayNomSubdirectoryRejet() {
        //TODO
        this.arrayNomSubdirectoryRejet = null;
    }

    public String getRepertoire50Phototheque() {
        return repertoire50Phototheque;
    }

    public String getRepertoire00NEW() {
        return repertoire00NEW;
    }

}
