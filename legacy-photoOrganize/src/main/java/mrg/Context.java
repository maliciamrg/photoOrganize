package mrg;

import com.malicia.mrg.util.WhereIAm;
import mrg.param.electx.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Context {

    public static final String FOLDERDELIM = "\\";
    public static final int IREP_NEW = 10;

    public static final String RED = "Red";
    public static final String GREEN = "Green";
    public static final String TAG_ORG = "#ORG#";
    public static final String TAG_RAPPROCHEMENT = TAG_ORG + "RAPPROCHEMENT";
    public static final String TAG_ACTION_GO = "GO" + "->";
    public static final String TAG_ACTION_GO_RAPPROCHEMENT = TAG_ACTION_GO + Context.TAG_RAPPROCHEMENT;
    public static final String TAG_REPSWEEP = TAG_ORG + "REPSWEEP";
    public static final String COLLECTIONS = "!!Collections";
    public static final String POSSIBLE_NEW_GROUP = "possibleRegroupement";
    public static final String PREFIX = " --- ";
    public static final int INT_WIDTH = 120;
    public static final String ERR_404_JPG = "D:\\JavaProjet\\photoOrganize\\src\\main\\resources\\err404.jpg";
    public static final String JPG = "jpg";
    public static final String filtreImportScanUn = " left join AgLibraryKeywordImage ki " +
            " on ki.image = e.id_local " +
            " left join AgLibraryKeyword k " +
            " on k.id_local = ki.tag ";
    public static final String filtreImportScanDeux = " and k.lc_name != 'import_argentique_bf_evolution' ";
    public static final long GMT01JAN200112AM = 978307200;
    private static final String JSON = ".json";
    private static final Logger LOGGER = LogManager.getLogger(Context.class);
    public static int nbDiscretionnaire = 0;
    public static String localVoidPhotoUrl;
    public static String localErr404PhotoUrl;
    public static String localErrPhotoUrl;
    public final ActionRepertoire actionVersRepertoire;
    public final List<RepertoirePhoto> arrayRepertoirePhoto = new ArrayList<>();
    public Workflow workflow;
    public ControleRepertoire paramControleRepertoire;
    public RepertoireFonctionnel repFonctionnel;
    public ElementsRejet paramElementsRejet;
    public List<String> extensionsUseFile;
    public TriNew paramTriNew;
    public DataApplication dataApplication;
    public Context() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, URISyntaxException {


//        paramElementsRejet = (ElementsRejet) ElementsRejet.readJSON(ElementsRejet.class, getResourceAsStream("ElementsRejet.json"));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Autoconstruction.json")));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Events.json")));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Holidays.json")));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Rejet.json")));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Sauvegarde.json")));
//        arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, getResourceAsStream("repertoirePhoto-Shooting.json")));
//        repFonctionnel = (RepertoireFonctionnel) RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class, getResourceAsStream("RepertoireFonctionnel.json"));
        //       paramControleRepertoire = (ControleRepertoire) ControleRepertoire.readJSON(ControleRepertoire.class, getResourceAsStream("ControleRepertoire.json"));
//        paramTriNew = (TriNew) TriNew.readJSON(TriNew.class, getResourceAsStream("TriNew.json"));

        actionVersRepertoire = new ActionRepertoire();

//
//        localVoidPhotoUrl = Objects.requireNonNull(Context.class.getClassLoader().getResource("images.png")).toURI().toURL().toExternalForm();
//        localErr404PhotoUrl = Objects.requireNonNull(Context.class.getClassLoader().getResource("err404.jpg")).toURI().toURL().toExternalForm();
//        localErrPhotoUrl = Objects.requireNonNull(Context.class.getClassLoader().getResource("error.jpg")).toURI().toURL().toExternalForm();


    }

    public static Context YamlConfigRunner(String args) throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (args.compareTo("") == 1) {
            System.out.println("Usage: <file.yml>");
            return null;
        }

        Context contexte = new Context();
        Yaml yaml = new Yaml();

        try (InputStream in = Context.class.getResourceAsStream("/" + args)) {
//        try( InputStream in = Files.newInputStream( Paths.get( args ) ) ) {
            contexte = yaml.loadAs(in, Context.class);
            LOGGER.debug(contexte.toString());
        }
        return contexte;
    }

    public static Context chargeParam() throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, URISyntaxException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        return YamlConfigRunner("ContextApplication.yaml");
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public List<String> getExtensionsUseFile() {
        return extensionsUseFile;
    }

    public void setExtensionsUseFile(List<String> extensionsUseFile) {
        this.extensionsUseFile = extensionsUseFile;
    }

    @Override
    public String toString() {
        return "Context{" +
                "workflow=" + workflow +
                ", actionVersRepertoire=" + actionVersRepertoire +
                ", arrayRepertoirePhoto=" + arrayRepertoirePhoto +
                ", paramControleRepertoire=" + paramControleRepertoire +
                ", repFonctionnel=" + repFonctionnel +
                ", paramElementsRejet=" + paramElementsRejet +
                ", paramTriNew=" + paramTriNew +
                '}';
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

//    private void chargeParamTriNew() throws IOException {
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        File f = new File(rootPath + "objJson\\");
//        final String triNew = "TriNew";
//        FilenameFilter filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File f, String name) {
//                return (name.startsWith(triNew) && name.endsWith(JSON));
//            }
//        };
//        File[] files = f.listFiles(filter);
//        for (int i = 0; i < files.length; i++) {
//            paramTriNew = (TriNew) TriNew.readJSON(TriNew.class, files[i].toString());
//        }
//        if (paramTriNew == null) {
//            paramTriNew = new TriNew();
//            TriNew.writeJSON(paramTriNew, triNew + JSON);
//        } else {
//            TriNew.reWriteJSON(paramTriNew);
//        }
//    }

//    private void chargeRepertoirePhoto() throws IOException {
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        File f = new File(rootPath + "objJson\\");
//        FilenameFilter filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File f, String name) {
//                String repertoirePhoto = "repertoirePhoto";
//                return (name.startsWith(repertoirePhoto) && name.endsWith(JSON));
//            }
//        };
//        File[] files = f.listFiles(filter);
//        for (int i = 0; i < files.length; i++) {
//            arrayRepertoirePhoto.add((RepertoirePhoto) RepertoirePhoto.readJSON(RepertoirePhoto.class, files[i].toString()));
//        }
//    }

//    private void chargeControleRepertoire() throws IOException {
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        File f = new File(rootPath + "objJson\\");
//        final String controleRepertoire = "ControleRepertoire";
//        FilenameFilter filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File f, String name) {
//                return (name.startsWith(controleRepertoire) && name.endsWith(JSON));
//            }
//        };
//        File[] files = f.listFiles(filter);
//        for (int i = 0; i < files.length; i++) {
//            paramControleRepertoire = (ControleRepertoire) ControleRepertoire.readJSON(ControleRepertoire.class, files[i].toString());
//        }
//        if (paramControleRepertoire == null) {
//            paramControleRepertoire = new ControleRepertoire();
//            ElementsRejet.writeJSON(paramControleRepertoire, controleRepertoire + JSON);
//        } else {
//            ElementsRejet.reWriteJSON(paramControleRepertoire);
//        }
//    }

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


//    private void chargeRepertoireFonctionnel() throws IOException {
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        File f = new File(rootPath + "objJson\\");
//        final String repertoireFonctionnel = "RepertoireFonctionnel";
//        FilenameFilter filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File f, String name) {
//                return (name.startsWith(repertoireFonctionnel) && name.endsWith(JSON));
//            }
//        };
//        File[] files = f.listFiles(filter);
//        for (int i = 0; i < files.length; i++) {
//            repFonctionnel = (RepertoireFonctionnel) RepertoireFonctionnel.readJSON(RepertoireFonctionnel.class, files[i].toString());
//        }
//        if (repFonctionnel == null) {
//            repFonctionnel = new RepertoireFonctionnel();
//            RepertoireFonctionnel.writeJSON(repFonctionnel, repertoireFonctionnel + JSON);
//        } else {
//            RepertoireFonctionnel.reWriteJSON(repFonctionnel);
//        }
//    }

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

//    private List<String> getResourceFiles(String path) throws IOException {
//        List<String> filenames = new ArrayList<>();
//
//        try (
//                InputStream in = getResourceAsStream(path);
//                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
//            String resource;
//
//            while ((resource = br.readLine()) != null) {
//                filenames.add(resource);
//            }
//        }
//
//        return filenames;
//    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public void updateWorkflow(String resourceFileWorkflow) throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        Context contexte = new Context();
        Yaml yaml = new Yaml();

        try (InputStream in = Context.class.getResourceAsStream("/" + resourceFileWorkflow)) {
//        try( InputStream in = Files.newInputStream( Paths.get( args ) ) ) {
            contexte = yaml.loadAs(in, Context.class);
            setWorkflow(contexte.getWorkflow());
            LOGGER.debug(toString());
        }
    }
}
