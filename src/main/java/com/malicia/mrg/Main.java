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
    private static final Boolean IS_IN_WORK = Boolean.FALSE  ;

    private static Context ctx;
    private static Database dbLr;

    public static void main(String[] args) {
        try {
            createLoggingPanel();

            chargeLog4j();

            /// chargement application
            ctx = Context.chargeParam();
            dbLr = Database.chargeDatabaseLR(ctx.getCatalogLrcat());
            //*

            isInWork();

            //Maintenance database lr
            maintenanceDatabase();

            //En Fonction De La Strategies De Rangement
            rangerLesRejets();
            topperLesRepertoires();
            regrouperLesNouvellesPhoto();
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

    private static void createLoggingPanel() {

    // Create logging panel
        JTextArea jLoggingConsole = new JTextArea(5,0); // 5 lines high here
        jLoggingConsole.setLineWrap(true);
        jLoggingConsole.setWrapStyleWord(true);
        jLoggingConsole.setEditable (false);
        jLoggingConsole.setFont(new Font("Courier", Font.PLAIN, 12));

        // Make scrollable console pane
        JScrollPane jConsoleScroll = new JScrollPane(jLoggingConsole);
        jConsoleScroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

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
        frame.setSize(new Dimension(800, 400));

        // center the frame
        frame.setLocationRelativeTo(null);

        // make it visible to the user
        frame.setVisible(true);
    }

    private static void maintenanceDatabase() throws SQLException {

        Split_LOGGER_info(dbLr.pathAbsentPhysique());
        Split_LOGGER_info(dbLr.folderWithoutRoot());
        Split_LOGGER_info(dbLr.folderAbsentPhysique());
        Split_LOGGER_info(dbLr.fileWithoutFolder());
    }

    private static void Split_LOGGER_info(String txt) {
        String[] atxt = txt.split("\n");
        for (String s: atxt) {
            LOGGER.info(s);
        }
    }


    private static void isInWork() {
        if (Boolean.TRUE.equals(IS_IN_WORK)) {
            throw new IllegalStateException("Under Construct");
        }
    }

    private static void chargeLog4j() {
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

        //Regroupement
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), ctx.getParamTriNew().getRepertoire50NEW());

        GrpPhoto listFileBazar = new GrpPhoto();
        GrpPhoto listElekidz = new GrpPhoto();
        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();


        List<String> listkidsModel = ctx.getParamTriNew().getListeModelKidz();
        long maxprev = 0;
        while (rsele.next()) {

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
        if (listEletmp.size() > ctx.getParamTriNew().getThresholdNew()) {
            listGrpEletmp.add(listEletmp);
        } else {
            listFileBazar.addAll(listEletmp);
        }

        deplacementDesGroupes(listFileBazar, listElekidz, listGrpEletmp);

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

            File f = new File(ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire() + "\\" + new File(repPhoto.getRepertoire()).getName() +  ".svg.json");
            Serialize.writeJSON(repPhoto, f);
            LOGGER.debug("ecriture fichier ->" + f.toString());

        }
    }

    private static void sauvegardeLightroomConfigSauve() throws ZipException {
        // zip file with a folder
        new ZipFile(ctx.getRepertoireDestZip()).addFolder(new File(ctx.getRepertoireRoamingAdobeLightroom()));
    }

    private static void purgeDesRepertoireVide50Phototheque() {
        String folderlocation = ctx.getRepertoire50Phototheque();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation);
        } while (!isFinished);
    }

    private static void purgeDesRepertoireVide00NEW() {
        String folderlocation = ctx.getRepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation);
        } while (!isFinished);
    }


    private static void rangerLesRejets() throws IOException, SQLException {
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
