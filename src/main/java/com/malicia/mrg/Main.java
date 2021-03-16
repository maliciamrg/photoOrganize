package com.malicia.mrg;

import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.malicia.mrg.app.WorkWithFiles;
import com.malicia.mrg.app.WorkWithRepertory;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.model.ElementFichier;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.param.importjson.TriNew;
import com.malicia.mrg.util.Output;
import com.malicia.mrg.util.Serialize;
import com.malicia.mrg.util.SystemFiles;
import com.malicia.mrg.util.WhereIAm;
import com.malicia.mrg.view.JTextAreaAppender;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    //Work In Progress ??????
    private static final Boolean IS_IN_WORK = Boolean.TRUE;

    private static Context ctx;
    private static Database dbLr;

    public static void main(String[] args) {
        try {
            //intialize logging
            createLoggingPanel();
            chargeLog4j();
            //*

            // chargement application
            ctx = Context.chargeParam();
            dbLr = Database.chargeDatabaseLR(ctx.getCatalogLrcat());
            //*

            //Maintenance database lr
            maintenanceDatabase();
            ctx.getActionVersRepertoire().populate(dbLr.getFolderCollection(Context.COLLECTIONS));
            //*

            //effectuer les actions demander via le tag Lightroom
            makeActionFromKeyword();
            isInWork();

            //initialization pour nouveau démarrage
            dbLr.creationContextEtPurgeKeyword(ctx.getActionVersRepertoire().listeAction);
            dbLr.MiseAzeroDesColorLabelsRed();
            dbLr.topperARed50NEW(ctx.getParamTriNew().getRepertoire50NEW());

            //En Fonction De La Strategies De Rangement
            rangerLesRejets();
            topperLesRepertoires();
            regrouperLesNouvellesPhoto();
            //*

            //lister les possible photo oublier
            listerLesRapprochermentAvecLesRepertoirePhoto();
            //*

            //Nettoyage repertoires Local
            purgeDesRepertoireVide50Phototheque();
            //*

            //Nettoyage repertoires réseaux
            purgeDesRepertoireVide00NEW();
            //*

            //Sauvegarde Lightroom sur Local
            sauvegardeLightroomConfigSauve();
            //*

            //sauvegarde Vers Réseaux Pour Cloud
            sauvegardeStudioPhoto2Reseaux();
            //*

        } catch (Exception e) {
            e.printStackTrace();
            exceptionLog(e, LOGGER);
        }

    }

    private static void makeActionFromKeyword() throws SQLException, IOException {
        //action collection
        Map<String, String> listeAction = ctx.getActionVersRepertoire().listeAction;
        for (String key : listeAction.keySet()) {
            dbLr.sqlmoveAllFileWithTagtoRep(key + Context.TAGORG,ctx.getRepertoire50Phototheque() + listeAction.get(key));
        }
        //Action GO
        Map<String, String> fileToGo = dbLr.getFileForGoTag(Context.ACTION01GO);
        for (String key : fileToGo.keySet()) {
            String newPath = dbLr.getNewPathForGoTagandFileIdlocal(Context.ACTION01GO, key);
            dbLr.sqlmovefile(key, newPath);
        }

    }

    private static void createLoggingPanel() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        // Create logging panel
        JTextArea jLoggingConsole = new JTextArea(5, 0); // 5 lines high here
        jLoggingConsole.setLineWrap(true);
        jLoggingConsole.setWrapStyleWord(true);
        jLoggingConsole.setEditable(false);
        jLoggingConsole.setFont(new Font("Courier", Font.PLAIN, 12));

        // Make scrollable console pane
        JScrollPane jConsoleScroll = new JScrollPane(jLoggingConsole);
        jConsoleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Subscribe the text area to JTextAreaAppender
        JTextAreaAppender.addLog4j2TextAreaAppender(jLoggingConsole);

        //------------------------

        // now add the scrollpane to the jframe's content pane, specifically
        // placing it in the center of the jframe's borderlayout
        JFrame frame = new JFrame("PhotoOrganize");
        frame.getContentPane().add(jConsoleScroll, BorderLayout.CENTER);

        // make it easy to close the application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set the frame size (you'll usually want to call frame.pack())
        frame.setSize(new Dimension(1024, 600));

        // center the frame
        frame.setLocationRelativeTo(null);

        // make it visible to the user
        frame.setVisible(true);
    }

    private static void maintenanceDatabase() throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        splitLOGGERInfo(dbLr.pathAbsentPhysique());
        splitLOGGERInfo(dbLr.folderWithoutRoot());
        splitLOGGERInfo(dbLr.folderAbsentPhysique());
        splitLOGGERInfo(dbLr.fileWithoutFolder());
        splitLOGGERInfo(dbLr.keywordImageWithoutKeyword());
    }

    private static void splitLOGGERInfo(String txt) {
        String[] atxt = txt.split("\n");
        for (String s : atxt) {
            LOGGER.info(s);
        }
    }


    private static void isInWork() {
        if (Boolean.TRUE.equals(IS_IN_WORK)) {
            throw new IllegalStateException("Under Construct");
        }
    }

    private static void chargeLog4j() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        LOGGER.fatal("                                                                                    ");
        LOGGER.fatal("          <==============================================================>          ");
        LOGGER.fatal("    <===                                                                    ===>    ");
        LOGGER.fatal(" <=====        S T A R T   A P P L I C A T I O N   P H O T O T R I 2         =====> ");
        LOGGER.fatal("    <===                                                                    ===>    ");
        LOGGER.fatal("          <==============================================================>          ");
        LOGGER.fatal("                                                                                    ");
//        OFF	0
//        FATAL	100
//        ERROR	200
//        WARN	300
//        INFO	400
//        DEBUG	500
//        TRACE	600
//        ALL	Integer.MAX_VALUE
        LOGGER.trace("---==[ trace  ]==---");
        LOGGER.debug("---==[ debug ]==---");
        LOGGER.info("---==[  info   ]==---");
        LOGGER.warn("---==[  warn   ]==---");
        LOGGER.error("---==[ error  ]==---");
        LOGGER.fatal("---==[  fatal  ]==---");
        LOGGER.info("Start");
    }

    private static void sauvegardeStudioPhoto2Reseaux() throws Exception {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        RSync rsync = new RSync()
                .sources(ctx.getRepFonctionnel().getRepertoiresyncsource())
                .destination(ctx.getRepFonctionnel().getRepertoiresyncdest())
                .recursive(true)
                .exclude(ctx.getRepFonctionnel().getRsyncexclude())
//                .dryRun(true)
                .humanReadable(true)
                .archive(true)
                .delete(true)
                .verbose(true);
        StreamingProcessOutput output = new StreamingProcessOutput(new Output());
        output.monitor(rsync.builder());

    }

    private static void regrouperLesNouvellesPhoto() throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Regroupement
        String repertoire50NEW = ctx.getParamTriNew().getRepertoire50NEW();
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), repertoire50NEW);

        GrpPhoto listFileBazar = new GrpPhoto();
        GrpPhoto listElekidz = new GrpPhoto();
        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();


        List<String> listkidsModel = ctx.getParamTriNew().getListeModelKidz();
        long maxprev = 0;
        while (rsele.next()) {

            if (rsele.getBoolean("isNew")) {
                // Recuperer les info de l'elements
                String fileIdLocal = rsele.getString("file_id_local");
                String absolutePath = rsele.getString("absolutePath");
                String pathFromRoot = rsele.getString("pathFromRoot");
                String lcIdxFilename = rsele.getString("lc_idx_filename");
                String cameraModel = rsele.getString("CameraModel");
                long mint = rsele.getLong("mint");
                long maxt = rsele.getLong("maxt");
                long captureTime = rsele.getLong("captureTime");

                ElementFichier eleFile = new ElementFichier(absolutePath, pathFromRoot, lcIdxFilename, fileIdLocal);

                if (listkidsModel.contains(cameraModel)) {
                    listElekidz.add(eleFile);
                } else {
                    if (mint > maxprev) {

                        if (listEletmp.size() > ctx.getParamTriNew().getThresholdNew()) {
                            listGrpEletmp.add(listEletmp);
                        } else {
                            listFileBazar.addAll(listEletmp);
                        }

                        listEletmp = new GrpPhoto();

                    }
                    maxprev = maxt;
                    listEletmp.setFirstDate(captureTime);
                    listEletmp.add(eleFile);
                }
            }
        }
        if (listEletmp.size() > ctx.getParamTriNew().getThresholdNew()) {
            listGrpEletmp.add(listEletmp);
        } else {
            listFileBazar.addAll(listEletmp);
        }

        deplacementDesGroupes(listFileBazar, listElekidz, listGrpEletmp);


    }


    private static void listerLesRapprochermentAvecLesRepertoirePhoto() throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Regroupement
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser( ctx.getParamTriNew().getTempsAdherence(), "");

        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();

        long maxprev = 0;
        while (rsele.next()) {

            // Recuperer les info de l'elements
            String fileIdLocal = rsele.getString("file_id_local");
            String absolutePath = rsele.getString("absolutePath");
            String pathFromRoot = rsele.getString("pathFromRoot");
            String lcIdxFilename = rsele.getString("lc_idx_filename");
            long mint = rsele.getLong("mint");
            long maxt = rsele.getLong("maxt");
            long captureTime = rsele.getLong("captureTime");

            // recherche du repPhoto concerner
            String ch = absolutePath + pathFromRoot;
            List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();
            int numeroRep = -1;
            int i;
            for (i = 0; i < arrayRepertoirePhoto.size(); i++) {
                if (arrayRepertoirePhoto.get(i).isRapprochermentNewOk()) {
                    if (ch.startsWith(SystemFiles.normalizePath(ctx.getRepertoire50Phototheque() + arrayRepertoirePhoto.get(i).getRepertoire()))) {
                        numeroRep = i;
                    }
                }
            }
            if (ch.startsWith(SystemFiles.normalizePath(ctx.getParamTriNew().getRepertoire50NEW()))) {
                numeroRep = Context.IREP_NEW;
            }


            if (numeroRep > -1) {
                ElementFichier eleFile = new ElementFichier(absolutePath, pathFromRoot, lcIdxFilename, fileIdLocal, numeroRep);

                if (mint > maxprev) {

                    listGrpEletmp.add(listEletmp);

                    listEletmp = new GrpPhoto();

                }
                maxprev = maxt;
                listEletmp.setFirstDate(captureTime);
                listEletmp.add(eleFile);
            }

        }
        if (listEletmp.size() > 0) {
            listGrpEletmp.add(listEletmp);
        }


        //display des rapprochementd

        for (GrpPhoto listEle : listGrpEletmp) {

            if (listEle.size() > 1 && listEle.getArrayRep(Context.IREP_NEW) > 0 && listEle.size() > listEle.getArrayRep(Context.IREP_NEW)) {

                actionRapprochementNewREpPhoto(listEle);

            }

        }


    }

    private static void actionRapprochementNewREpPhoto(GrpPhoto listEle) throws SQLException {
        Context.nbDiscretionnaire++;
        String nbDiscr = String.format("%1$03X", Context.nbDiscretionnaire);
        String tag = Context.TAGORG + "_" + nbDiscr + "_" + "possibleNewGroup";
        for (ElementFichier eleFile : listEle.lstEleFile) {
            dbLr.AddKeywordToFile(eleFile.getFileIdLocal(), tag);
        }


        LOGGER.info("---------------------------");
        int nbele = listEle.lstEleFile.size();
        LOGGER.info(String.format("%05d", nbele) + " ===========");
        int i;
        for (i = 0; i < ctx.getArrayRepertoirePhoto().size(); i++) {
            if (listEle.getArrayRep(i) > 0) {
                LOGGER.info(String.format("%05d", listEle.getArrayRep(i)) + " - " + ctx.getArrayRepertoirePhoto().get(i).getRepertoire());
            }
        }
        LOGGER.info(String.format("%05d", listEle.getArrayRep(Context.IREP_NEW)) + " - " + ctx.getParamTriNew().getRepertoire50NEW());

    }

    private static void deplacementDesGroupes(GrpPhoto listFileBazar, GrpPhoto listElekidz, List<GrpPhoto> listGrpEletmp) throws IOException, SQLException {
        //deplacement des group d'elements New Trier
        int nbtot = 0;
        LOGGER.info("listGrpEletmp       => " + String.format("%05d", listGrpEletmp.size()));
        for (GrpPhoto listEle : listGrpEletmp) {
            nbtot += listEle.lstEleFile.size();
            SimpleDateFormat repDateFormat = new SimpleDateFormat(TriNew.FORMATDATE_YYYY_MM_DD);
            String nomRep = repDateFormat.format(new Date(listEle.getFirstDate() * 1000)) + "_(" + String.format("%05d", listEle.lstEleFile.size()) + ")";
            for (ElementFichier eleGrp : listEle.lstEleFile) {

                String newName = ctx.getParamTriNew().getRepertoire50NEW() + nomRep + File.separator + eleGrp.getLcIdxFilename();
                WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);

            }
        }
        LOGGER.info("listGrpEletmp nbtot => " + String.format("%05d", nbtot));

        //deplacement des group d'elements Kidz
        LOGGER.info("listElekidz         => " + String.format("%05d", listElekidz.lstEleFile.size()));
        for (ElementFichier eleGrp : listElekidz.lstEleFile) {
            String newName = ctx.getParamTriNew().getRepertoireKidz() + File.separator + eleGrp.getLcIdxFilename();
            WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
        }

        //deplacement des group d'elements Bazar
        LOGGER.info("listFileBazar       => " + String.format("%05d", listFileBazar.lstEleFile.size()));
        for (ElementFichier eleGrp : listFileBazar.lstEleFile) {
            String newName = ctx.getParamTriNew().getRepertoireBazar() + File.separator + eleGrp.getLcIdxFilename();
            WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
        }
    }

    private static void topperLesRepertoires() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();

        ListIterator<RepertoirePhoto> repertoirePhotoIterator = arrayRepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            RepertoirePhoto repPhoto = repertoirePhotoIterator.next();
            LOGGER.info("topperLesRepertoires = > " + ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire());
            List<String> listRep = WorkWithRepertory.listRepertoireEligible(ctx.getRepertoire50Phototheque(), repPhoto);

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {
                String repertoire = repertoireIterator.next();

                if (!WorkWithRepertory.isRepertoireOk(dbLr, repertoire, repPhoto, ctx.getParamControleRepertoire())) {
                    LOGGER.debug(repertoire + "=>" + "ko");
                }

            }
            File f = new File(ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire() + Context.FOLDERDELIM + new File(repPhoto.getRepertoire()).getName() + ".svg.json");
            Serialize.writeJSON(repPhoto, f);
            LOGGER.debug("ecriture fichier ->" + f.toString());

        }
    }

    private static void sauvegardeLightroomConfigSauve() throws ZipException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        // zip file with a folder
        String repertoireDestZip = ctx.getRepertoireDestZip();
        File f = new File(repertoireDestZip);
        if (f.isFile()) {
            f.delete();
        }
        File RepertoireRoamingAdobeLightroom = new File(ctx.getRepertoireRoamingAdobeLightroom());
        new ZipFile(repertoireDestZip).addFolder(RepertoireRoamingAdobeLightroom);
    }

    private static void purgeDesRepertoireVide50Phototheque() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getRepertoire50Phototheque();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation);
        } while (!isFinished);
    }

    private static void purgeDesRepertoireVide00NEW() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getRepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation);
        } while (!isFinished);
    }


    private static void rangerLesRejets() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<File> arrayFichierRejet = WorkWithFiles.getFilesFromRepertoryWithFilter(ctx.getRepertoire50Phototheque(), ctx.getArrayNomSubdirectoryRejet(), ctx.getParamElementsRejet().getExtFileRejet());

        Map<String, Integer> countExt = new HashMap<>();
        LOGGER.info("arrayFichierRejet     => " + arrayFichierRejet.size());

        ListIterator<File> arrayFichierRejetIterator = arrayFichierRejet.listIterator();
        while (arrayFichierRejetIterator.hasNext()) {
            File fichier = arrayFichierRejetIterator.next();
            String fileExt = FilenameUtils.getExtension(fichier.getName()).toLowerCase();

            if (countExt.containsKey(fileExt)) {
                countExt.replace(fileExt, countExt.get(fileExt), countExt.get(fileExt) + 1);
            } else {
                countExt.put(fileExt, 1);
            }

            if (fileExt.toLowerCase().compareTo("zip") == 0) {
                LOGGER.info("unzip :" + fichier);
                WorkWithFiles.extractZipFile(fichier);
            }

            if (ctx.getParamElementsRejet().getArrayNomFileRejet().contains(fileExt.toLowerCase())) {
                String oldName = fichier.toString();
                String newName = oldName + "." + ctx.getParamElementsRejet().getExtFileRejet();
                File fsource = new File(oldName);
                if (!fsource.exists() || !fsource.isFile()) {
                    WorkWithFiles.renameFile(oldName, newName, dbLr);
                }
            }
        }

        Iterator it = countExt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info("arrayFichierRejet." + pair.getKey() + " => " + pair.getValue());
        }


    }

    public static void exceptionLog(Exception theException, Logger loggerOrigine) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        theException.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        loggerOrigine.fatal("theException = " + "\n" + stringWriter.toString());
    }


}
