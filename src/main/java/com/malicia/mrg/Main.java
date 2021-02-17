package com.malicia.mrg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        /// chargement aplication
        chargeParam();
        chargeDatabaseLR();
        //*

        //En Fonction De La Strategie De Rangement
        rangerLesRejets();
        renommerLesRepertoires();
        regrouperLesNouvellesPhoto();
        //*

        //Nettoyage repertoires Local
        nettoyagedesrejetsde50Phototheque();
        Purgedesrepertoirevide50Phototheque();

        //Nettoyage repertoires reseau
        purgedesrepertoirevide00NEW();

        //Sauvegarde Ligthroom sur Local
        SauvegardeLigthroomConfigSauve();

        //sauvgardeVers Reseau Pour Cloud
        SauvegardeStudioPhoto2Reseau();

    }
}
