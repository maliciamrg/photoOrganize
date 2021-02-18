package com.malicia.mrg;

import com.malicia.mrg.app.workWithFiles;
import com.malicia.mrg.app.workWithRepertory;
import com.malicia.mrg.data.Database;
import com.malicia.mrg.param.repertoirePhoto;
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

            /// chargement aplication
            ctx = Context.chargeParam();
            dbLr = Database.chargeDatabaseLR();
            //*

            //En Fonction De La Strategie De Rangement
            rangerLesRejets();
            renommerLesRepertoires();
            regrouperLesNouvellesPhoto();
            //*

            //Nettoyage repertoires Local
            //depreciated nettoyagedesrejetsde50Phototheque();
            Purgedesrepertoirevide50Phototheque();

            //Nettoyage repertoires reseau
            purgedesrepertoirevide00NEW();

            //Sauvegarde Ligthroom sur Local
            SauvegardeLigthroomConfigSauve();

            //sauvgardeVers Reseau Pour Cloud
            SauvegardeStudioPhoto2Reseau();

        } catch (ZipException e) {
            e.printStackTrace();
            excptlog(e, LOGGER);
        }

    }

    private static void SauvegardeStudioPhoto2Reseau() {
        //todo
    }

    private static void regrouperLesNouvellesPhoto() {
        //todo
    }

    private static void renommerLesRepertoires() {
        List<repertoirePhoto> arrayrepertoirePhoto = ctx.getArrayrepertoirePhoto();

        ListIterator<repertoirePhoto> repertoirePhotoIterator = arrayrepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            repertoirePhoto repPhoto = repertoirePhotoIterator.next();
            List<String> listRep = workWithRepertory.listRepertoireEligible(repPhoto);

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {
                String repertoire = repertoireIterator.next();
                String newRepertoire = workWithRepertory.newNameRepertoire(repertoire, repPhoto, ctx.getParamNomageRepertoire());
                renommerRepertoire(repertoire, newRepertoire);
            }
        }
    }

    private static void renommerRepertoire(String repertoire, String newRepertoire) {
        if (repertoire.compareTo(newRepertoire) != 0) {
            //Todo
        }
    }

    private static void SauvegardeLigthroomConfigSauve() throws ZipException {
        // zip file with a folder
        new ZipFile(ctx.getRepertoireDestZip()).addFolder(new File(ctx.getRepertoireRoamingAdobeLightroom()));
    }

    private static void Purgedesrepertoirevide50Phototheque() {
        String FOLDER_LOCATION = ctx.getrepertoire50Phototheque();
        boolean isFinished = false;
        do {
            isFinished = workWithRepertory.deleteEmptyRep(FOLDER_LOCATION);
        } while (!isFinished);
    }

    private static void purgedesrepertoirevide00NEW() {
        String FOLDER_LOCATION = ctx.getrepertoire00NEW();
        boolean isFinished = false;
        do {
            isFinished = workWithRepertory.deleteEmptyRep(FOLDER_LOCATION);
        } while (!isFinished);
    }


    private static void rangerLesRejets() {
        List<File> arrayFichierRejet = workWithFiles.getFilesFromRepertory(ctx.getArraynomsubdirectoryrejet());

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
                    workWithFiles.renameFile(fichier.getName(), workWithFiles.changeExtansionTo(fichier.getName(), "rejet"));
            }
        }
    }

    public static void excptlog(Exception theException, Logger loggerori) {
        StringWriter stringWritter = new StringWriter();
        PrintWriter printWritter = new PrintWriter(stringWritter, true);
        theException.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush();
        loggerori.fatal("theException = " + "\n" + stringWritter.toString());
    }
}
