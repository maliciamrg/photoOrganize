package com.malicia.mrg;

import com.malicia.mrg.app.workWithFiles;
import com.malicia.mrg.app.workWithRepertory;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.model.elementFichier;
import com.malicia.mrg.param.importJson.RepertoirePhoto;
import com.malicia.mrg.param.importJson.TriNew;
import com.malicia.mrg.util.Serialize;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.lingala.zip4j.ZipFile;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

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
            dbLr = Database.chargeDatabaseLR(ctx.getCatalogLrcat());
            //*

            //Maintenance database lr
            maintenanceDatabase();

            //En Fonction De La Strategies De Rangement
            rangerLesRejets();
            topperLesRepertoires();
            regrouperLesNouvellesPhoto();
            //*

            //Nettoyage repertoires Local
            PurgeDesRepertoireVide50Photothèque();
            //*

            //Nettoyage repertoires réseaux
            purgeDesRepertoireVide00NEW();
            //*

            //Sauvegarde Lightroom sur Local
            SauvegardeLightroomConfigSauve();

            _____________is___________In_______________Work____________();

            //sauvegarde Vers Réseaux Pour Cloud
            sauvegardeStudioPhoto2Réseaux();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            exceptionLog(e, LOGGER);
        }

    }

    private static void maintenanceDatabase() throws SQLException {
        LOGGER.info(dbLr.pathAbsentPhysique());
        LOGGER.info(dbLr.folderWithoutRoot());
        LOGGER.info(dbLr.folderAbsentPhysique());
        LOGGER.info(dbLr.fileWithoutFolder());
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
        //TODO
    }

    private static void regrouperLesNouvellesPhoto() throws SQLException, IOException {

        //Regroupement
        ResultSet rsele = dbLr.sqlgetListelementnewaclasser(ctx.getParamTriNew().getTempsAdherence(), ctx.getParamTriNew().getRepertoire50NEW());

        grpPhoto listFileBazar = new grpPhoto();
        grpPhoto listElekidz = new grpPhoto();
        List<grpPhoto> listGrpEletmp = new ArrayList();
        grpPhoto listEletmp = new grpPhoto();


        List<String> listkidsModel = ctx.getParamTriNew().getListeModelKidz();
        long maxprev = 0;
        while (rsele.next()) {

            // Recuperer les info de l'elements
            String file_id_local = rsele.getString("file_id_local");
            String file_id_global = rsele.getString("id_global");
            String folder_id_local = rsele.getString("folder_id_local");
            String absolutePath = rsele.getString("absolutePath");
            String pathFromRoot = rsele.getString("pathFromRoot");
            String lcIdxFilename = rsele.getString("lc_idx_filename");
            String cameraModel = rsele.getString("CameraModel");
            long mint = rsele.getLong("mint");
            long maxt = rsele.getLong("maxt");
            Double rating = rsele.getDouble("rating");
            Double pick = rsele.getDouble("pick");
            String fileformat = rsele.getString("fileformat");
            String orientation = rsele.getString("orientation");
            long captureTime = rsele.getLong("captureTime");

            elementFichier eleFile = new elementFichier(absolutePath, pathFromRoot, lcIdxFilename, file_id_local, folder_id_local, cameraModel, captureTime, mint, maxt, rating, pick);

            if (listkidsModel.contains(cameraModel)) {
                listElekidz.add(eleFile);
            } else {
                if (mint > maxprev) {

                    if (listEletmp.size() > ctx.getParamTriNew().getThresholdNew()) {
                        listGrpEletmp.add(listEletmp);
                    } else {
                        listFileBazar.addAll(listEletmp);
                    }

                    listEletmp = new grpPhoto();

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


        //deplacement des group d'elements New Trier
        int nbtot = 0;
        LOGGER.info("listGrpEletmp       => " + String.format("%05d", listGrpEletmp.size()));
        for (grpPhoto listEle : listGrpEletmp) {
            nbtot += listEle.lstEleFile.size();
            SimpleDateFormat repDateFormat = new SimpleDateFormat(TriNew.FORMATDATE_YYYY_MM_DD);
            String nomRep = repDateFormat.format(new Date(listEle.getFirstDate() * 1000)) + "_(" + String.format("%05d", listEle.lstEleFile.size()) + ")";
            for (elementFichier eleGrp : listEle.lstEleFile) {

                String newName = ctx.getParamTriNew().getRepertoire50NEW() + nomRep + File.separator + eleGrp.getLcIdxFilename();
                workWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);

            }
        }
        LOGGER.info("listGrpEletmp nbtot => " + String.format("%05d", nbtot));

        //deplacement des group d'elements Kidz
        LOGGER.info("listElekidz         => " + String.format("%05d", listElekidz.lstEleFile.size()));
        for (elementFichier eleGrp : listElekidz.lstEleFile) {
            String newName = ctx.getParamTriNew().getRepertoireKidz() + File.separator + eleGrp.getLcIdxFilename();
            workWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
        }

        //deplacement des group d'elements Bazar
        LOGGER.info("listFileBazar       => " + String.format("%05d", listFileBazar.lstEleFile.size()));
        for (elementFichier eleGrp : listFileBazar.lstEleFile) {
            String newName = ctx.getParamTriNew().getRepertoireBazar() + File.separator + eleGrp.getLcIdxFilename();
            workWithFiles.moveFileintoFolder(eleGrp, newName, dbLr);
        }


    }

    private static void topperLesRepertoires() throws IOException, SQLException {
        List<RepertoirePhoto> arrayRepertoirePhoto = ctx.getArrayRepertoirePhoto();

        ListIterator<RepertoirePhoto> repertoirePhotoIterator = arrayRepertoirePhoto.listIterator();
        while (repertoirePhotoIterator.hasNext()) {
            RepertoirePhoto repPhoto = repertoirePhotoIterator.next();
            LOGGER.info("topperLesRepertoires = > " + ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire());
            List<String> listRep = workWithRepertory.listRepertoireEligible(ctx.getRepertoire50Phototheque(), repPhoto);

            ListIterator<String> repertoireIterator = listRep.listIterator();
            while (repertoireIterator.hasNext()) {
                String repertoire = repertoireIterator.next();

                if (!workWithRepertory.isRepertoireOk(dbLr, repertoire, repPhoto, ctx.getParamControleRepertoire())) {
                    LOGGER.debug(repertoire + "=>" + "ko");
                }

            }

            File f = new File(ctx.getRepertoire50Phototheque() + repPhoto.getRepertoire() + "\\" + new File(repPhoto.getRepertoire()).getName() +  ".svg.json");
            Serialize.writeJSON(repPhoto, f);
            LOGGER.debug("ecriture fichier ->" + f.toString());

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


    private static void rangerLesRejets() throws IOException, SQLException {
        List<File> arrayFichierRejet = workWithFiles.getFilesFromRepertoryWithFilter(ctx.getRepertoire50Phototheque(), ctx.getArrayNomSubdirectoryRejet(), ctx.getParamElementsRejet().getExtFileRejet());

        Map<String, Integer> countExt = new HashMap<>();
        LOGGER.info("arrayFichierRejet     => " + arrayFichierRejet.size());

        ListIterator<File> arrayFichierRejetIterator = arrayFichierRejet.listIterator();
        while (arrayFichierRejetIterator.hasNext()) {
            File fichier = arrayFichierRejetIterator.next();
            String fileExt = FilenameUtils.getExtension(fichier.getName()).toLowerCase();

            if (countExt.containsKey(fileExt)) {
                boolean res = countExt.replace(fileExt, countExt.get(fileExt), countExt.get(fileExt) + 1);
            } else {
                countExt.put(fileExt, 1);
            }

            if (fileExt.toLowerCase().compareTo("zip") == 0) {
                LOGGER.info("unzip :" + fichier);
                workWithFiles.extractZipFile(fichier);
            }

            if (ctx.getParamElementsRejet().getArrayNomFileRejet().contains(fileExt.toLowerCase())) {
                String oldName = fichier.toString();
                String newName = oldName + "." + ctx.getParamElementsRejet().getExtFileRejet();
                File fsource = new File(oldName);
                if (!fsource.exists() || !fsource.isFile()) {
                    workWithFiles.renameFile(oldName, newName, dbLr);
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
