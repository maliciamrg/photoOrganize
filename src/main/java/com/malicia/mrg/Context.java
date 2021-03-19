package com.malicia.mrg;

import com.malicia.mrg.param.importjson.*;
import com.malicia.mrg.util.WhereIAm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Context {

    public static final String FOLDERDELIM = "\\";
    public static final int IREP_NEW = 10;
    public static final String RED = "Red";
    public static final String TAGORG = "#ORG#";
    public static final String ACTION01GO = "GO" + Context.TAGORG;
    public static final String COLLECTIONS = "!!Collections";
    private static final String JSON = ".json";
    private static final Logger LOGGER = LogManager.getLogger(Context.class);
    public static int nbDiscretionnaire = 0;
    private final ActionRepertoire actionVersRepertoire;
    private final List<RepertoirePhoto> arrayRepertoirePhoto = new ArrayList<>();
    private ControleRepertoire paramControleRepertoire;
    private RepertoireFonctionnel repFonctionnel;
    private ElementsRejet paramElementsRejet;
    private TriNew paramTriNew;

    public Context() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        paramElementsRejet = (ElementsRejet) ElementsRejet.readJSON(ElementsRejet.class, getResourceAsStream("ElementsRejet.json"));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Autoconstruction.json")));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Events.json")));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Holidays.json")));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Rejet.json")));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Sauvegarde.json")));
        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Shooting.json")));
        repFonctionnel = (RepertoireFonctionnel) RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class, getResourceAsStream("RepertoireFonctionnel.json"));
        paramControleRepertoire = (ControleRepertoire) ControleRepertoire.readJSON(ControleRepertoire.class, getResourceAsStream("ControleRepertoire.json"));
        paramTriNew = (TriNew) TriNew.readJSON(TriNew.class, getResourceAsStream("TriNew.json"));

        actionVersRepertoire = new ActionRepertoire() ;

    }

    public static Context chargeParam() throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        return new Context();
    }

    public ActionRepertoire getActionVersRepertoire() {
        return actionVersRepertoire;
    }

    public RepertoireFonctionnel getRepFonctionnel() {
        return repFonctionnel;
    }

    public TriNew getParamTriNew() {
        return paramTriNew;
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

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
