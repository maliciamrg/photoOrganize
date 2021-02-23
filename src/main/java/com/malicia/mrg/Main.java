package com.malicia.mrg;

import com.malicia.mrg.app.workWithFiles;
import com.malicia.mrg.app.workWithRepertory;
import com.malicia.mrg.data.Database;
import com.malicia.mrg.param.RepertoirePhoto;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.lingala.zip4j.ZipFile;

import java.io.*;
import java.util.List;
import java.util.ListIterator;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    //Work In Progress ??????
    private static final Boolean isInWork = Boolean.TRUE;

    private static Context ctx;
    private static Database dbLr;

    public static void main(String[] args) {
        try {
            chargeLog4j();

            /// chargement application
            ctx = Context.chargeParam();
            dbLr = Database.chargeDatabaseLR();
            //*

            //En Fonction De La Strategies De Rangement
            rangerLesRejets();
            _____________is___________In_______________Work____________();
            renommerLesRepertoires();
            regrouperLesNouvellesPhoto();
            //*

            //Nettoyage repertoires Local
            PurgeDesRepertoireVide50Photothèque();

            //Nettoyage repertoires réseaux
            purgeDesRepertoireVide00NEW();

            //Sauvegarde Lightroom sur Local
            SauvegardeLightroomConfigSauve();

            //sauvegarde Vers Réseaux Pour Cloud
            sauvegardeStudioPhoto2Réseaux();

        } catch (IOException e) {
            e.printStackTrace();
            exceptionLog(e, LOGGER);
        }

    }

    private static void _____________is___________In_______________Work____________() {
        if (isInWork) {
            throw new IllegalStateException("Under Construct");
        }
    }

    private static void chargeLog4j() {
        InputStream stream = Context.class.getClassLoader().getResourceAsStream("log4j2.properties");
//        LogManager.getLogManager().readConfiguration(stream);
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

    private static void sauvegardeStudioPhoto2Réseaux() {
        //todo
    }

    private static void regrouperLesNouvellesPhoto() {
        //todo
    }

    private static void renommerLesRepertoires() {
        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();

        ListIterator<RepertoirePhoto> repertoirePhotoIterator = arrayRepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            RepertoirePhoto repPhoto = repertoirePhotoIterator.next();
            List<String> listRep = workWithRepertory.listRepertoireEligible(repPhoto);

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {
                String repertoire = repertoireIterator.next();
                String newRepertoire = workWithRepertory.newNameRepertoire(repertoire, repPhoto, ctx.getParamNommageRepertoire());
                renommerRepertoire(repertoire, newRepertoire);
            }
        }
    }

    private static void renommerRepertoire(String repertoire, String newRepertoire) {
        if (repertoire.compareTo(newRepertoire) != 0) {
            //Todo
        }
    }

    private static void SauvegardeLightroomConfigSauve() throws ZipException {
        // zip file with a folder
        new ZipFile(ctx.getRepertoireDestZip()).addFolder(new File(ctx.getRepertoireRoamingAdobeLightroom()));
    }

    private static void PurgeDesRepertoireVide50Photothèque() {
        String FOLDER_LOCATION = ctx.getRepertoire50Phototheque();
        boolean isFinished = false;
        do {
            isFinished = workWithRepertory.deleteEmptyRep(FOLDER_LOCATION);
        } while (!isFinished);
    }

    private static void purgeDesRepertoireVide00NEW() {
        String FOLDER_LOCATION = ctx.getRepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = workWithRepertory.deleteEmptyRep(FOLDER_LOCATION);
        } while (!isFinished);
    }


    private static void rangerLesRejets() {
        List<File> arrayFichierRejet = workWithFiles.getFilesFromRepertoryWithFilter(ctx.getRepertoire50Phototheque(), ctx.getArrayNomSubdirectoryRejet(), ctx.getParamElementsRejet().getExtFileRejet());

        ListIterator<File> arrayFichierRejetIterator = arrayFichierRejet.listIterator();
        while (arrayFichierRejetIterator.hasNext()) {
            File fichier = arrayFichierRejetIterator.next();
            String fileExt = FilenameUtils.getExtension(fichier.getName()).toLowerCase();

            if (fileExt.toLowerCase().compareTo("zip") == 0) {
                workWithFiles.extractZipFile(fichier);
            }

            if (ctx.getParamElementsRejet().getArrayNomFileRejet().contains(fileExt.toLowerCase())) {
                workWithFiles.renameFile(fichier.toString(), fichier.toString() + "." + ctx.getParamElementsRejet().getExtFileRejet());
            }
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
