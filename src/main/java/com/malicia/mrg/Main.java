package com.malicia.mrg;

import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.malicia.mrg.app.WorkWithFiles;
import com.malicia.mrg.app.WorkWithRepertory;
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

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String colorTagNew = Context.RED;
    public static final String colorTagWhatsApp = Context.GREEN;
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Logger LOGGER_TO_TAG_RAP = LogManager.getLogger("loggerToTagRap");
    private static final Logger LOGGER_TO_SYNC_PH_FILE = LogManager.getLogger("loggerToSyncPhFile");
    private static final Logger LOGGER_TO_ANAL_REP = LogManager.getLogger("loggerToAnalRepDisplay");
    private static Context ctx;
    private static Database dbLr;
    private static JFrame frame;
    private static AnalyseGlobalRepertoires analFonctionRep;
    private static JProgressBar progress;
    private static int unNbMisAREDRep = 0;
    private static String repertoirerejet;

    public static void main(String[] args) {
        try {
            //intialize logging
            createLoggingPanel();
            chargeLog4j();
            //*

            LOGGER.info(InfoVersion.showVersionInfo());
            LOGGER_TO_TAG_RAP.info(InfoVersion.showVersionInfo());
            LOGGER_TO_SYNC_PH_FILE.info(InfoVersion.showVersionInfo());
            LOGGER_TO_ANAL_REP.info(InfoVersion.showVersionInfo());

            // chargement application
            ctx = Context.chargeParam();
            loadCustomContext();

            closelightroom(ctx.dataApplication.getApplicationToClose());
            dbLr = Database.chargeDatabaseLR(ctx.getCatalogLrcat(), ctx.workflow.IS_DRY_RUN);

            SystemFiles.setIsDryRun(ctx.workflow.IS_DRY_RUN);
            ctx.getActionVersRepertoire().populate(dbLr.getFolderCollection(Context.COLLECTIONS, Context.TAG_ORG, ""));
            //*

            // chargement parameter
            chargementParametre(ctx, args);
            //*

            displayBooleen();

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_SYNCR_LR_0000000"))) {
                //Synchro fichier physique avec database lr
                synchroDatabase();
                //*
            }

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
                tagRedOnlyRep00New(colorTagNew);
            }
            //*

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RETAG_GREEN_0000"))) {
                tagGreenOnlyRep00NewWhatsApp(colorTagWhatsApp);
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

            //************************************************************
            //*     analyse                                              *
            //*                                                          *

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
                //En Fonction De La Strategies De Rangement
                analFonctionRep = analyseFonctionellesDesRepertoires();
                LOGGER_TO_ANAL_REP.info(analFonctionRep.toDisplay());
                //*
            }

            //*                                                          *
            //************************************************************

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
                //Nettoyage repertoires phototheque
                purgeDesRepertoireVide50Phototheque();
                //*
                //Nettoyage repertoires a trier
                purgeDesRepertoireVide50New();
                //*
                //Nettoyage repertoires check in
                purgeDesRepertoireVide00NEW();
                //*
            }

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
                //Maintenance database lr
                maintenanceDatabase();
                //*
            }

            //************************************************************
            //*     tagging                                              *
            //*                                                          *

            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_WRK_REP_PHOTO_00"))) {
                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_EXEC_FONC_REP_00"))) {
                    //Popup Action sur les erreurs Fonctionelle des repertoires
                    analFonctionRep.action(ctx, dbLr);
                    //*
                }

                if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_TAG_CTRL_REP_000"))) {
                    tagretourValRepertoire(analFonctionRep.getListOfretourValRepertoire(), colorTagNew);
                }
            }

            //*                                                          *
            //************************************************************

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

    private static void loadCustomContext() throws IllegalStateException {
        int i;
        for (i = 0; i < ctx.arrayRepertoirePhoto.size(); i++) {
            if (ctx.arrayRepertoirePhoto.get(i).getNomunique().contains("Rejet")) {
                repertoirerejet =ctx.arrayRepertoirePhoto.get(i).getRepertoire();
            }
        }
        if (repertoirerejet.length()<3){
            throw new IllegalStateException("repertoirerejet = '" + repertoirerejet + "' : length < 3 ");
        }
    }

    private static void tagretourValRepertoire(List<BlocRetourRepertoire> retourValRepertoire, String color) {
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

                                        String[] tags = (Context.TAG_REPSWEEP + "_" + TagRetour +  "..." + "("+ number +")" ).replace("[", "").replace("]", "").split("_");


                                        String cletrace = tags[1] + ":" + tags[2];
                                        try {
                                            for (int i = 0; i <= tags.length - 1 - 1; i++) {
                                                dbLr.sqlcreateKeyword(tags[i], tags[i + 1]);
                                            }

                                            dbLr.AddKeywordToRepNoVideo(blocRetourRep.getRepertoire(), tags[tags.length - 1], tags[tags.length - 1 - 1]);

                                            int ret = dbLr.topperRepertoireARed(blocRetourRep.getRepertoire(), color);
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
            Runtime.getRuntime().exec(toClose);
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

        splitLOGGERInfo(isMoreZeroComm(dbLr.KeywordImageWithoutImages(progress)));

        splitLOGGERInfo(isMoreZeroComm(dbLr.keywordImageWithoutKeyword(progress)));

        progress.setString("");
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

    private static void tagRedOnlyRep00New(String color) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        int unNbMisARED = dbLr.deTopperAColorOld50NEW(new String[]{ctx.getParamTriNew().getRepertoire50NEW(), ctx.getRepertoire00NEW()}, color);
        loggerInfo("UnTag a RED " + String.format("%05d", unNbMisARED) + " - images ", unNbMisARED);

        dbLr.MiseAzeroDesColorLabels("rouge");
        int nbMisARED = dbLr.topperARed50NEW(new String[]{ctx.getParamTriNew().getRepertoire50NEW(), ctx.getRepertoire00NEW()}, color);
        loggerInfo("Tag a RED " + String.format("%05d", nbMisARED) + " - images ", nbMisARED);

        unNbMisAREDRep = dbLr.deTopperARedOldRepertoire(color.toLowerCase(Locale.ROOT));
        loggerInfo("UnTag a RED " + String.format("%05d", unNbMisAREDRep) + " - repertoire ", unNbMisAREDRep);

    }

    private static void tagGreenOnlyRep00NewWhatsApp(String color) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        int unNbMisAGREEN = dbLr.deTopperAColorOld50NEW(new String[]{ctx.getParamTriNew().getRepertoire50NEW(), ctx.getRepertoire00NEW()}, color);
        loggerInfo("UnTag a GREEN " + String.format("%05d", unNbMisAGREEN) + " - images ", unNbMisAGREEN);

//        dbLr.MiseAzeroDesColorLabels("green");
        int nbMisAGREEN = dbLr.topperAGreen50NEWWhatsApp(new String[]{ctx.getParamTriNew().getRepertoire50NEW(), ctx.getRepertoire00NEW()}, color);
        loggerInfo("Tag a GREEN " + String.format("%05d", nbMisAGREEN) + " - images ", nbMisAGREEN);

//        unNbMisAGREENRep = dbLr.deTopperARedOldRepertoire(color.toLowerCase(Locale.ROOT));
//        loggerInfo("UnTag a GREEN " + String.format("%05d", unNbMisAGREENRep) + " - repertoire ", unNbMisAREDRep);

    }

    private static void displayBooleen() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String txt = "\n";
        if (Boolean.TRUE.equals(ctx.workflow.IS_DRY_RUN)) {
            txt += "\n";
            txt += "   --DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN----DRY-RUN--   " + "\n";
            txt += "\n";

        }
        txt += "   -------------------------------ACTION PREVU--------------------------------------   " + "\n";
        txt += "   -                                                                               -   " + "\n";
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_SYNCR_LR_0000000"))) {
            txt += "   -  " + "synchroDatabase()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_MAINT_LR_0000000"))) {
            txt += "   -  " + "maintenanceDatabase()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_ACTION_FROM_KEY0"))) {
            txt += "   -  " + "makeActionFromKeyword()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_PURGE_ACTION_000"))) {
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
            txt += "   -  " + "tagRedOnlyRep00New()" + "\n";
        }
        if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RETAG_GREEN_0000"))) {
            txt += "   -  " + "tagGreenOnlyRep00NewWhatsApp()" + "\n";
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

        if (isItTimeToSave() || Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_FORCE_SVG_000000"))) {
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_SVG_LRCONFIG_000"))) {
                txt += "   -  " + "sauvegardeLightroomConfigSauve()" + "\n";
            }
            if (Boolean.TRUE.equals(ctx.workflow.TODO.contains("IS_RSYNC_BIB_000000"))) {
                txt += "   -  " + "sauvegardeStudioPhoto2Reseaux()" + "\n";
            }
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
        loggerInfo("nb de fichier tagger : " + Context.TAG_ACTION_GO_RAPPROCHEMENT + " => " + String.format("%05d", fileToGo.size()), fileToGo.size());
        int nb = 0;
        for (String scrFileIdLocal : fileToGo.keySet()) {
            Map<String, String> forGoTag = dbLr.getNewPathForGoTagandFileIdlocal(Context.TAG_ORG, scrFileIdLocal);
            if (forGoTag.size() > 0) {
                nb++;
                String source = fileToGo.get(scrFileIdLocal).get("oldPath");
                String newPath = forGoTag.get("newPath");
                if (new File(source).exists() && !new File(newPath).exists()) {
                    LOGGER.debug("---move " + Context.TAG_ACTION_GO_RAPPROCHEMENT + " : " + source + " -> " + newPath);
                    Function.moveFile(source, newPath, dbLr);
                    dbLr.removeKeywordImages(forGoTag.get("kiIdLocal"));
                }
            }
        }
        loggerInfo("move " + String.format("%05d", nb) + " - " + Context.TAG_ACTION_GO_RAPPROCHEMENT, nb);

        //action collection
        Map<String, String> listeAction = ctx.getActionVersRepertoire().getListeAction();
        for (String key : listeAction.keySet()) {
            Map<String, Map<String, String>> fileToTag = dbLr.sqllistAllFileWithTagtoRep(key, listeAction.get(key));
            loggerInfo("move " + String.format("%05d", fileToTag.size()) + " - " + key, fileToTag.size());
            for (String scrFileIdLocal : fileToTag.keySet()) {
                String oldPath = fileToTag.get(scrFileIdLocal).get("oldPath");
                String newPath = fileToTag.get(scrFileIdLocal).get("newPath");
                if (new File(oldPath).exists() && !new File(newPath).exists()) {
                    LOGGER.debug("---move " + key + " : " + oldPath + " -> " + newPath);
                    Function.moveFile(oldPath, newPath, dbLr);
                    dbLr.removeKeywordImages(fileToTag.get(scrFileIdLocal).get("kiIdLocal"));
                }
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
        progress.setSize(500, 500);

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
        splitLOGGERInfo(isMoreZeroComm(dbLr.AdobeImagesWithoutLibraryFile(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.folderWithoutRoot(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.fileWithoutFolder(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.KeywordImageWithoutImages(progress)));
        splitLOGGERInfo(isMoreZeroComm(dbLr.keywordImageWithoutKeyword(progress)));
        progress.setString("");
    }

    private static void synchroDatabase() throws SQLException, IOException, ParseException, InterruptedException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER_TO_SYNC_PH_FILE);

        List<String> extensionsUseFile = new ArrayList<>() ;
        extensionsUseFile.addAll(ctx.getExtensionsUseFile());
        extensionsUseFile.addAll(ctx.getParamElementsRejet().getarrayExtensionFileRejetSup());

        List<String> nomSubdirectoryRejet = new ArrayList<>() ;
        nomSubdirectoryRejet.addAll(ctx.getArrayNomSubdirectoryRejet());
        nomSubdirectoryRejet.addAll(ctx.getParamElementsRejet().getArrayNomSubdirectoryRejet());
        nomSubdirectoryRejet.add(repertoirerejet);

        List<String> listFilesPh = WorkWithFiles.getAllPhysicalFiles(dbLr.getAllAbsolutePath(), extensionsUseFile, nomSubdirectoryRejet, progress);

        List<String> listFilesLog = dbLr.getAllFilesLogiques();

        analyseFilePhysiqueAndLogiques(listFilesPh, listFilesLog);

        progress.setString("");
    }

    private static void analyseFilePhysiqueAndLogiques(List<String> listFilesPh, List<String> listFilesLog) throws SQLException, IOException, ParseException, InterruptedException {
        List<String> listFilesNotInLog = new ArrayList<>();
        int koPhy = 0;
        int koLog = 0;

//        int posLog = 0;
        int nbLog = listFilesLog.size();

//        int posPhy = 0;
        int nbPhy = listFilesPh.size();

        LOGGER_TO_SYNC_PH_FILE.info("Differences between listFilesPh and listFilesLog:");

        Iterator<String> iterPh = listFilesPh.listIterator();
        Iterator<String> iterLo = listFilesLog.listIterator();

        String elemPh = iterPh.hasNext() ? iterPh.next() : null;
        String elemLo = iterLo.hasNext() ? iterLo.next() : null;

        while (elemPh != null || elemLo != null) {
            int compareResult;

            if (elemPh == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemLo == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemPh.compareTo(elemLo);
            }

            if (compareResult < 0) {
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesLog - " + elemPh + "");
                listFilesNotInLog.add(elemPh);
                koPhy++;
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
            } else if (compareResult > 0) {
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesPh  - " + elemLo + " ");
                koLog++;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            }
        }

        LOGGER.info(" nb path logique  = " + nbLog + " : absent logique in physique = " + koLog + "\n");
        LOGGER_TO_SYNC_PH_FILE.info(" nb path logique  = " + nbLog + " : absent logique in physique = " + koLog + "\n");
        LOGGER.info(" nb path physique = " + nbPhy + " : absent physique in logique = " + koPhy + "\n");
        LOGGER_TO_SYNC_PH_FILE.info(" nb path physique = " + nbPhy + " : absent physique in logique = " + koPhy + "\n");

        correctPhysique_lc_duplicate(listFilesPh, listFilesNotInLog);

        correctLogicalLowercase(listFilesLog, listFilesNotInLog);

        correctPhysiqueLostSidecar(listFilesNotInLog);

        correctPhysiqueAlreadyHash(listFilesNotInLog);

        correctPhysiqueStartDot(listFilesNotInLog);

        //todo
        // - gif 2 mp4
        // - bmp 2 jpeg
        // - mp3 2 mp4

        LOGGER.info("\n");
    }

    private static void correctLogicalLowercase(List<String> listFilesLog, List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //correct logique extension lowercase/uppercase
        //correct logique filename lowercase/uppercase
        List<List<String>> listLoIdxFilenameToUpdate = new ArrayList<>();

        Collections.sort(listFilesNotInLog);

        Iterator<String> iterLoRe = listFilesLog.listIterator();
        Iterator<String> iterPhNotLog = listFilesNotInLog.listIterator();

        String elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
        String elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;

        while (elemLoRe != null || elemPhNotLog != null) {
            int compareResult;

            if (elemLoRe == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemPhNotLog == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemLoRe.toLowerCase().compareTo(elemPhNotLog.toLowerCase());
            }

            if (compareResult < 0) {
                elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
            } else if (compareResult > 0) {
                elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                if (elemLoRe.compareTo(elemPhNotLog)!=0){
                    listLoIdxFilenameToUpdate.add(Arrays.asList(elemLoRe,elemPhNotLog));
                }
                elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
                elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;
            }
        }

        Iterator<List<String>> iterLoIdxUp = listLoIdxFilenameToUpdate.listIterator();
        List<String> elemLoIdxUp = iterLoIdxUp.hasNext() ? iterLoIdxUp.next() : null;
        int koLoExtUpdateDo = 0;
        while (elemLoIdxUp != null ) {
            int ret = Function.moveFile(elemLoIdxUp.get(0), elemLoIdxUp.get(1), dbLr);
            if (ret>0) {
                koLoExtUpdateDo = koLoExtUpdateDo + ret;
                listFilesNotInLog.remove(elemLoIdxUp.get(1));
            }
            elemLoIdxUp = iterLoIdxUp.hasNext() ? iterLoIdxUp.next() : null;
        }
        LOGGER.info("    --- corrige Correct logique U/L case         : toDo = " + listLoIdxFilenameToUpdate.size() + " , Done = " + koLoExtUpdateDo + "\n");
        //------------------------------------------------
    }

    private static void correctPhysiqueLostSidecar(List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //move physique lost sidecar to rejet
        List<String> listPhLostSidecarToRejet = new ArrayList<>();
        int koPhLostSidecarToRejetDo =0;
        for (String element : listFilesNotInLog) {
            for (String suffix : ctx.getParamElementsRejet().getarrayExtensionFileRejetSup()) {
                if (element.toLowerCase().endsWith(suffix.toLowerCase())) {
                    listPhLostSidecarToRejet.add(element);
                    break;  // If a match is found, break out of the inner loop
                }
            }
        }
        koPhLostSidecarToRejetDo = putThatInRejet(listPhLostSidecarToRejet);
        LOGGER.info("    --- corrige physique lost sidecar to rejet   : toDo = " + listPhLostSidecarToRejet.size() + " , Done = " + koPhLostSidecarToRejetDo + "\n");
        //------------------------------------------------
    }

    private static void correctPhysiqueAlreadyHash(List<String> listFilesNotInLog) throws SQLException, IOException, ParseException {
        //------------------------------------------------
        //move physique already hash in database to rejet
        int koPhHashExistToRejetDo =0;
        for (String element : listFilesNotInLog) {
                if (dbLr.getFileByHash(lrHashOf(element)).compareTo("")!=0) {
                    koPhHashExistToRejetDo = koPhHashExistToRejetDo + moveTo99Rejet(element);
                }
        }
        LOGGER.info("    --- corrige physique hash already exist      : -------------- Done = " + koPhHashExistToRejetDo + "\n");
    }

    private static void correctPhysiqueStartDot(List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //move physique file start with dot
        int koPhStartDotDo = 0;
        String baseNameNew = "";
        for (String element : listFilesNotInLog) {
            String baseName = FilenameUtils.getBaseName(element);
            if (baseName.startsWith(".")) {
                baseNameNew = baseName;
                while (baseNameNew.startsWith(".")) {
                    baseNameNew = baseNameNew.substring(1);
                }
                koPhStartDotDo = koPhStartDotDo + Function.moveFile(element, element.replace(baseName,baseNameNew), dbLr);
            }
        }
        LOGGER.info("    --- corrige physique start dot               : -------------- Done = " + koPhStartDotDo + "\n");
    }

    private static void correctPhysique_lc_duplicate(List<String> listFilesPh, List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //correct physique file in double in lowercase (db can handler only one unique folder+lc_idx_filename)
        List<String> listFilesToRename = new ArrayList<>();

        Collections.sort(listFilesNotInLog);

        Iterator<String> iterPhDup = listFilesPh.listIterator();
        Iterator<String> iterPhNotLo = listFilesNotInLog.listIterator();

        String elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
        String elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;

        while (elemPhDup != null || elemPhNotLo != null) {
            int compareResult;

            if (elemPhDup == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemPhNotLo == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemPhDup.toLowerCase().compareTo(elemPhNotLo.toLowerCase());
            }

            if (compareResult < 0) {
                elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
            } else if (compareResult > 0) {
                elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
                while (elemPhDup != null && elemPhDup.toLowerCase().compareTo(elemPhNotLo.toLowerCase()) == 0 ) {
                    listFilesToRename.add(elemPhDup);
                    elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
                }
                elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;
            }
        }

        Iterator<String> iterPhRen = listFilesToRename.listIterator();
        String elemPhRep = iterPhRen.hasNext() ? iterPhRen.next() : null;
        int koRenameDo = 0;
        while (elemPhRep != null ) {

            String generatedString = new Random().ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            //rename to rejet dans meme repertoire
            String oldName = elemPhRep;
            String newName = elemPhRep.substring(0,elemPhRep.lastIndexOf(".")) + "_" + generatedString + elemPhRep.substring(elemPhRep.lastIndexOf("."));
            koRenameDo = koRenameDo + Function.moveFile(oldName, newName, dbLr);
            elemPhRep = iterPhRen.hasNext() ? iterPhRen.next() : null;
        }
        LOGGER.info("    --- corrige Physique lc_duplicate            : toDo = " + listFilesToRename.size() + " , Done = " + koRenameDo + "\n");
        //------------------------------------------------
    }

    private static String lrHashOf(String file) throws IOException, ParseException {
        //706893605.66357:img_2337.MOV:86632353
        //modtime(windows):filename:sizeinbytes

        Path filePath = Paths.get(file);
        FileTime fileTime = Files.getLastModifiedTime(filePath);
        // Convert the FileTime to microsecond
        long unixTimeMicro = fileTime.to(TimeUnit.MICROSECONDS);
        double dbFileTime = ((((double) unixTimeMicro) /10) / 100000) - Context.GMT01JAN200112AM;//GMT: Monday, January 1, 2001 12:00:00 AM
        String PerfectHash = new DecimalFormat("#.#####").format(dbFileTime) + ":" + filePath.getFileName() + ":" + String.valueOf(Files.size(filePath));
        String timestamp = new DecimalFormat("#.#####").format(dbFileTime);
        String hashLike = timestamp.substring(0, timestamp.length() - 3) + "%:%" + filePath.getFileName() + ":" + String.valueOf(Files.size(filePath));
        return hashLike;
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

        Output output1 = new Output(new String[]{"total size is", "bytes  received", "Number of", "deleting"}, new String[]{"%"}, progress);
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

    private static boolean isItTimeToSave() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        File syncMouchard = new File(ctx.getRepFonctionnel().getRepertoiresyncdest() + ctx.getRepFonctionnel().getSyncdestmouchard());
        if (!syncMouchard.exists()) {
            syncMouchard.createNewFile();
        }
        Calendar cal = Calendar.getInstance();
        long lastModified = syncMouchard.lastModified();
        cal.setTime(new Date(lastModified));
        cal.add(Calendar.DATE, Integer.parseInt(ctx.getRepFonctionnel().getSyncAmountDaysBetween()));

        Calendar calt = Calendar.getInstance();
        calt.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOGGER.info("Today                                   : " + sdf.format(calt.getTimeInMillis()));
        LOGGER.info("Dernier Modification du fichier mouchard: " + sdf.format(lastModified));
        LOGGER.info("Prochain TimeToSave                     : " + sdf.format(cal.getTimeInMillis()));

        boolean isItTimeToSave = calt.compareTo(cal) > 0;
        LOGGER.info("isItTimeToSave                          : " + isItTimeToSave);
        return isItTimeToSave;
    }

    private static void regrouperLesNouvellesPhoto(JProgressBar progress) throws SQLException, IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        //Regroupement
        String[] repertoire50NEW = new String[]{ctx.getParamTriNew().getRepertoire50NEW(), ctx.getRepertoire00NEW()};
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), repertoire50NEW);

        GrpPhoto listFileBazar = new GrpPhoto();
        GrpPhoto listElekidz = new GrpPhoto();
        List<GrpPhoto> listGrpEletmp = new ArrayList();
        GrpPhoto listEletmp = new GrpPhoto();

        int numRow = 0;
        String txtPr = dbLr.retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
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

            visuProgress(progress, txtPr, numRow++, numRowMax);

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
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), new String[]{});

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
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER_TO_TAG_RAP);

        for (GrpPhoto listEle : listGrpEletmp) {
            Map<String, Map<String, Integer>> listeAction = new HashMap<>();
            if (listEle.size() > 1 && listEle.getArrayRep(Context.IREP_NEW) > 0 && listEle.size() > listEle.getArrayRep(Context.IREP_NEW)) {
                Context.nbDiscretionnaire++;
                String nbDiscr = String.format("%1$03X", Context.nbDiscretionnaire);
                String tag = Context.TAG_RAPPROCHEMENT + "_" + nbDiscr + "_" + Context.POSSIBLE_NEW_GROUP;
                LOGGER.info("tag : " + tag + " ==> ");
                LOGGER_TO_TAG_RAP.info("    ");
                LOGGER_TO_TAG_RAP.info("tag : " + tag + " ==> ");
                for (ElementFichier eleFile : listEle.lstEleFile) {
                    dbLr.AddKeywordToFile(eleFile.getFileIdLocal(), tag, Context.TAG_RAPPROCHEMENT);
                    LOGGER.debug(" --- " + eleFile.getPathFromRoot() + File.separator + eleFile.getLcIdxFilename());
                    LOGGER_TO_TAG_RAP.debug(" --- " + eleFile.getPathFromRoot() + File.separator + eleFile.getLcIdxFilename());
                }
                //display info
                int i;
                List<ElementFichier> lstEleFile = listEle.getLstEleFile();
                for (i = 0; i < lstEleFile.size(); i++) {
                    String masterKey = lstEleFile.get(i).getAbsolutePath();
                    if (listeAction.containsKey(masterKey)) {

                        Map<String, Integer> listeValueAction = listeAction.get(masterKey);
                        String valueKey = lstEleFile.get(i).getPathFromRoot();
                        if (listeValueAction.containsKey(valueKey)) {
                            listeValueAction.replace(valueKey, listeValueAction.get(valueKey) + 1);
                        } else {
                            listeValueAction.put(valueKey, 1);
                        }

                        listeAction.replace(masterKey, listeValueAction);

                    } else {
                        Map<String, Integer> value = new HashMap<>();
                        value.put(lstEleFile.get(i).getPathFromRoot(), 1);
                        listeAction.put(masterKey, value);
                    }
                }
                for (String masterKey : listeAction.keySet()) {
                    Map<String, Integer> listeValueAction = listeAction.get(masterKey);
                    LOGGER.info("    " + masterKey);
                    LOGGER_TO_TAG_RAP.info("    " + masterKey);
                    for (String valueKey : listeValueAction.keySet()) {
                        LOGGER.info("        " + String.format("%05d", listeValueAction.get(valueKey)) + " - " + valueKey);
                        LOGGER_TO_TAG_RAP.info("        " + String.format("%05d", listeValueAction.get(valueKey)) + " - " + valueKey);
                    }
                }
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
                listEle.deBounce();
                for (ElementFichier eleGrp : listEle.lstEleFileWithoutDuplicates) {

                    String newName = ctx.getParamTriNew().getRepertoire50NEW() + nomRep + File.separator + addSourceToNameFor(SystemFiles.normalizePath(eleGrp.getAbsolutePath()).compareTo(SystemFiles.normalizePath(ctx.getRepertoire00NEW())) == 0, eleGrp.getPathFromRoot(), eleGrp.getLcIdxFilename());
//                    LOGGER.info("move from {} to {} " , eleGrp.getPath(), newName );
                    Function.moveFile(eleGrp.getPath(), Function.modifyPathIfExistWithRandom(newName), dbLr);

                }
            }
            LOGGER.info("Repertoire New - Nb groupe => " + String.format("%05d", listGrpEletmp.size()) + " pour un total de " + String.format("%05d", nbtot) + " elements ");
        }

        if (listElekidz.lstEleFile.size() > 0) {
            //deplacement des group d'elements Kidz
            LOGGER.info("Repertoire New - vers Kidz : " + String.format("%05d", listElekidz.lstEleFile.size()));
            for (ElementFichier eleGrp : listElekidz.lstEleFile) {
                String newName = ctx.getParamTriNew().getRepertoireKidz() + File.separator + eleGrp.getLcIdxFilename();
                Function.moveFile(eleGrp.getPath(), Function.modifyPathIfExistWithRandom(newName), null);
            }
        }

        if (listFileBazar.lstEleFile.size() > 0) {
            //deplacement des group d'elements Bazar
            LOGGER.info("Repertoire New - nb Bazar  : " + String.format("%05d", listFileBazar.lstEleFile.size()));
            for (ElementFichier eleGrp : listFileBazar.lstEleFile) {
                String newName = ctx.getParamTriNew().getRepertoireBazar() + File.separator + eleGrp.getLcIdxFilename();
                Function.moveFile(eleGrp.getPath(), Function.modifyPathIfExistWithRandom(newName), dbLr);
            }
        }
    }

    private static String addSourceToNameFor(boolean toDo, String path, String lcIdxFilename) {
        if (toDo) {
            return path.replaceAll("[@\\/\\'()]", "_") + lcIdxFilename;
        } else {
            return lcIdxFilename;
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

                visuProgress(progress, repPhoto.repertoire, i++, listRep.size());

                String repertoire = repertoireIterator.next();
                findZipAndExtractToRejet(repertoire);
            }

        }
        progress.setString("");
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
            isFinished = WorkWithRepertory.deleteEmptyRep(new File(folderlocation), progress);
        } while (!isFinished);
        progress.setString("");
    }

    private static void purgeDesRepertoireVide50New() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getParamTriNew().getRepertoire50NEW();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(new File(folderlocation), progress);
        } while (!isFinished);
        progress.setString("");
    }

    private static void purgeDesRepertoireVide00NEW() throws IOException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        String folderlocation = ctx.getRepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = WorkWithRepertory.deleteEmptyRep(new File(folderlocation), progress);
        } while (!isFinished);
        progress.setString("");
    }


    private static void rangerLesRejets() throws IOException, SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        List<String> arrayFichierRejetStr = getFichierRejetStr();

        putThatInRejet(arrayFichierRejetStr);
    }

    private static List<String> getFichierRejetStr() throws SQLException {
        List<File> arrayFichierRejet = WorkWithFiles.getFilesFromRepertoryWithFilter(ctx.getRepertoire50Phototheque(), ctx.getArrayNomSubdirectoryRejet(), ctx.getParamElementsRejet().getExtFileRejet());

        List<String> arrayFichierRejetStr = new ArrayList<>();
        for (File FichierRejet : arrayFichierRejet) {
            arrayFichierRejetStr.add(FichierRejet.toString());
        }

        for (int y = 0; y < arrayFichierRejetStr.size(); y++) {
            if (arrayFichierRejetStr.get(y).toLowerCase(Locale.ROOT).contains((ctx.getRepertoire50Phototheque() + repertoirerejet).toLowerCase(Locale.ROOT))) {
                arrayFichierRejetStr.remove(y);
                y--;
            }
        }

        //todo add all photo rejected to arrayFichierRejetStr
        arrayFichierRejetStr.addAll(dbLr.getlistPhotoFlagRejeter());
        return arrayFichierRejetStr;
    }

    private static int putThatInRejet(List<String> arrayFichierRejetStr) throws IOException, SQLException {

        int countMove = 0;
        Map<String, Integer> countExt = new HashMap<>();
        Map<String, Integer> countExtDo = new HashMap<>();
        Map<String, Integer> countExtDel = new HashMap<>();
        LOGGER.debug("arrayFichierRejet     => " + String.format("%05d", arrayFichierRejetStr.size()));

        for (int y = 0; y < arrayFichierRejetStr.size(); y++) {
            String fichierStr = arrayFichierRejetStr.get(y);

            visuProgress(progress,fichierStr,y,arrayFichierRejetStr.size());

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

            if (ctx.getExtensionsUseFile().contains(fileExt.toLowerCase()) ||
                    (
                            ctx.getParamElementsRejet().getExtFileRejet().compareTo(fileExt.toLowerCase()) == 0
                                    && !ctx.getParamElementsRejet().getArrayNomSubdirectoryRejet().stream().anyMatch(filepath::contains)
                    )
            ) {
                String generatedString =  new Random().ints(48, 122 + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(5)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                //rename to rejet dans meme repertoire
                String oldName = fichierStr;
                String newName = addRejetSubRepToPath(oldName) + "." + generatedString + "." + ctx.getParamElementsRejet().getExtFileRejet();
                File fsource = new File(oldName);
                File fdest = new File(newName);
                if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                    countExtDo.replace(fileExt, countExtDo.get(fileExt), countExtDo.get(fileExt) + 1);
                    countMove = countMove + Function.moveFile(oldName, newName, dbLr);
                }
            } else {
                if (ctx.getParamElementsRejet().getarrayExtensionFileRejetSup().contains(fileExt.toLowerCase())) {
                    int retNb = moveTo99Rejet(fichierStr);
                    countMove = countMove + retNb;
                    countExtDel.replace(fileExt, countExtDel.get(fileExt), countExtDel.get(fileExt) + retNb);

                }
            }
        }
        progress.setString("");
        Iterator it = countExt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info("arrayFichierRejet." + pair.getKey() + " => " + String.format("%05d", pair.getValue()) + " => do : " + String.format("%05d", countExtDo.get(pair.getKey())) + " => del : " + String.format("%05d", countExtDel.get(pair.getKey())));
        }
        return countMove;
    }

    private static int moveTo99Rejet(String fichierStr) throws IOException, SQLException {
        //move to 99-rejet
        int i;
        int countMove = 0;
        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();
        for (i = 0; i < arrayRepertoirePhoto.size(); i++) {
            RepertoirePhoto repertoirePhoto = arrayRepertoirePhoto.get(i);
            if (repertoirePhoto.getNomunique().contains("Rejet")) {

                String oldName = fichierStr;
                File fsource = new File(oldName);

                File fdest = new File(ctx.getRepertoire50Phototheque() + repertoirePhoto.getRepertoire() + Context.FOLDERDELIM + oldName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
                String newName = fdest.toString();

                if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                    countMove = countMove + Function.moveFile(oldName, newName, dbLr);
                } else {
                    LOGGER.debug("oldName " + oldName + " move impossile to newName : " + newName);
                    LOGGER.debug("fsource.exists() " + fsource.exists());
                    LOGGER.debug("fsource.isFile()  " + fsource.isFile());
                    LOGGER.debug("!fdest.exists() " + !fdest.exists());
                }

            }
        }
        return countMove;
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
        progress.setValue(numRow);
        progress.setMaximum(numRowMax);
        progress.setString(txtPr + " - " + new DecimalFormat("#.##").format(numRow * 100L / numRowMax) + "%");
    }
}
