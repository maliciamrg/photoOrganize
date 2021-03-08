package com.malicia.mrg;

import com.malicia.mrg.param.importjson.*;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Context {
    private static final String JSON = ".json";
    private ControleRepertoire paramControleRepertoire;
    private List<RepertoirePhoto> arrayRepertoirePhoto = FXCollections.observableArrayList();
    private RepertoireFonctionnel repFonctionnel;
    private ElementsRejet paramElementsRejet;

    public TriNew getParamTriNew() {
        return paramTriNew;
    }

    private TriNew paramTriNew;

    public Context() throws IOException {
        chargeElementsRejet();
        chargeRepertoirePhoto();
        chargeRepertoireFonctionnel();
        chargeControleRepertoire();
        chargeParamTriNew();
    }

    public static Context chargeParam() throws IOException {
        return new Context();
    }

    public ElementsRejet getParamElementsRejet() {
        return paramElementsRejet;
    }

    private void chargeParamTriNew() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        final String triNew = "TriNew";
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith(triNew) && name.endsWith(JSON));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            paramTriNew = (TriNew) TriNew.readJSON(TriNew.class, files[i].toString());
        }
        if (paramTriNew == null) {
            paramTriNew = new TriNew();
            TriNew.writeJSON(paramTriNew, triNew + JSON);
        } else {
            TriNew.reWriteJSON(paramTriNew);
        }
    }

    private void chargeRepertoirePhoto() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                String repertoirePhoto = "repertoirePhoto";
                return (name.startsWith(repertoirePhoto) && name.endsWith(JSON));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, files[i].toString()));
        }
    }

    private void chargeControleRepertoire() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        final String controleRepertoire = "ControleRepertoire";
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith(controleRepertoire) && name.endsWith(JSON));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            paramControleRepertoire = (ControleRepertoire) ControleRepertoire.readJSON(ControleRepertoire.class, files[i].toString());
        }
        if (paramControleRepertoire == null) {
            paramControleRepertoire = new ControleRepertoire();
            ElementsRejet.writeJSON(paramControleRepertoire, controleRepertoire + JSON);
        } else {
            ElementsRejet.reWriteJSON(paramControleRepertoire);
        }
    }

    public ControleRepertoire getParamControleRepertoire() {
        return paramControleRepertoire;
    }

    public List<RepertoirePhoto> getArrayRepertoirePhoto() {
        return arrayRepertoirePhoto;
    }

    public String getRepertoireDestZip() {
        int n = (int) Math.floor(Math.random() * Double.valueOf(repFonctionnel.getNbRotateRepertoireDestZip()) + 1);
        NumberFormat formatter = new DecimalFormat("00000");
        String number = formatter.format(n);
        return repFonctionnel.getRepertoireDestZip().replace("%num%", number);
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return repFonctionnel.getRepertoireRoamingAdobeLightroom();
    }


    private void chargeRepertoireFonctionnel() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        final String repertoireFonctionnel = "RepertoireFonctionnel";
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith(repertoireFonctionnel) && name.endsWith(JSON));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            repFonctionnel = (RepertoireFonctionnel) RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class, files[i].toString());
        }
        if (repFonctionnel == null) {
            repFonctionnel = new RepertoireFonctionnel();
            RepertoireFonctionnel.writeJSON(repFonctionnel, repertoireFonctionnel + JSON);
        } else {
            RepertoireFonctionnel.reWriteJSON(repFonctionnel);
        }
    }

    private void chargeElementsRejet() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File f = new File(rootPath + "objJson\\");
        final String elementsRejet = "ElementsRejet";
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return (name.startsWith(elementsRejet) && name.endsWith(JSON));
            }
        };
        File[] files = f.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            paramElementsRejet = (ElementsRejet) ElementsRejet.readJSON(ElementsRejet.class, files[i].toString());
        }
        if (paramElementsRejet == null) {
            paramElementsRejet = new ElementsRejet();
            ElementsRejet.writeJSON(paramElementsRejet, elementsRejet + JSON);
        } else {
            ElementsRejet.reWriteJSON(paramElementsRejet);
        }
    }

    public String getRepertoire50Phototheque() {
        return repFonctionnel.getRepertoire50Phototheque();
    }

    public String getRepertoire00NEW() {
        return repFonctionnel.getRepertoire00NEW();
    }

    public List<String> getArrayNomSubdirectoryRejet() {
        return paramElementsRejet.getArrayNomSubdirectoryRejet();
    }

    public String getCatalogLrcat() {
        return repFonctionnel.getRepertoireCatalog() + File.separator + repFonctionnel.getCatalogLrcat();
    }
}
