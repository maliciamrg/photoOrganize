package com.malicia.mrg;

import com.malicia.mrg.app.workWithFiles;
import com.malicia.mrg.app.workWithRepertory;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static Context ctx;

    public static void main(String[] args) {
        /// chargement aplication
        ctx = Context.chargeParam();
        chargeDatabaseLR();
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

}
