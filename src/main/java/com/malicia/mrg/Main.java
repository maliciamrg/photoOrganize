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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ListIterator;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static Context ctx;
    private static Database dbLr;

    public static void main(String[] args) {
        try {

            /// chargement application
            ctx = Context.chargeParam();
            dbLr = Database.chargeDatabaseLR();
            //*

            //En Fonction De La Strategies De Rangement
            rangerLesRejets();
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

        } catch (ZipException e) {
            e.printStackTrace();
            exceptionLog(e, LOGGER);
        }

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
        List<File> arrayFichierRejet = workWithFiles.getFilesFromRepertory(ctx.getArrayNomSubdirectoryRejet());

        ListIterator<File> arrayFichierRejetIterator = arrayFichierRejet.listIterator();
        while (arrayFichierRejetIterator.hasNext()) {
            File fichier = arrayFichierRejetIterator.next();
            switch (FilenameUtils.getExtension(fichier.getName()).toLowerCase()) {
                case "zip":
                    workWithFiles.extractZipFile(fichier);
                    break;
                case "rejet":
                    break;
                default:
                    workWithFiles.renameFile(fichier.getName(), workWithFiles.changeExtensionTo(fichier.getName(), "rejet"));
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
