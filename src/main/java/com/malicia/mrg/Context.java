package com.malicia.mrg;

import com.malicia.mrg.param.ElementsRejet;
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
    private RepertoireFonctionnel RepFonctionnel;
    private ElementsRejet paramElementsRejet;

    public Context() throws IOException {
        chargeElementsRejet();
        chargeRepertoirePhoto();
        chargeRepertoireFonctionnel();
        chargeNommageRepertoire();
    }

    public static Context chargeParam() throws IOException {
        return new Context();
    }

    public ElementsRejet getParamElementsRejet() {
        return paramElementsRejet;
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
            arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, files[i].toString()));
        }
    }

    private void chargeNommageRepertoire() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith("NommageRepertoire") && name.endsWith(".json"));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            paramNommageRepertoire = (NommageRepertoire) NommageRepertoire.readJSON(NommageRepertoire.class, files[i].toString());
        }
        if (paramNommageRepertoire == null) {
            paramNommageRepertoire = new NommageRepertoire();
            ElementsRejet.writeJSON(paramNommageRepertoire, "NommageRepertoire.json");
        } else {
            ElementsRejet.reWriteJSON(paramNommageRepertoire);
        }
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
        return RepFonctionnel.getRepertoireDestZip().replace("%num%", number);
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return RepFonctionnel.getRepertoireRoamingAdobeLightroom();
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
            RepFonctionnel = (RepertoireFonctionnel) RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class, files[i].toString());
        }
        if (RepFonctionnel == null) {
            RepFonctionnel = new RepertoireFonctionnel();
            RepertoireFonctionnel.writeJSON(RepFonctionnel, "RepertoireFonctionnel.json");
        } else {
            RepertoireFonctionnel.reWriteJSON(RepFonctionnel);
        }
    }

    private void chargeElementsRejet() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith("ElementsRejet") && name.endsWith(".json"));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            paramElementsRejet = (ElementsRejet) ElementsRejet.readJSON(ElementsRejet.class, files[i].toString());
        }
        if (paramElementsRejet == null) {
            paramElementsRejet = new ElementsRejet();
            ElementsRejet.writeJSON(paramElementsRejet, "ElementsRejet.json");
        } else {
            ElementsRejet.reWriteJSON(paramElementsRejet);
        }
    }

    public String getRepertoire50Phototheque() {
        return RepFonctionnel.getRepertoire50Phototheque();
    }

    public String getRepertoire00NEW() {
        return RepFonctionnel.getRepertoire00NEW();
    }

    public List<String> getArrayNomSubdirectoryRejet() {
        return paramElementsRejet.getArrayNomSubdirectoryRejet();
    }

    public String getCatalogLrcat() {
        return RepFonctionnel.getRepertoireCatalog() + File.separator + RepFonctionnel.getCatalogLrcat() ;
    }
}
