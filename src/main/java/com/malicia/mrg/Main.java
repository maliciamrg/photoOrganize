package com.malicia.mrg;

import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.malicia.mrg.app.*;
import com.malicia.mrg.app.rep.AnalyseGlobalRepertoires;
import com.malicia.mrg.app.rep.BlocRetourRepertoire;
import com.malicia.mrg.app.rep.EleChamp;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.model.ElementFichier;
import com.malicia.mrg.param.electx.RepertoirePhoto;
import com.malicia.mrg.param.electx.TriNew;
import com.malicia.mrg.util.Output;
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
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static Context ctx;
    private static Database dbLr;
    private static JFrame frame;
    private static AnalyseGlobalRepertoires analFonctionRep;
    private static JProgressBar progress;
    private static int unNbMisAREDRep = 0;

    public static void main(String[] args) {
        try {
            //intialize logging
            createLoggingPanel();
            chargeLog4j();
            //*

            LOGGER.info(InfoVersion.showVersionInfo());


            // chargement application
            ctx = Context.chargeParam();
            closelightroom(ctx.dataApplication.getApplicationToClose());
            dbLr = Database.chargeDatabaseLR(ctx.getCatalogLrcat(), ctx.workflow.IS_DRY_RUN);
//            AnalyseGlobalRepertoires.init(ctx, dbLr);

            SystemFiles.setIsDryRun(ctx.workflow.IS_DRY_RUN);
            ctx.getActionVersRepertoire().populate(dbLr.getFolderCollection(Context.COLLECTIONS, Context.TAG_ORG, ""));
            //ctx.getActionVersRepertoire().populate(dbLr.getFolderCollection("01-Cataloque_Photo", Context.TAG_ORG, ""));
            //*

            // chargement parameter
            chargementParametre(ctx, args);
            //*

            displayBooleen();

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
                //Maintenance database lr
                maintenanceDatabase();
                //*
            }

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_ACTION_FROM_KEY0"))) {
                //effectuer les actions demander via le tag Lightroom
                makeActionFromKeyword();
                //*
            }
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_PURGE_ACTION_000"))) {
                //purger les action demander via les keywords
                removeLinkWithActionFromKeyword();
                //*
            }

            //initialization pour nouveau démarrage
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_DEL_00000000"))) {
                List<String> lstIdKey = new ArrayList<>();
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CR_000000000"))) {
                    lstIdKey = creationDesKeywordProjet();
                }
                purgeKeywordProjet(lstIdKey);
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
                    //Maintenance database lr
                    maintenanceDatabase();
                    //*
                }
            }

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RETAG_RED_000000"))) {
                reTAGlesColorTagARED();
            }
            //*

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_UNZIP_REP_PHOTO0"))) {
                    unzipAndExtractAllZip();
                }
            }

            //En Fonction De La Strategies De Rangement
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REJET_000000"))) {
                //En Fonction De La Strategies De Rangement
                rangerLesRejets();
                //*
            }
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
                //En Fonction De La Strategies De Rangement
                analFonctionRep = analyseFonctionellesDesRepertoires();
//                LOGGER.info(analFonctionRep.toDisplay());
                //*
            }
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RGP_NEW_00000000"))) {
                //regrouper le new
                regrouperLesNouvellesPhoto(progress);
                progress.setString("");
                //*
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_LST_RAPP_NEW_REP"))) {
                    //lister les possible photo oublier
                    List<GrpPhoto> grpPhotosRapprocher = listerLesRapprochermentAvecLesRepertoirePhoto();
                    if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_RAPP_NEW_REP")) && Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CR_000000000"))) {
                        miseEnPlaceDesTagDeRapprochement(grpPhotosRapprocher);
                    }
                    //*
                }
                //*
            }
            //*

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_PURGE_FOLDER_000"))) {
                //Nettoyage repertoires Local
                purgeDesRepertoireVide50Phototheque();
                //*
                //Nettoyage repertoires réseaux
                purgeDesRepertoireVide00NEW();
                //*
            }

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
                //Maintenance database lr
                maintenanceDatabase();
                //*
            }

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_EXEC_FONC_REP_00"))) {
                    //Popup Action sur les erreurs Fonctionelle des repertoires
                    analFonctionRep.action(ctx, dbLr);
                    //*
                }

                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CTRL_REP_000"))) {
                    tagretourValRepertoire(analFonctionRep.getListOfretourValRepertoire());
                }
            }
            if (isItTimeToSave() || Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_FORCE_SVG_000000"))) {
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_SVG_LRCONFIG_000"))) {
                    //Sauvegarde Lightroom sur Local
                    sauvegardeLightroomConfigSauve();
                    //*
                }

                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RSYNC_BIB_000000"))) {
                    //sauvegarde Vers Réseaux Pour Cloud
                    sauvegardeStudioPhoto2Reseaux();
                    //*
                }
            }

            endall();

            if (Boolean.TRUE) {
                throw new IllegalStateException("Stop Run");
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionLog(e, LOGGER);
        }

    }

    private static void tagretourValRepertoire(List<BlocRetourRepertoire> retourValRepertoire) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        final int[] nbTopRed = {0};
        HashMap<String, Integer> lstIdKey = new HashMap<>();
        retourValRepertoire.forEach(
                (blocRetourRep) -> {
                    final boolean[] miseared = {false};
                    List<EleChamp> eChamp = blocRetourRep.getListOfControleValRepertoire();
                    eChamp.forEach(
                            (champ) -> {
                                if (!champ.isRetourControle()) {

                                    champ.getCompTagRetour().forEach((TagRetour) -> {

//                                            System.out.println(blocRetourRep.getRepertoire() + " -- " + champ.getCompTagRetour());
                                        LOGGER.debug(blocRetourRep.getRepertoire() + " -- " + TagRetour);
                                        NumberFormat formatter = new DecimalFormat("0000");
                                        int nbDiscr = ThreadLocalRandom.current().nextInt(0, 9999);
                                        String number = formatter.format(nbDiscr);
//                                            String tag = champ.getCompTagRetour() + "_" + "(" + number +")"  ;

                                        String[] tags = (Context.TAG_REPSWEEP + "_" + TagRetour + " id:" + number + "").replace("[", "").replace("]", "").split("_");


                                        String cletrace = tags[1] + ":" + tags[2];
                                        try {
                                            for (int i = 0; i <= tags.length - 1 - 1; i++) {
                                                dbLr.sqlcreateKeyword(tags[i], tags[i + 1]);
                                            }

                                            dbLr.AddKeywordToRepNoVideo(blocRetourRep.getRepertoire(), tags[tags.length - 1], tags[tags.length - 1 - 1]);

                                            int ret = dbLr.topperRepertoireARed(blocRetourRep.getRepertoire());
                                            if (ret > 0) {
                                                LOGGER.debug("   Tag a RED ");
                                                if (!miseared[0]) {
                                                    nbTopRed[0]++;
                                                    miseared[0] = true;
                                                }
                                            }

                                            if (lstIdKey.containsKey(cletrace)) {
                                                lstIdKey.replace(cletrace, lstIdKey.get(cletrace) + 1);
                                            } else {
                                                lstIdKey.put(cletrace, 1);
                                            }

                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }

                                    });
                                }
                            }

                    );
                }
        );

        NumberFormat formatter = new DecimalFormat("00000");

        loggerInfo("Tag a RED " + String.format("%05d", nbTopRed[0]) + " - repertoire ", nbTopRed[0]);
        loggerInfo("Delta Tag a RED " + String.format("%+05d", nbTopRed[0] - unNbMisAREDRep) + " - repertoire ", nbTopRed[0] - unNbMisAREDRep);

        List<String> lstIdKeyByKey = new ArrayList<>(lstIdKey.keySet());
        Collections.sort(lstIdKeyByKey);
        lstIdKeyByKey.forEach((tempKey) -> {
            LOGGER.info(getStringLn(tempKey) + " = " + formatter.format(lstIdKey.get(tempKey)));
        });
    }

    private static void chargementParametre(Context ctx, String[] args) {
        for (int i = 0; i < args.length; i++) {
            String[] decomp = args[i].split(":");

            switch (decomp[0]) {
                case "thresholdNew":
                    ctx.getParamTriNew().setThresholdNew(Long.parseLong(decomp[1]));
                    break;
            }
            LOGGER.info("Parameter : " + i + " = " + args[i]);
        }
    }

    private static void closelightroom(String toClose) {
        try {
            Runtime.getRuntime().exec(toClose) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeLinkWithActionFromKeyword() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //action collection
        Map<String, String> listeAction = ctx.getActionVersRepertoire().getListeAction();
        //action GO
        listeAction.put(Context.TAG_ACTION_GO_RAPPROCHEMENT, "");

        for (String tag : listeAction.keySet()) {

            Map<String, Map<String, String>> filetoPurge = dbLr.getFileForGoTag(tag);
            int nb = 0;
            loggerInfo("purge link tag " + " - " + String.format("%05d", filetoPurge.size()) + " - " + tag, filetoPurge.size());
            for (String key : filetoPurge.keySet()) {
                nb += dbLr.removeKeywordImages(filetoPurge.get(key).get("kiIdLocal"));
            }
            loggerInfo("        - fait " + " - " + String.format("%05d", nb) + " - " + tag, nb);

        }

    }

    private static void purgeKeywordProjet(List<String> lstIdKey) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        int nbPurge = dbLr.purgeGroupeKeyword(Context.TAG_ORG, lstIdKey);
        loggerInfo("purge " + String.format("%05d", nbPurge) + " - keywords ", nbPurge);
    }

    private static List<String> creationDesKeywordProjet() throws SQLException {
        List<String> lstIdKey = new ArrayList<>();
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        String idKey = "";

        Map<String, String> dbLrSqlcreateKeyword = new HashMap<>();

        dbLrSqlcreateKeyword = dbLr.sqlcreateKeyword("", Context.TAG_ORG);
        lstIdKey.add(dbLrSqlcreateKeyword.get("keyWordIdlocal"));
        if (Boolean.valueOf(dbLrSqlcreateKeyword.get("NewkeyWordIdlocal"))) {
            LOGGER.info(getStringLn("Keyword " + Context.TAG_ORG) + " idlocal = " + lstIdKey.get(lstIdKey.size() - 1));
        }

        dbLrSqlcreateKeyword = dbLr.sqlcreateKeyword(Context.TAG_ORG, Context.TAG_RAPPROCHEMENT);
        lstIdKey.add(dbLrSqlcreateKeyword.get("keyWordIdlocal"));
        if (Boolean.valueOf(dbLrSqlcreateKeyword.get("NewkeyWordIdlocal"))) {
            LOGGER.info(getStringLn("Keyword " + Context.TAG_ORG) + " idlocal = " + lstIdKey.get(lstIdKey.size() - 1));
        }

        dbLrSqlcreateKeyword = dbLr.sqlcreateKeyword(Context.TAG_ORG, Context.TAG_REPSWEEP);
        lstIdKey.add(dbLrSqlcreateKeyword.get("keyWordIdlocal"));
        if (Boolean.valueOf(dbLrSqlcreateKeyword.get("NewkeyWordIdlocal"))) {
            LOGGER.info(getStringLn("Keyword " + Context.TAG_ORG) + " idlocal = " + lstIdKey.get(lstIdKey.size() - 1));
        }

        dbLrSqlcreateKeyword = dbLr.sqlcreateKeyword(Context.TAG_RAPPROCHEMENT, Context.TAG_ACTION_GO_RAPPROCHEMENT);
        lstIdKey.add(dbLrSqlcreateKeyword.get("keyWordIdlocal"));
        if (Boolean.valueOf(dbLrSqlcreateKeyword.get("NewkeyWordIdlocal"))) {
            LOGGER.info(getStringLn("Keyword " + Context.TAG_ACTION_GO_RAPPROCHEMENT) + " idlocal = " + lstIdKey.get(lstIdKey.size() - 1));
        }

        for (String key : ctx.getActionVersRepertoire().getListeAction().keySet()) {

            dbLrSqlcreateKeyword = dbLr.sqlcreateKeyword(Context.TAG_ORG, key);
            lstIdKey.add(dbLrSqlcreateKeyword.get("keyWordIdlocal"));
            if (Boolean.valueOf(dbLrSqlcreateKeyword.get("NewkeyWordIdlocal"))) {
                LOGGER.info(getStringLn("Keyword " + key) + " idlocal = " + lstIdKey.get(lstIdKey.size() - 1));
            }

        }

        return lstIdKey;
    }

    private static String getStringLn(String s) {
        return String.format("%-60s", s);
    }

    private static void reTAGlesColorTagARED() throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        int unNbMisARED = dbLr.deTopperARedOld50NEW(ctx.getParamTriNew().getRepertoire50NEW());
        loggerInfo("UnTag a RED " + String.format("%05d", unNbMisARED) + " - images ", unNbMisARED);

        dbLr.MiseAzeroDesColorLabels("rouge");
        int nbMisARED = dbLr.topperARed50NEW(ctx.getParamTriNew().getRepertoire50NEW());
        loggerInfo("Tag a RED " + String.format("%05d", nbMisARED) + " - images ", nbMisARED);

        unNbMisAREDRep = dbLr.deTopperARedOldRepertoire();
        loggerInfo("UnTag a RED " + String.format("%05d", unNbMisAREDRep) + " - repertoire ", unNbMisAREDRep);

    }

    private static void displayBooleen() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String txt = "\n";
        if (Boolean.TRUE.equals(ctx.workflow.IS_DRY_RUN)) {
            txt += "" + "\n";
            txt += "   --DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN--   " + "\n";
            txt += "" + "\n";

        }
        txt += "   -------------------------------ACTION PREVU--------------------------------------   " + "\n";
        txt += "   -                                                                               -   " + "\n";
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
            txt += "   -  " + "maintenanceDatabase()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_ACTION_FROM_KEY0"))) {
            txt += "   -  " + "makeActionFromKeyword()" + "\n";
            txt += "   -  " + "removeLinkWithActionFromKeyword()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_DEL_00000000"))) {
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CR_000000000"))) {
                txt += "   -  - " + "creationDesKeywordProjet()" + "\n";
            }
            txt += "   -  " + "purgeKeywordProjet()" + "\n";
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
                txt += "   -  " + "maintenanceDatabase()" + "\n";
            }
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RETAG_RED_000000"))) {
            txt += "   -  " + "reTAGlesColorTagARED()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_UNZIP_REP_PHOTO0"))) {
                txt += "   -  - " + "UnzipAndExtractAllZip()" + "\n";
            }
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REJET_000000"))) {
            txt += "   -  " + "rangerLesRejets()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
            txt += "   -  " + "analyseFonctionellesDesRepertoires()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RGP_NEW_00000000"))) {
            txt += "   -  " + "regrouperLesNouvellesPhoto()" + "\n";
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_LST_RAPP_NEW_REP"))) {
                txt += "   -  - " + "listerLesRapprochermentAvecLesRepertoirePhoto()" + "\n";
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_RAPP_NEW_REP")) && Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CR_000000000"))) {
                    txt += "   -  -  - " + "miseEnPlaceDesTagDeRapprochement()" + "\n";
                }
            }
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_PURGE_FOLDER_000"))) {
            txt += "   -  " + "isItTimeToSave()" + "\n";
            txt += "   -  - " + "purgeDesRepertoireVide50Phototheque()" + "\n";
            txt += "   -  - " + "purgeDesRepertoireVide00NEW()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
            txt += "   -  " + "maintenanceDatabase()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_EXEC_FONC_REP_00"))) {
                txt += "   -  - " + "analFonctionRep.action()" + "\n";
            }
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CTRL_REP_000"))) {
                txt += "   -  - " + "tagretourValRepertoire(analFonctionRep.getListOfretourValRepertoire())" + "\n";
            }
        }


        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_SVG_LRCONFIG_000"))) {
            txt += "   -  " + "sauvegardeLightroomConfigSauve()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RSYNC_BIB_000000"))) {
            txt += "   -  " + "sauvegardeStudioPhoto2Reseaux()" + "\n";
        }

        txt += "   -                                                                               -   " + "\n";
        txt += "   ---------------------------------------------------------------------------------   " + "\n";
        LOGGER.info(txt);

    }

    private static void endall() throws InterruptedException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        if (frame != null) {
            TimeUnit.SECONDS.sleep(20);
            frame.dispose();
        }
    }

    private static void makeActionFromKeyword() throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Action GO
        Map<String, Map<String, String>> fileToGo = dbLr.getFileForGoTag(Context.TAG_ACTION_GO_RAPPROCHEMENT);
        loggerInfo("nb de fichier tagger : " + Context.TAG_ACTION_GO_RAPPROCHEMENT + " => " + String.format("%05d", fileToGo.size()) + "", fileToGo.size());
        int nb = 0;
        for (String key : fileToGo.keySet()) {
            Map<String, String> forGoTag = dbLr.getNewPathForGoTagandFileIdlocal(Context.TAG_ORG, key);
            if (forGoTag.size() > 0) {
                nb++;
                String source = fileToGo.get(key).get("oldPath");
                String newPath = forGoTag.get("newPath");
                LOGGER.debug("---move " + Context.TAG_ACTION_GO_RAPPROCHEMENT + " : " + source + " -> " + newPath);
                SystemFiles.moveFile(source, newPath);
                dbLr.sqlmovefile(key, newPath);
                dbLr.removeKeywordImages(forGoTag.get("kiIdLocal"));
            }
        }
        loggerInfo("move " + String.format("%05d", nb) + " - " + Context.TAG_ACTION_GO_RAPPROCHEMENT, nb);

        //action collection
        Map<String, String> listeAction = ctx.getActionVersRepertoire().getListeAction();
        for (String key : listeAction.keySet()) {
            Map<String, Map<String, String>> fileToTag = dbLr.sqllistAllFileWithTagtoRep(key, ctx.getRepertoire50Phototheque() + listeAction.get(key));
            loggerInfo("move " + String.format("%05d", fileToTag.size()) + " - " + key, fileToTag.size());
            for (String keyt : fileToTag.keySet()) {
                String oldPath = fileToTag.get(keyt).get("oldPath");
                String newPath = fileToTag.get(keyt).get("newPath");
                LOGGER.debug("---move " + key + " : " + oldPath + " -> " + newPath);
                SystemFiles.moveFile(oldPath, newPath);
                dbLr.sqlmovefile(keyt, newPath);
                dbLr.removeKeywordImages(fileToTag.get(keyt).get("kiIdLocal"));
            }
        }
    }

    private static void loggerInfo(String texte, int size) {
        if (size != 0) {
            LOGGER.info(texte);
        } else {
            LOGGER.debug(texte);
        }
    }

    private static void createLoggingPanel() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        // Create logging panel
        JTextArea jLoggingConsole = new JTextArea(5, 0); // 5 lines high here
        jLoggingConsole.setLineWrap(true);
        jLoggingConsole.setWrapStyleWord(true);
        jLoggingConsole.setEditable(false);
        jLoggingConsole.setFont(new Font("monospaced", Font.PLAIN, 12));

        // Make scrollable console pane
        JScrollPane jConsoleScroll = new JScrollPane(jLoggingConsole);
        jConsoleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Subscribe the text area to JTextAreaAppender
        JTextAreaAppender.addLog4j2TextAreaAppender(jLoggingConsole);

        //------------------------

        // now add the scrollpane to the jframe's content pane, specifically
        // placing it in the center of the jframe's borderlayout
        frame = new JFrame("PhotoOrganize");

        progress = new JProgressBar();
        progress.setValue(0);
        progress.setStringPainted(true);
        progress.setSize(500,500);

        frame.getContentPane().add(progress, BorderLayout.SOUTH);
        frame.getContentPane().add(jConsoleScroll, BorderLayout.CENTER);

        // make it easy to close the application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set the frame size (you'll usually want to call frame.pack())
        frame.setSize(new Dimension(1240, 600));

        // center the frame
        frame.setLocationRelativeTo(null);

        // make it visible to the user
        frame.setVisible(true);
    }

    private static void maintenanceDatabase() throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        splitLOGGERInfo(isMoreZeroComm(dbLr.pathAbsentPhysique(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.AdobeImagesWithoutLibraryFile(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.KeywordImageWithoutImages(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.folderWithoutRoot(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.folderAbsentPhysique(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.fileWithoutFolder(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.keywordImageWithoutKeyword(progress)));
        progress.setString("");
    }

    private static String isMoreZeroComm(String commentaireAAnalyser) {
        if (commentaireAAnalyser.contains("--- corrige         = 0")) {
            return "";
        }
        return commentaireAAnalyser;
    }

    private static void splitLOGGERInfo(String txt) {
        if (txt.length() > 0) {
            String[] atxt = txt.split("\n");
            for (String s : atxt) {
                if (s.contains("(debug) ")) {
                    LOGGER.debug(s);
                } else {
                    LOGGER.info(s);
                }
            }
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
                .info("PROGRESS2,STATS2")
                .outputCommandline(true)
                .recursive(true)
                .exclude(ctx.getRepFonctionnel().getRsyncexclude())
                .dryRun(ctx.workflow.IS_DRY_RUN)
                .stats(true)
                .progress(true)
                .itemizeChanges(true)
                .humanReadable(true)
                .delete(true)
                .verbose(true);

        progress.setMaximum(78286);

        Output output1 = new Output(new String[]{"total size is","bytes  received","Number of","deleting"},new String[]{"%"},progress);
        StreamingProcessOutput output = new StreamingProcessOutput(output1);
        output.monitor(rsync.builder());

        writeMouchard(output1);
    }

    private static void writeMouchard(Output output) throws IOException {
        File syncMouchard = new File(ctx.getRepFonctionnel().getRepertoiresyncdest() + ctx.getRepFonctionnel().getSyncdestmouchard());

        Calendar calt = Calendar.getInstance();
        calt.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String txt = " " + sdf.format(calt.getTimeInMillis()) + "\n" +
                Arrays.toString(ctx.getRepFonctionnel().getRepertoiresyncsource()) + "\n" +
                " to " + ctx.getRepFonctionnel().getRepertoiresyncdest() + "\n" +
                " -> " + "\n" +
                output.resumer + "\n" +
                " ---------------------------------------  \n";
        LOGGER.debug(txt);

        if (!ctx.workflow.IS_DRY_RUN) {
            if (!syncMouchard.exists()) {
                syncMouchard.createNewFile();
            }
            Files.write(syncMouchard.toPath(), txt.getBytes(), StandardOpenOption.APPEND);
        }
    }

    private static boolean isItTimeToSave() {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        File syncMouchard = new File(ctx.getRepFonctionnel().getRepertoiresyncdest() + ctx.getRepFonctionnel().getSyncdestmouchard());
        Calendar cal = Calendar.getInstance();
        long lastModified = syncMouchard.lastModified();
        cal.setTime(new Date(lastModified));
        cal.add(Calendar.DATE, Integer.parseInt(ctx.getRepFonctionnel().getSyncAmountDaysBetween()));

        Calendar calt = Calendar.getInstance();
        calt.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOGGER.info("Today                                   : " + sdf.format(calt.getTimeInMillis()));
        LOGGER.info("Dernier Modification du fichier mouchard: " + sdf.format(lastModified));
        LOGGER.info("Prochain TmeToSave                      : " + sdf.format(cal.getTimeInMillis()));

        boolean isItTimeToSave = calt.compareTo(cal) > 0;
        LOGGER.info("isItTimeToSave                          : " + isItTimeToSave);
        return isItTimeToSave;
    }

    private static void regrouperLesNouvellesPhoto(JProgressBar progress) throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Regroupement
        String repertoire50NEW = ctx.getParamTriNew().getRepertoire50NEW();
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), repertoire50NEW);

        GrpPhoto listFileBazar = new GrpPhoto();
        GrpPhoto listElekidz = new GrpPhoto();
        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();

        int numRow = 0;
        String txtPr =  dbLr.retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = dbLr.getQueryRowCount(dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), repertoire50NEW));

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

            dbLr.visuProgress(progress,txtPr,numRow++,numRowMax);

        }
        if (listEletmp.size() > ctx.getParamTriNew().getThresholdNew()) {
            listGrpEletmp.add(listEletmp);
        } else {
            listFileBazar.addAll(listEletmp);
        }

        deplacementDesGroupes(listFileBazar, listElekidz, listGrpEletmp);


    }


    private static List<GrpPhoto> listerLesRapprochermentAvecLesRepertoirePhoto() throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Regroupement
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), "");

        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();

        long maxprev = 0;
        long captureTimeprev = Long.MIN_VALUE;
        while (rsele.next()) {

            // Recuperer les info de l'elements
            String fileIdLocal = rsele.getString("file_id_local");
            String absolutePath = rsele.getString("absolutePath");
            String pathFromRoot = rsele.getString("pathFromRoot");
            String lcIdxFilename = rsele.getString("lc_idx_filename");
            long mint = rsele.getLong("mint");
            long maxt = rsele.getLong("maxt");
            long captureTime = rsele.getLong("captureTime");
            if (rsele.wasNull()) {
                captureTime = Long.MIN_VALUE; // set it to empty string as you desire.
            }

            // recherche du repPhoto concerner
            String ch = absolutePath + pathFromRoot;
            List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();
            int numeroRep = -1;
            int i;
            for (i = 0; i < arrayRepertoirePhoto.size(); i++) {
                if (arrayRepertoirePhoto.get(i).isRapprochermentNewOk() && ch.startsWith(SystemFiles.normalizePath(ctx.getRepertoire50Phototheque() + arrayRepertoirePhoto.get(i).getRepertoire()))) {
                    numeroRep = i;
                }
            }
            if (ch.startsWith(SystemFiles.normalizePath(ctx.getParamTriNew().getRepertoire50NEW()))) {
                numeroRep = Context.IREP_NEW;
            }


            if (numeroRep > -1) {
                ElementFichier eleFile = new ElementFichier(absolutePath, pathFromRoot, lcIdxFilename, fileIdLocal, numeroRep);

                eleFile.setcaptureTime(captureTime);
                eleFile.setmint(mint);
                eleFile.setmaxt(maxt);

                if (captureTime < captureTimeprev) {
                    throw new IllegalStateException("captureTime not in order = " + captureTime + " < " + captureTimeprev);
                }

                if (mint > maxprev) {
                    if (listEletmp.size() > 1 && listEletmp.getArrayRep(Context.IREP_NEW) > 0 && listEletmp.size() > listEletmp.getArrayRep(Context.IREP_NEW)) {
                        listGrpEletmp.add(listEletmp);
                    }

                    listEletmp = new GrpPhoto();

                }
                maxprev = maxt;
                captureTimeprev = captureTime;
                listEletmp.setFirstDate(captureTime);
                listEletmp.add(eleFile);
            }

        }
        if (listEletmp.size() > 0) {
            listGrpEletmp.add(listEletmp);
        }


        //display des rapprochement
        for (GrpPhoto listEle : listGrpEletmp) {
            if (listEle.size() > 1 && listEle.getArrayRep(Context.IREP_NEW) > 0 && listEle.size() > listEle.getArrayRep(Context.IREP_NEW)) {
                LOGGER.info("---------------------------");
                int nbele = listEle.lstEleFile.size();
                LOGGER.info(String.format("%05d", nbele) + " ===========");
                int i;
                for (i = 0; i < ctx.getArrayRepertoirePhoto().size(); i++) {
                    if (listEle.getArrayRep(i) > 0) {

                        int ii;
                        for (ii = 0; ii < listEle.getLstEleFile().size(); ii++) {
                            LOGGER.debug(listEle.getLstEleFile().get(ii).toString());
                        }
                        LOGGER.info(String.format("%05d", listEle.getArrayRep(i)) + " - " + ctx.getArrayRepertoirePhoto().get(i).getRepertoire());
                    }
                }
                LOGGER.info(String.format("%05d", listEle.getArrayRep(Context.IREP_NEW)) + " - " + ctx.getParamTriNew().getRepertoire50NEW());
            }
        }

        return listGrpEletmp;
    }

    private static void miseEnPlaceDesTagDeRapprochement(List<GrpPhoto> listGrpEletmp) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        for (GrpPhoto listEle : listGrpEletmp) {
            if (listEle.size() > 1 && listEle.getArrayRep(Context.IREP_NEW) > 0 && listEle.size() > listEle.getArrayRep(Context.IREP_NEW)) {
                Context.nbDiscretionnaire++;
                String nbDiscr = String.format("%1$03X", Context.nbDiscretionnaire);
                String tag = Context.TAG_RAPPROCHEMENT + "_" + nbDiscr + "_" + Context.POSSIBLE_NEW_GROUP;
                LOGGER.info("tag : " + tag + " ==> ");
                for (ElementFichier eleFile : listEle.lstEleFile) {
                    dbLr.AddKeywordToFile(eleFile.getFileIdLocal(), tag, Context.TAG_RAPPROCHEMENT);
                    LOGGER.debug(" --- " + eleFile.getPathFromRoot() + File.separator + eleFile.getLcIdxFilename());
                }
                //display info
                int i;
                for (i = 0; i < ctx.getArrayRepertoirePhoto().size(); i++) {
                    if (listEle.getArrayRep(i) > 0) {
                        LOGGER.info("    " + String.format("%05d", listEle.getArrayRep(i)) + " - " + ctx.getArrayRepertoirePhoto().get(i).getRepertoire());
                    }
                }
                LOGGER.info("    " + String.format("%05d", listEle.getArrayRep(Context.IREP_NEW)) + " - " + ctx.getParamTriNew().getRepertoire50NEW());


            }
        }
    }

    private static void deplacementDesGroupes(GrpPhoto listFileBazar, GrpPhoto listElekidz, List<GrpPhoto> listGrpEletmp) throws IOException, SQLException {
        //deplacement des group d'elements New Trier
        if (listGrpEletmp.size() > 0) {
            int nbtot = 0;
            for (GrpPhoto listEle : listGrpEletmp) {
                nbtot += listEle.lstEleFile.size();
                SimpleDateFormat repDateFormat = new SimpleDateFormat(TriNew.FORMATDATE_YYYY_MM_DD);
                String nomRep = repDateFormat.format(new Date(listEle.getFirstDate() * 1000)) + "_(" + String.format("%05d", listEle.lstEleFile.size()) + ")";
                for (ElementFichier eleGrp : listEle.lstEleFile) {

                    String newName = ctx.getParamTriNew().getRepertoire50NEW() + nomRep + File.separator + eleGrp.getLcIdxFilename();
                    WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);

                }
            }
            LOGGER.info("Repertoire New - Nb groupe => " + String.format("%05d", listGrpEletmp.size()) + " pour un total de " + String.format("%05d", nbtot) + " elements ");
        }

        if (listElekidz.lstEleFile.size() > 0) {
            //deplacement des group d'elements Kidz
            LOGGER.info("Repertoire New - vers Kidz : " + String.format("%05d", listElekidz.lstEleFile.size()));
            for (ElementFichier eleGrp : listElekidz.lstEleFile) {
                String newName = ctx.getParamTriNew().getRepertoireKidz() + File.separator + eleGrp.getLcIdxFilename();
                WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
            }
        }

        if (listFileBazar.lstEleFile.size() > 0) {
            //deplacement des group d'elements Bazar
            LOGGER.info("Repertoire New - nb Bazar  : " + String.format("%05d", listFileBazar.lstEleFile.size()));
            for (ElementFichier eleGrp : listFileBazar.lstEleFile) {
                String newName = ctx.getParamTriNew().getRepertoireBazar() + File.separator + eleGrp.getLcIdxFilename();
                WorkWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
            }
        }
    }

    private static void unzipAndExtractAllZip() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();

        ListIterator<RepertoirePhoto> repertoirePhotoIterator = arrayRepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            RepertoirePhoto repPhoto = repertoirePhotoIterator.next();
            LOGGER.debug("UnzipAndExtractAllZip = > " + ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire());
            List<String> listRep = WorkWithRepertory.listRepertoireEligible(ctx.getRepertoire50Phototheque(), repPhoto);

            int i = 0;

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {

                visuProgress(progress,repPhoto.repertoire,i++,listRep.size());

                String repertoire = repertoireIterator.next();
                findZipAndExtractToRejet(repertoire);
            }

        }
    }

    private static AnalyseGlobalRepertoires analyseFonctionellesDesRepertoires() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        AnalyseGlobalRepertoires analyseRepertoires = new AnalyseGlobalRepertoires();

        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();

        ListIterator<RepertoirePhoto> repertoirePhotoIterator = arrayRepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            RepertoirePhoto repPhoto = repertoirePhotoIterator.next();


            LOGGER.debug("repertoire = > " + ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire());
            List<String> listRep = WorkWithRepertory.listRepertoireEligible(ctx.getRepertoire50Phototheque(), repPhoto);

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {
                String repertoire = repertoireIterator.next();

//                BlocRetourRepertoire retourRepertoire = AnalyseGlobalRepertoires.calculateLesEleChampsDuRepertoire(repertoire, repPhoto, ctx.getParamControleRepertoire());

                analyseRepertoires.add(new BlocRetourRepertoire(repertoire, repPhoto, ctx.getParamControleRepertoire(), ctx, dbLr));

//                File f = new File(repertoire + Context.FOLDERDELIM + "photoOrganizeAnalyse.json");
//                if (!retourRepertoire.isRepertoireValide()) {
//                    Serialize.writeJSON(retourRepertoire, f);
//                    LOGGER.debug(repertoire + "=>" + "ko" + " => " + " ecriture fichier ->" + f.toString());
//                } else {
//                    f.delete();
//                }

            }

//            File f = new File(ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire() + Context.FOLDERDELIM + new File(repPhoto.getRepertoire()).getName() + ".svg.json");
//            Serialize.writeJSON(repPhoto, f);
//            LOGGER.debug("ecriture fichier ->" + f.toString());

        }

        LOGGER.info(analyseRepertoires.toString());
        return analyseRepertoires;
    }

    private static void findZipAndExtractToRejet(String repertoire) throws IOException {

        List<File> arrayFichierZip = WorkWithFiles.getFilesFromRepertoryWithFilter(repertoire, "zip");

        ListIterator<File> arrayFichierRejetIterator = arrayFichierZip.listIterator();
        while (arrayFichierRejetIterator.hasNext()) {
            File fichier = arrayFichierRejetIterator.next();

            LOGGER.info("unzip :" + fichier);
            WorkWithFiles.extractZipFile(fichier);

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
        File repertoireRoamingAdobeLightroom = new File(ctx.getRepertoireRoamingAdobeLightroom());
        new ZipFile(repertoireDestZip).addFolder(repertoireRoamingAdobeLightroom);
    }

    private static void purgeDesRepertoireVide50Phototheque() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getRepertoire50Phototheque();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation, Main.progress);
        } while (!isFinished);
        Main.progress.setString("");
    }

    private static void purgeDesRepertoireVide00NEW() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getRepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(folderlocation, progress);
        } while (!isFinished);
        progress.setString("");
    }


    private static void rangerLesRejets() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<File> arrayFichierRejet = WorkWithFiles.getFilesFromRepertoryWithFilter(ctx.getRepertoire50Phototheque(), ctx.getArrayNomSubdirectoryRejet(), ctx.getParamElementsRejet().getExtFileRejet());

        List<String> arrayFichierRejetStr = new ArrayList<>();
        for (File FichierRejet : arrayFichierRejet) {
            arrayFichierRejetStr.add(FichierRejet.toString());
        }

        for (int y = 0; y < arrayFichierRejetStr.size(); y++) {
            if (arrayFichierRejetStr.get(y).toLowerCase(Locale.ROOT).contains((ctx.getRepertoire50Phototheque() + "99-rejet").toLowerCase(Locale.ROOT))) {
                arrayFichierRejetStr.remove(y);
                y--;
            }
        }

        //todo add all photo rejected to arrayFichierRejetStr
        arrayFichierRejetStr.addAll(dbLr.getlistPhotoFlagRejeter());

        Map<String, Integer> countExt = new HashMap<>();
        Map<String, Integer> countExtDo = new HashMap<>();
        Map<String, Integer> countExtDel = new HashMap<>();
        LOGGER.debug("arrayFichierRejet     => " + String.format("%05d", arrayFichierRejetStr.size()));

        for (int y = 0; y < arrayFichierRejetStr.size(); y++) {
            String fichierStr = arrayFichierRejetStr.get(y);
            String fileExt = FilenameUtils.getExtension(fichierStr).toLowerCase();
            String filepath = FilenameUtils.getFullPath(fichierStr).toLowerCase();

            if (countExt.containsKey(fileExt)) {
                countExt.replace(fileExt, countExt.get(fileExt), countExt.get(fileExt) + 1);
            } else {
                countExt.put(fileExt, 1);
                countExtDo.put(fileExt, 0);
                countExtDel.put(fileExt, 0);
            }

            if (fileExt.toLowerCase().compareTo("zip") == 0) {
                LOGGER.info("unzip :" + fichierStr);
                WorkWithFiles.extractZipFile(new File(fichierStr));
            }

            if (ctx.getParamElementsRejet().getArrayNomFileRejet().contains(fileExt.toLowerCase()) ||
                    (
                            ctx.getParamElementsRejet().getExtFileRejet().compareTo(fileExt.toLowerCase()) == 0
                                    && !ctx.getParamElementsRejet().getArrayNomSubdirectoryRejet().stream().anyMatch(filepath::contains)
                    )
            ) {
                //rename to rejet dans meme repertoire
                String oldName = fichierStr;
                String newName = addRejetSubRepToPath(oldName) + "." + ctx.getParamElementsRejet().getExtFileRejet();
                File fsource = new File(oldName);
                File fdest = new File(newName);
                if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                    countExtDo.replace(fileExt, countExtDo.get(fileExt), countExtDo.get(fileExt) + 1);
                    WorkWithFiles.renameFile(oldName, newName, dbLr);
                }
            } else {
                if (ctx.getParamElementsRejet().getArrayNomFileRejetSup().contains(fileExt.toLowerCase())) {
                    //move to 99-rejet
                    int i;
                    List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();
                    for (i = 0; i < arrayRepertoirePhoto.size(); i++) {
                        RepertoirePhoto repertoirePhoto = arrayRepertoirePhoto.get(i);
                        if (repertoirePhoto.getRepertoire().toLowerCase(Locale.ROOT).contains("99-rejet")) {

                            String oldName = fichierStr;
                            File fsource = new File(oldName);

                            File fdest = new File(ctx.getRepertoire50Phototheque() + repertoirePhoto.getRepertoire() + Context.FOLDERDELIM + oldName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
                            String newName = fdest.toString();

                            if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                                countExtDel.replace(fileExt, countExtDel.get(fileExt), countExtDel.get(fileExt) + 1);
                                WorkWithFiles.renameFile(oldName, newName, dbLr);
                            } else {
                                LOGGER.debug("oldName " + oldName + " move impossile to newName : " + newName);
                                LOGGER.debug("fsource.exists() " + fsource.exists());
                                LOGGER.debug("fsource.isFile()  " + fsource.isFile());
                                LOGGER.debug("!fdest.exists() " + !fdest.exists());
                            }

                        }
                    }
                }
            }
        }

        Iterator it = countExt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info("arrayFichierRejet." + pair.getKey() + " => " + String.format("%05d", pair.getValue()) + " => do : " + String.format("%05d", countExtDo.get(pair.getKey())) + " => del : " + String.format("%05d", countExtDel.get(pair.getKey())));
        }


    }

    private static String addRejetSubRepToPath(String fichierStr) throws SQLException {
        if (fichierStr.toLowerCase(Locale.ROOT).contains("\\rejet\\")) {
            return fichierStr;
        } else {
            String rejetPath = FilenameUtils.concat(FilenameUtils.getFullPath(fichierStr), "rejet");
            return FilenameUtils.concat(rejetPath, FilenameUtils.getName(fichierStr));
        }
    }

    public static void exceptionLog(Exception theException, Logger loggerOrigine) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        theException.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        loggerOrigine.fatal("theException = " + "\n" + stringWriter);
    }

    public static void visuProgress(JProgressBar progress, String txtPr, int numRow, int numRowMax) {
        progress.setMaximum(numRowMax);
        progress.setValue(numRow);
        progress.setString(txtPr + " - " + new DecimalFormat("#.##").format(numRow*100/numRowMax) + "%");
    }
}
