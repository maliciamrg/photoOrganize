package com.malicia.mrg.model;

import com.malicia.mrg.Context;
import com.malicia.mrg.param.electx.ControleRepertoire;
import com.malicia.mrg.util.SQLiteJDBCDriverConnection;
import com.malicia.mrg.util.SystemFiles;

import com.malicia.mrg.util.WhereIAm;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.malicia.mrg.util.SystemFiles.normalizePath;

public class Database extends SQLiteJDBCDriverConnection {

    private static final Logger LOGGER = LogManager.getLogger(Database.class);

    private Database(String catalogLrcat) throws SQLException {
        super(catalogLrcat);
    }

    public static Database chargeDatabaseLR(String catalogLrcat, Boolean IS_DRY_RUN) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        Database database = new Database(catalogLrcat);
        database.setIsDryRun(IS_DRY_RUN);
        return database;
    }

    public void AddKeywordToFile(String fileIdLocal, String tag, String TagMaitre) throws SQLException {
        String idlocaltag = sqlcreateKeyword(TagMaitre, tag).get("keyWordIdlocal");
        long idlocalImage = sqlGetAdobeImage(fileIdLocal);

        long idlocalKeywordImage = sqlGetAgLibraryKeywordImage(idlocalImage, idlocaltag);
        if (idlocalKeywordImage == 0) {
            idlocalKeywordImage = sqlGetPrevIdlocalforKeywordImage();

            String sql;
            sql = "insert into AgLibraryKeywordImage " +
                    "( 'id_local', 'image', 'tag') " +
                    "VALUES " +
                    "('" + idlocalKeywordImage + "', '" + idlocalImage + "', '" + idlocaltag + "');" +
                    ";";
            executeUpdate(sql);
        }
    }

    private long sqlGetAdobeImage(String fileIdLocal) throws SQLException {

        ResultSet rsexist = select(
                " select  e.id_local as result" +
                        " from Adobe_images e " +
                        " where " + fileIdLocal + " = e.rootFile " +
                        ";");

        int idlocalImage = 0;
        while (rsexist.next()) {
            idlocalImage = rsexist.getString("result") == null ? 0 : rsexist.getInt("result");
        }
        return idlocalImage;

    }

    private long sqlGetAgLibraryKeywordImage(long idlocalImage, String idlocaltag) throws SQLException {

        ResultSet rsexist = select(
                " select  e.id_local as result" +
                        " from AgLibraryKeywordImage e " +
                        " where " + idlocalImage + " = e.image " +
                        " and " + idlocaltag + " = e.tag " +
                        ";");

        int idlocalKeywordImage = 0;
        while (rsexist.next()) {
            idlocalKeywordImage = rsexist.getString("result") == null ? 0 : rsexist.getInt("result");
        }
        return idlocalKeywordImage;

    }

    public int AddKeywordToRep(String repertoire, String tag, String TagMaitre) throws SQLException {
        return AddKeywordToRep(repertoire, tag, TagMaitre, "");
    }

    public int AddKeywordToRepNoVideo(String repertoire, String tag, String TagMaitre) throws SQLException {
        return AddKeywordToRep(repertoire, tag, TagMaitre, " and e.fileFormat != 'VIDEO' ");
    }

    private int AddKeywordToRep(String repertoire, String tag, String TagMaitre, String OptionalNoVideoCriteria) throws SQLException {

        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        String idlocaltag = sqlcreateKeyword(TagMaitre, tag).get("keyWordIdlocal");

//        Map<String, String> ret = new HashMap<>();
        String sql = "SELECT e.id_local as idlocalImage " +
                "FROM  AgLibraryFile a " +
                "inner join Adobe_images e  " +
                " on a.id_local = e.rootFile    " +
                "WHERE a.folder = '" + idLocalRep.get("Folderidlocal") + "'" +
                OptionalNoVideoCriteria +
                "order by e.captureTime asc " +
                ";";

        ResultSet rs = select(sql);
        int nb = 0;
        while (rs.next()) {

            nb++;
            Long idlocalImage = rs.getLong("idlocalImage");

            long idlocalKeywordImage = sqlGetAgLibraryKeywordImage(idlocalImage, idlocaltag);
            if (idlocalKeywordImage == 0) {
                idlocalKeywordImage = sqlGetPrevIdlocalforKeywordImage();

                String sqlu;
                sqlu = "insert into AgLibraryKeywordImage " +
                        "( 'id_local', 'image', 'tag') " +
                        "VALUES " +
                        "('" + idlocalKeywordImage + "', '" + idlocalImage + "', '" + idlocaltag + "');" +
                        ";";
                executeUpdate(sqlu);
            }

        }
        return nb;
    }

    public int topperARed50NEW(String repertoire50NEW) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '" + Context.RED + "' " +
                " where colorLabels <> '" + Context.RED + "' " +
                " and rootFile in ( " +
                getStringAllIdLocalFromNew(repertoire50NEW) +
                " ) " +
                ";";
        return executeUpdate(sql);

    }

    public int topperRepertoireARed(String repertoire) throws SQLException {
//        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

        Map<String, String> idlocalforRep = getIdlocalforRep(repertoire);
        String Folderidlocal = idlocalforRep.get("Folderidlocal");

        if (Folderidlocal.compareTo("0") != 0) {
            long newIdlocalforFolderLabel = sqlGetPrevIdlocalforFolderLabel();

            if (newIdlocalforFolderLabel == 0) {
                throw new IllegalStateException("no more idlocal empty for folder");
            }

            sqlDeleteIdlocalforFolderLabel(Folderidlocal);

            String sql;
            sql = "INSERT INTO AgLibraryFolderLabel" +
                    "(id_local, " +
                    "id_global, " +
                    "folder, " +
                    "label," +
                    "labelType ) " +
                    "VALUES " +
                    "('" + newIdlocalforFolderLabel + "', " +
                    "'" + UUID.randomUUID().toString().toUpperCase() + "', " +
                    "'" + Folderidlocal + "', " +
                    "'" + Context.RED.toLowerCase(Locale.ROOT) + "' ," +
                    "'Color')" +
                    ";";
            return executeUpdate(sql);
        }
        return 0;
    }


    private String getStringAllIdLocalFromNew(String repertoire50NEW) {
        return " select a.id_local as file_id_local " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b   " +
                " on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder p   " +
                " on b.rootFolder = p.id_local  " +
                "where '" + SystemFiles.normalizePath(repertoire50NEW) + "' like p.absolutePath || '%'  " +
                "and p.absolutePath || b.pathFromRoot like '" + SystemFiles.normalizePath(repertoire50NEW) + "' || '%' "
                ;
    }

    public void MiseAzeroDesColorLabels(String colortag) throws SQLException {
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '' " +
                "where colorLabels = '" + colortag + "' " +
                ";";
        executeUpdate(sql);
    }

    public int deTopperARedOldRepertoire() throws SQLException {
        String sql;
        sql = "delete from AgLibraryFolderLabel " +
                " where label = '" + Context.RED.toLowerCase(Locale.ROOT) + "' " +
                ";";
        return executeUpdate(sql);
    }

    public int deTopperARedOld50NEW(String repertoire50NEW) throws SQLException {
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '' " +
                " where colorLabels = '" + Context.RED + "' " +
                " and rootFile not in ( " +
                getStringAllIdLocalFromNew(repertoire50NEW) +
                " ) " +
                ";";
        return executeUpdate(sql);
    }


    public int sqlDeleteAdobe_images(String images_id_local) throws SQLException {
        String sql;
        sql = "delete from Adobe_images " +
                "where id_local = '" + images_id_local + "' " +
                ";";
        int ret = executeUpdate(sql);
        return ret;
    }

    public void creationContextEtPurgeKeyword(Map<String, String> listeAction) throws SQLException {

    }

    public void makeRepertory(String directoryName) throws SQLException {

        Map<String, String> idlocalforRep = getIdlocalforRep(new File(directoryName).getParent());
        String rootFolder = idlocalforRep.get("rootFolder");

        String pathFromRoot = normalizePath(normalizePath(directoryName).replace(normalizePath(idlocalforRep.get("absolutePath")), ""));

        Map<String, String> idlocalforNewRep = getIdlocalforRep(directoryName);

        if (idlocalforNewRep.get("Folderidlocal").compareTo("0") == 0) {

            long newIdlocalforNewRep = sqlGetPrevIdlocalforFolder();

            if (newIdlocalforNewRep == 0) {
                throw new IllegalStateException("no more idlocal empty for folder");
            }

            String sql;
            sql = "INSERT INTO AgLibraryFolder" +
                    "(id_local, " +
                    "id_global, " +
                    "pathFromRoot, " +
                    "rootFolder) " +
                    "VALUES " +
                    "('" + newIdlocalforNewRep + "', " +
                    "'" + UUID.randomUUID().toString().toUpperCase() + "', " +
                    "'" + pathFromRoot + "', " +
                    "'" + rootFolder + "')" +
                    ";";
            executeUpdate(sql);
        }

    }

    public void sqlmovefile(ElementFichier source, String destination) throws IOException, SQLException {

        File fdest = new File(destination);

        Map<String, String> dst = getIdlocalforRep(fdest.getParent());

        String sql;
        sql = "" +
                "update AgLibraryFile " +
                "set folder =  " + dst.get("Folderidlocal") + " ," +
                " baseName =  '" + FilenameUtils.getBaseName(destination) + "' , " +
                " idx_filename =  '" + fdest.getName() + "' , " +
                " lc_idx_filename =  '" + fdest.getName().toLowerCase() + "'  " +
                "where id_local =  " + source.getFileIdLocal() + " " +
                ";";
        executeUpdate(sql);


    }

    public void sqlmovefile(String fileIdLocal, String destination) throws IOException, SQLException {

        File fdest = new File(destination);

        Map<String, String> dst = getIdlocalforRep(fdest.getParent());

        String sql;
        sql = "" +
                "update AgLibraryFile " +
                "set folder =  " + dst.get("Folderidlocal") + " " +
                "where id_local =  " + fileIdLocal + " " +
                ";";
        executeUpdate(sql);

    }

    private void sqlDeleteIdlocalforFolderLabel(String folderIdLocal) throws SQLException {
        String sql = "delete FROM AgLibraryFolderLabel " +
                "where folder =  " + folderIdLocal + " " +
                "; ";
        executeUpdate(sql);
    }

    private long sqlGetPrevIdlocalforFolderLabel() throws SQLException {
        String sql = "select * FROM AgLibraryFolderLabel " +
                "ORDER by id_local desc " +
                "; ";
        ResultSet rs = select(sql);
        boolean first = true;
        long idLocalCalcul = 0;
        long idLocal = 0;
        while (rs.next()) {
            // Recuperer les info de l'elements
            idLocal = rs.getLong("id_local");
            if (first) {
                idLocalCalcul = idLocal;
                first = false;
            } else {
                idLocalCalcul -= 1;
                if (idLocalCalcul > idLocal) {
                    return idLocalCalcul;
                }
            }
        }
        // 0 ou 1 repertoire dans AgLibraryFolder
        if (idLocalCalcul == idLocal) {
            idLocalCalcul = (idLocal / 2) + 1;
            if (idLocalCalcul == idLocal) {
                idLocalCalcul = ThreadLocalRandom.current().nextInt((int) idLocalCalcul + 1, (int) idLocalCalcul + 99999 + 1);
            }
        }
        return idLocalCalcul;
    }


    private long sqlGetPrevIdlocalforFolder() throws SQLException {
        String sql = "select * FROM AgLibraryFolder " +
                "ORDER by id_local desc " +
                "; ";
        ResultSet rs = select(sql);
        boolean first = true;
        long idLocalCalcul = 0;
        long idLocal = 0;
        while (rs.next()) {
            // Recuperer les info de l'elements
            idLocal = rs.getLong("id_local");
            if (first) {
                idLocalCalcul = idLocal;
                first = false;
            } else {
                idLocalCalcul -= 1;
                if (idLocalCalcul > idLocal) {
                    return idLocalCalcul;
                }
            }
        }
        // 0 ou 1 repertoire dans AgLibraryFolder
        if (idLocalCalcul == idLocal) {
            idLocalCalcul = (idLocal / 2) + 1;
        }
        return idLocalCalcul;
    }

    public void renameFileLogique(String oldName, String newName) throws SQLException {
        long idlocal = getIdlocalforFilePath(oldName).get("idlocal");
        long idfoldernew = Long.parseLong(getIdlocalforRep(newName).get("Folderidlocal"));
        if (idlocal > 0 && idfoldernew > 0) {
            File fdest = new File(newName);
            String sql;
            String baseName = FilenameUtils.getBaseName(newName);
            String ext = FilenameUtils.getExtension(newName);
            sql = "" +
                    "update AgLibraryFile " +
                    "set " +
                    " baseName =  '" + baseName + "' , " +
                    " idx_filename =  '" + fdest.getName() + "' , " +
                    " extension =  '" + ext + "' , " +
                    " lc_idx_filename =  '" + fdest.getName().toLowerCase() + "' , " +
                    " lc_idx_filenameExtension =  '" + ext.toLowerCase() + "' , " +
                    " folder =  " + idfoldernew + " " +
                    "where id_local =  " + idlocal + " " +
                    ";";
            executeUpdate(sql);
        }
    }

    private Map<String, Long> getIdlocalforFilePath(String path) throws SQLException {
        File fpath = new File(path);
        String baseName = FilenameUtils.getBaseName(path);
        String ext = FilenameUtils.getExtension(path);
        ResultSet rsexist = select(
                "select p.absolutePath ,  p.id_local , fo.rootFolder , fo.pathFromRoot , fo.id_local , fi.folder , fi.originalFilename , fi.id_local as result  " +
                        "from AgLibraryRootFolder as p , " +
                        "AgLibraryFolder as fo , " +
                        "AgLibraryFile as fi " +
                        "where '" + SystemFiles.normalizePath(fpath.getParent()) + "' like p.absolutePath || '%'  " +
                        "and fo.rootFolder = p.id_local " +
                        "and '" + SystemFiles.normalizePath(fpath.getParent() + File.separator) + "' = p.absolutePath || fo.pathFromRoot  " +
                        "and fi.folder = fo.id_local " +
                        "and fi.baseName =  '" + baseName + "' " +
                        "and fi.extension =  '" + ext + "'   " +
                        ";");

        Map<String, Long> ret = new HashMap<>();
        ret.put("idlocal", 0l);
        ret.put("rootFolder", 0l);
        while (rsexist.next()) {
            ret.replace("idlocal", rsexist.getLong("result"));
            ret.replace("rootFolder", rsexist.getLong("rootFolder"));
        }
        return ret;
    }


    private Map<String, String> getIdlocalforRep(String repertoire) throws SQLException {
        ResultSet rsexist = select(
                "select p.absolutePath ,  p.id_local , fo.rootFolder , fo.pathFromRoot , fo.id_local as result  " +
                        "from AgLibraryRootFolder as p , " +
                        "AgLibraryFolder as fo " +
                        "where '" + SystemFiles.normalizePath(repertoire) + "' like p.absolutePath || '%'  " +
                        "and fo.rootFolder = p.id_local " +
                        "and '" + SystemFiles.normalizePath(repertoire + File.separator) + "' = p.absolutePath || fo.pathFromRoot  " +
                        ";");
        Map<String, String> ret = new HashMap<>();
        ret.put("Folderidlocal", "0");
        while (rsexist.next()) {
            ret.replace("Folderidlocal", rsexist.getString("result"));
            ret.put("rootFolder", rsexist.getString("rootFolder"));
            ret.put("absolutePath", rsexist.getString("absolutePath"));
            ret.put("pathFromRoot", rsexist.getString("pathFromRoot"));
        }
        return ret;
    }

    public Map<String, Integer> getStarValue(String repertoire) throws SQLException {
        return getStarValue(repertoire, "");
    }

    public Map<String, Integer> getStarValueNoVideo(String repertoire) throws SQLException {
        return getStarValue(repertoire, " and e.fileFormat != 'VIDEO' ");
    }

    private Map<String, Integer> getStarValue(String repertoire, String OptionalNoVideoCriteria) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        Map<String, Integer> idlocal = new HashMap<>();
        idlocal.put("0", 0);
        idlocal.put("1", 0);
        idlocal.put("2", 0);
        idlocal.put("3", 0);
        idlocal.put("4", 0);
        idlocal.put("5", 0);

        ResultSet rsexist = select(
                "select " +
                        "e.rating , " +
                        "count(*) as result " +
                        "from AgLibraryFile a  " +
                        "inner join Adobe_images e  " +
                        " on a.id_local = e.rootFile    " +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder  " +
                        OptionalNoVideoCriteria +
                        " group by e.rating " +
                        ";");

        while (rsexist.next()) {
            String rating = String.valueOf(rsexist.getLong("rating")) == null ? "0" : String.valueOf(rsexist.getLong("rating"));
            boolean res = idlocal.replace(rating, idlocal.get(rating), idlocal.get(rating) + rsexist.getInt("result"));
        }
        return idlocal;
    }

    public double ecartJourFolder(String repertoire) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        ResultSet rsexist = select(
                " select min(strftime('%s', e.captureTime)) as captureTimeMin , " +
                        " max(strftime('%s', e.captureTime)) as captureTimeMax " +
                        " from AgLibraryFile a" +
                        " inner join Adobe_images e" +
                        " on a.id_local = e.rootFile" +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder" +
                        " and e.pick >= 0" +
                        " ;");

        SimpleDateFormat repDateFormat = new SimpleDateFormat(ControleRepertoire.FORMATDATE_YYYY_MM_DD);
        DateTime captureTimeMin = null;
        DateTime captureTimeMax = null;
        while (rsexist.next()) {
            captureTimeMin = new DateTime(rsexist.getLong("captureTimeMin") * 1000);
            captureTimeMax = new DateTime(rsexist.getLong("captureTimeMax") * 1000);
        }
        double days = Days.daysBetween(captureTimeMin, captureTimeMax).getDays() + 1;
        return days;
    }

    public double nbJourFolderVideo(String repertoire) throws SQLException {
        return nbJourFolder(repertoire, " and e.fileFormat = 'VIDEO' ");
    }

    public double nbJourFolderNoVideo(String repertoire) throws SQLException {
        return nbJourFolder(repertoire, " and e.fileFormat != 'VIDEO' ");
    }

    public double nbJourFolder(String repertoire, String OptionalNoVideoCriteria) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        ResultSet rsexist = select(
                " select " +
                        " count(*) , " +
                        " strftime('%Y%m%d', e.captureTime) " +
                        " from AgLibraryFile a" +
                        " inner join Adobe_images e" +
                        " on a.id_local = e.rootFile" +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder" +
                        " and e.pick >= 0" +
                        OptionalNoVideoCriteria +
                        " group by strftime('%Y%m%d', e.captureTime)" +
                        " ;");


        double days = 0;
        while (rsexist.next()) {
            days++;
        }
        return days;
    }

    public int nbPickAllEle(String repertoire) throws SQLException {
        return nbPick(repertoire, " and e.pick > 0 ", "");
    }

    public int nbPickNoVideo(String repertoire) throws SQLException {
        return nbPick(repertoire, " and e.pick > 0 ", " and e.fileFormat != 'VIDEO' ");
    }

    public int nbNoPickNoVideo(String repertoire) throws SQLException {
        return nbPick(repertoire, " and e.pick = 0 ", " and e.fileFormat != 'VIDEO' ");
    }

    private int nbPick(String repertoire, String PickCriteria, String OptionalNoVideoCriteria) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        ResultSet rsexist = select(
                " select  count(*) as result" +
                        " from AgLibraryFile a  " +
                        " inner join Adobe_images e " +
                        " on a.id_local = e.rootFile " +
//                        " " +
//                        Context.filtreImportScanUn +
//                        " " +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder " +
                        PickCriteria +
                        OptionalNoVideoCriteria +
//                        "" +
//                        Context.filtreImportScanDeux +
//                        "" +
                        ";");

        int nbpick = 0;
        while (rsexist.next()) {
            nbpick = rsexist.getString("result") == null ? 0 : rsexist.getInt("result");
        }
        return nbpick;
    }


    public String getDate(String repertoire) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        ResultSet rsexist = select(
                " select min(strftime('%s', e.captureTime)) as captureTime" +
                        " from AgLibraryFile a" +
                        " inner join Adobe_images e" +
                        " on a.id_local = e.rootFile" +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder" +
                        " and e.pick >= 0" +
                        " ;");

        long captureTime = 0;
        while (rsexist.next()) {
            captureTime = rsexist.getLong("captureTime");
        }
        SimpleDateFormat repDateFormat = new SimpleDateFormat(ControleRepertoire.FORMATDATE_YYYY_MM_DD);
        return repDateFormat.format(new Date(captureTime * 1000));
    }

    public Boolean isValueInKeyword(String getoValue, String tagAction) throws SQLException {
        List<String> listTag = getValueForKeyword(tagAction);
        return listTag.contains(getoValue);
    }

    public List<String> getValueForKeyword(String getcChamp) throws SQLException {
        List<String> listLcName = new ArrayList<>();
        ResultSet rs = select("select lc_name " +
                "from AgLibraryKeyword " +
                "where genealogy like ( " +
                "select '/%_' || id_local || '/%' " +
                "from  AgLibraryKeyword " +
                "where lc_name = '" + getcChamp.toLowerCase() + "' ) " +
                ";");
        while (rs.next()) {
            listLcName.add(rs.getString("lc_name"));
        }
        return listLcName;
    }


    /**
     * Sqlget listelementnewaclasser result set.
     *
     * @param tempsAdherence  the temps adherence
     * @param repertoire50NEW
     * @return the result set
     * @throws SQLException the sql exception
     */
    public ResultSet sqlgetListelementnewaclasser(String tempsAdherence, String repertoire50NEW) throws SQLException {
        String CLAUSEWHERE = "";

        if (repertoire50NEW.compareTo("") != 0) {
            CLAUSEWHERE = "where '" + SystemFiles.normalizePath(repertoire50NEW) + "' like p.absolutePath || '%'  " +
                    "and p.absolutePath || b.pathFromRoot like '" + SystemFiles.normalizePath(repertoire50NEW) + "' || '%' ";
        }

        return select(
                "select a.id_local as file_id_local, " +
                        "a.id_global , " +
                        "b.id_local as folder_id_local , " +
                        "p.absolutePath , " +
                        "b.pathFromRoot , " +
                        "a.lc_idx_filename as lc_idx_filename , " +
                        " aiecm.value as CameraModel , " +
                        " strftime('%s', DATETIME( e.captureTime,\"-" + tempsAdherence + "\")) as mint , " +
                        " strftime('%s', DATETIME(e.captureTime,\"+" + tempsAdherence + "\")) as maxt  , " +
                        "b.rootFolder , " +
                        "e.rating , " +
                        "e.pick , " +
                        "e.fileformat , " +
                        "e.orientation , " +
                        "strftime('%s', e.captureTime) as captureTime , " +
                        " ( '" + SystemFiles.normalizePath(repertoire50NEW) + "' like p.absolutePath || '%'  " +
                        "and p.absolutePath || b.pathFromRoot like '" + SystemFiles.normalizePath(repertoire50NEW) + "' || '%' ) as isNew " +
                        "from Adobe_images e  " +
                        "inner join AgLibraryFile a  " +
                        " on a.id_local = e.rootFile    " +
                        "inner join AgLibraryFolder b   " +
                        " on a.folder = b.id_local  " +
                        "inner join AgLibraryRootFolder p   " +
                        " on b.rootFolder = p.id_local  " +
                        "LEFT JOIN AgHarvestedExifMetadata ahem " +
                        "ON e.id_local = ahem.image " +
                        "LEFT JOIN AgInternedExifCameraModel aiecm " +
                        "ON ahem.cameraModelRef = aiecm.id_local " +
                        CLAUSEWHERE +
                        "order by julianday(e.captureTime) asc " +
                        "" +
//                        "limit 10 " +
                        ";");


    }


    public String pathAbsentPhysique(JProgressBar progress) throws SQLException {
        String sql = "select " +
                "c.absolutePath , " +
                "b.pathFromRoot , " +
                "a.lc_idx_filename as lc_idx_filename , " +
                "c.id_local as path_id_local , " +
                "b.id_local as folder_id_local , " +
                "a.id_local as file_id_local  , " +
                "b.rootFolder " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b   " +
                " on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder c   " +
                " on b.rootFolder = c.id_local  " +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int nb = 0;
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            File filepath = new File(rs.getString("absolutePath") + rs.getString("pathFromRoot") + rs.getString("lc_idx_filename"));
            nb += 1;
            if (!filepath.exists()) {
                txtret += "(debug) ko = " + "file_id_local" + "(" + rs.getString("file_id_local") + ")" + filepath + " " + "\n";
                ko += 1;
                koCor += sqlDeleteFile(rs.getString("file_id_local"));
            }
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb path logique = " + nb + " : absent physique = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String folderAbsentPhysique(JProgressBar progress) throws SQLException {
        String sql = "select " +
                "c.absolutePath , " +
                "b.pathFromRoot , " +
                "c.id_local as path_id_local , " +
                "b.id_local as folder_id_local , " +
                "b.rootFolder " +
                "from AgLibraryFolder b   " +
                "inner join AgLibraryRootFolder c   " +
                " on b.rootFolder = c.id_local  " +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int nb = 0;
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            File filepath = new File(rs.getString("absolutePath") + rs.getString("pathFromRoot"));
            nb += 1;
            if (!filepath.exists()) {
                txtret += "(debug) ko = " + "folder_id_local" + "(" + rs.getString("folder_id_local") + ")" + filepath + "\n";
                ko += 1;
                koCor += sqlDeleteRepertory(rs.getString("folder_id_local"));
            }
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb folder logique = " + nb + " : absent physique = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String fileWithoutFolder(JProgressBar progress) throws SQLException {
        String sql = "select " +
                " b.pathFromRoot , " +
                " a.lc_idx_filename as lc_idx_filename , " +
                " b.id_local as folder_id_local , " +
                " a.id_local as file_id_local  , " +
                " b.rootFolder " +
                "from AgLibraryFile a " +
                "left join AgLibraryFolder b " +
                " on a.folder = b.id_local " +
                "WHERE b.pathFromRoot is NULL" +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            txtret += "(debug) ko = " + "file_id_local" + "(" + rs.getString("file_id_local") + ")" + " lc_idx_filename => " + rs.getString("lc_idx_filename") + "\n";
            ko += 1;
            koCor += sqlDeleteFile(rs.getString("file_id_local"));
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb file without Folder = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String AdobeImagesWithoutLibraryFile(JProgressBar progress) throws SQLException {
        String sql = "SELECT e.id_local  from Adobe_images e " +
                "left join AgLibraryFile f " +
                "on f.id_local = e.rootFile " +
                "where lc_idx_filename is NULL" +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            txtret += "(debug) ko = " + "rootFile" + "(" + rs.getString("id_local") + ")" + "\n";
            ko += 1;
            koCor += sqlDeleteAdobe_images(rs.getString("id_local"));
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb Images Without File = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String KeywordImageWithoutImages(JProgressBar progress) throws SQLException {
        String sql = "SELECT ki.id_local as id_local " +
                "from AgLibraryKeywordImage ki " +
                "left join Adobe_images i " +
                "on ki.image = i.id_local " +
                "where i.rootFile is NULL " +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            txtret += "(debug) ko = " + "KeywordImage" + "(" + rs.getString("id_local") + ")" + "\n";
            ko += 1;
            koCor += removeKeywordImages(rs.getString("id_local"));
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb KeyImg Without Img  = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String folderWithoutRoot(JProgressBar progress) throws SQLException {
        String sql = "select c.absolutePath , " +
                "b.pathFromRoot , " +
                "c.id_local as path_id_local , " +
                "b.id_local as folder_id_local , " +
                "b.rootFolder " +
                " from AgLibraryFolder b   " +
                " LEFT join AgLibraryRootFolder c " +
                "  on b.rootFolder = c.id_local " +
                " WHERE c.absolutePath is NULL" +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int ko = 0;
        int koCor = 0;

        int numRow = 0;
        String txtPr = retWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName());
        int numRowMax = getQueryRowCount(sql);

        while (rs.next()) {
            txtret += "(debug) ko = " + "folder_id_local" + "(" + rs.getString("folder_id_local") + ")" + " pathFromRoot => " + rs.getString("pathFromRoot") + "\n";
            ko += 1;
            koCor += sqlDeleteRepertory(rs.getString("folder_id_local"));
            visuProgress(progress,txtPr,numRow++,numRowMax);
        }
        txtret += " nb folder without Root = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    private int sqlDeleteRepertory(String folderIdLocal) throws SQLException {
        String sql = " delete " +
                "from AgLibraryFolder  " +
                " where id_local = '" + folderIdLocal + "' " +
                " ; ";
        return executeUpdate(sql);
    }

    private int sqlDeleteFile(String fileIdLocal) throws SQLException {
        String sql = " delete " +
                "from AgLibraryFile  " +
                " where id_local = '" + fileIdLocal + "' " +
                " ; ";
        return executeUpdate(sql);
    }

    public Map<String, String> sqlcreateKeyword(String keywordmaitre, String keyword) throws SQLException {
        Map<String, String> ret = new HashMap<String, String>();
        String keyWordIdlocal = getIdforKeyword(keyword).get("KeyWordIdlocal");
        if (keyWordIdlocal.compareTo("") == 0) {

            Map<String, String> idforKeywordMaitre = getIdforKeyword(keywordmaitre);

            long idlocal = sqlGetPrevIdlocalforKeyword();
            String sql = "INSERT INTO AgLibraryKeyword (id_local, id_global, dateCreated, " +
                    "genealogy, imageCountCache, includeOnExport, includeParents, includeSynonyms, " +
                    "keywordType, lastApplied, lc_name, name, parent) " +
                    "VALUES (" +
                    "" + idlocal + " , " +
                    "'" + UUID.randomUUID() + "', " +
                    "'608509497.846982', " +
                    "'" + idforKeywordMaitre.get("genealogy") + "/8" + idlocal + "', " +
                    "'', " +
                    "'1', " +
                    "'1', " +
                    "'1', " +
                    "'', " +
                    "'', " +
                    "'" + keyword.toLowerCase() + "', " +
                    "'" + keyword + "', " +
                    "'" + idforKeywordMaitre.get("KeyWordIdlocal") + "' " +
                    ")" +
                    ";";
            executeUpdate(sql);
            ret.put("keyWordIdlocal", String.valueOf(idlocal));
            ret.put("NewkeyWordIdlocal", String.valueOf(Boolean.TRUE));
            return ret;
        }
        ret.put("keyWordIdlocal", keyWordIdlocal);
        ret.put("NewkeyWordIdlocal", String.valueOf(Boolean.FALSE));
        return ret;
    }

    private Map<String, String> getIdforKeyword(String keyword) throws SQLException {
        Map<String, String> ret = new HashMap<>();
        String sql;
        if (keyword.compareTo("") == 0) {
            sql = "select " +
                    "id_local , " +
                    "genealogy " +
                    "from  AgLibraryKeyword " +
                    "where lc_name is null ";
        } else {
            sql = "select " +
                    "id_local , " +
                    "genealogy " +
                    "from  AgLibraryKeyword " +
                    "where lc_name = '" + keyword.toLowerCase() + "' ";
        }

        ResultSet rs = this.select(sql);

        ret.put("genealogy", "");
        ret.put("KeyWordIdlocal", "");
        while (rs.next()) {
            ret.replace("genealogy", rs.getString("genealogy"));
            ret.replace("KeyWordIdlocal", rs.getString("id_local"));
        }
        return ret;
    }

    public long sqlGetPrevIdlocalforKeyword() throws SQLException {
        String sql = "select * FROM AgLibraryKeyword " +
                "ORDER by id_local desc " +
                "; ";
        ResultSet rs = select(sql);
        boolean first = true;
        long idLocalCalcul = 0;
        long idLocal = 0;
        while (rs.next()) {
            // Recuperer les info de l'elements
            idLocal = rs.getLong("id_local");
            if (first) {
                idLocalCalcul = idLocal;
                first = false;
            } else {
                idLocalCalcul -= 1;
                if (idLocalCalcul > idLocal) {
                    return idLocalCalcul;
                }
            }
        }
        // 0 ou 1 repertoire dans AgLibraryFolder
        if (idLocalCalcul == idLocal) {
            idLocalCalcul = (idLocal / 2) + 1;
        }
        return idLocalCalcul;
    }

    public long sqlGetPrevIdlocalforKeywordImage() throws SQLException {
        String sql = "select * FROM AgLibraryKeywordImage " +
                "ORDER by id_local desc " +
                "; ";
        ResultSet rs = select(sql);
        boolean first = true;
        long idLocalCalcul = 0;
        long idLocal = 0;
        while (rs.next()) {
            // Recuperer les info de l'elements
            idLocal = rs.getLong("id_local");
            if (first) {
                idLocalCalcul = idLocal;
                first = false;
            } else {
                idLocalCalcul -= 1;
                if (idLocalCalcul > idLocal) {
                    return idLocalCalcul;
                }
            }
        }
        // 0 ou 1 repertoire dans AgLibraryFolder
        if (idLocalCalcul == idLocal) {
            idLocalCalcul = (idLocal / 2) + 1;
        }
        return idLocalCalcul;
    }


    public int purgeGroupeKeyword(String tagorg, List<String> lstIdKey) throws SQLException {
        String sepa = " , ";
        String array = "";
        for (int i = 0; i < lstIdKey.size(); i++) {
            array += lstIdKey.get(i) + sepa;
        }
        if (lstIdKey.size() > 0) {
            array = array.substring(0, array.length() - sepa.length());
        }

        String sql = " delete " +
                " from AgLibraryKeyword  " +
                " WHERE id_local in ( " +
                " SELECT A.id_local " +
                " from AgLibraryKeyword A " +
                " inner join AgLibraryKeyword B " +
                " on A.genealogy like B.genealogy || '%' " +
                " where b.name = '" + tagorg + "' " +
                " and a.id_local not in ( " +
                " " + array + " " +
                " ) " +
                " ) " +
                " ; ";
        return executeUpdate(sql);
    }

    public String keywordImageWithoutKeyword(JProgressBar progress) throws SQLException {
        String sql = " select k.id_local " +
                "from AgLibraryKeywordImage k " +
                "left join AgLibraryKeyword i " +
                " on k.tag = i.id_local " +
                "where lc_name is NULL " +
                ";";
        ResultSet rs = select(sql);
        int ko = 0;
        while (rs.next()) {
            ko++;
        }
        int koCor = 0;
        if (ko > 0) {
            koCor += sqlDeletekeywordImage();
        }
        String txtret = "";
        txtret += " nb keyword orphelin    = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    private int sqlDeletekeywordImage() throws SQLException {
        String sql = " delete " +
                "from AgLibraryKeywordImage  " +
                " where id_local in ( " +
                "select k.id_local as result " +
                "from AgLibraryKeywordImage k " +
                "left join AgLibraryKeyword i " +
                " on k.tag = i.id_local " +
                "where name is NULL " +
                ") " +
                " ; ";
        return executeUpdate(sql);
    }

    public HashMap<String, String> getFolderCollection(String collections, String tagorg, String filtreDebutNomRep) throws SQLException {
        HashMap<String, String> ret = new HashMap<>();
        String sql = " select * " +
                "from AgLibraryFolder " +
                "where pathFromRoot REGEXP  '" + collections + "\\/" + filtreDebutNomRep + "[@&#a-zA-Z _0-9-]*\\/$' " +
                ";";
        ResultSet rs = select(sql);
        while (rs.next()) {
            String pathFromRoot = rs.getString("pathFromRoot");
            String[] split = pathFromRoot.split("/");
            String tag = split[split.length - 1] + tagorg;
            ret.put(tag, pathFromRoot);
        }
        return ret;
    }

    public Map<String, Map<String, String>> sqllistAllFileWithTagtoRep(String tag, String destPath) throws SQLException, IOException {
        Map<String, Map<String, String>> ret2 = new HashMap<>();
        String sql = "select f.id_local as id_local , " +
                "p.absolutePath as absolutePath , " +
                "b.pathFromRoot as pathFromRoot , " +
                "f.lc_idx_filename as lcIdxFilename , " +
                "ki.id_local as ki_id_local " +
                "from AgLibraryKeyword k , " +
                "AgLibraryKeywordImage ki , " +
                "Adobe_images e , " +
                "AgLibraryFile f , " +
                "AgLibraryFolder b  , " +
                "AgLibraryRootFolder p " +
                "where k.name = '" + tag + "' " +
                "and k.id_local = ki.tag " +
                "and ki.image = e.id_local " +
                "and e.rootFile = f.id_local " +
                "and f.folder = b.id_local  " +
                "and b.rootFolder = p.id_local  " +
                ";";
        ResultSet rs = select(sql);
        while (rs.next()) {
            Map<String, String> ret = new HashMap<>();
            String fileIdLocal = rs.getString("id_local");
            String absolutePath = rs.getString("absolutePath");
            String pathFromRoot = rs.getString("pathFromRoot");
            String lcIdxFilename = rs.getString("lcIdxFilename");
            String kiIdLocal = rs.getString("ki_id_local");
            String oldPath = normalizePath(absolutePath + pathFromRoot + lcIdxFilename);
            String newPath = normalizePath(destPath + File.separator + lcIdxFilename);
            ret.put("oldPath", oldPath);
            ret.put("newPath", newPath);
            ret.put("kiIdLocal", kiIdLocal);
            ret2.put(fileIdLocal, ret);
        }
        return ret2;
    }

    public Map<String, Map<String, String>> getFileForGoTag(String tag) throws SQLException, IOException {
        Map<String, Map<String, String>> ret2 = new HashMap<>();
        String sql = " select f.id_local as id_local , p.absolutePath as absolutePath , b.pathFromRoot as pathFromRoot , f.lc_idx_filename as lcIdxFilename , " +
                " ki.id_local as ki_id_local " +
                " from AgLibraryKeyword k , AgLibraryKeywordImage ki , Adobe_images e , AgLibraryFile f , AgLibraryFolder b  , AgLibraryRootFolder p " +
                " where k.name = '" + tag + "' " +
                " and k.id_local = ki.tag " +
                " and ki.image = e.id_local " +
                " and e.rootFile = f.id_local " +
                " and f.folder = b.id_local " +
                " and b.rootFolder = p.id_local " +
                " ;";
        ResultSet rs = select(sql);
        while (rs.next()) {
            Map<String, String> ret = new HashMap<>();
            String fileIdLocal = rs.getString("id_local");
            String absolutePath = rs.getString("absolutePath");
            String pathFromRoot = rs.getString("pathFromRoot");
            String lcIdxFilename = rs.getString("lcIdxFilename");
            String kiIdLocal = rs.getString("ki_id_local");
            String oldPath = normalizePath(absolutePath + pathFromRoot + File.separator + lcIdxFilename);
            ret.put("oldPath", oldPath);
            ret.put("kiIdLocal", kiIdLocal);
            ret2.put(fileIdLocal, ret);
        }
        return ret2;
    }

    public Map<String, String> getNewPathForGoTagandFileIdlocal(String generiquetag, String fileIdLocal) throws SQLException, IOException {
        Map<String, String> ret = new HashMap<>();
        String sql = "select DISTINCT p2.absolutePath as absolutePath , b2.pathFromRoot as pathFromRoot , f.lc_idx_filename as lcIdxFilename , " +
                " ki.id_local as ki_id_local " +
                " from AgLibraryFile f " +
                " inner join AgLibraryFolder b " +
                " on f.folder = b.id_local " +
                " inner join Adobe_images e " +
                " on e.rootFile = f.id_local " +
                " inner join AgLibraryKeywordImage ki " +
                " on e.id_local = ki.image " +
                " inner join AgLibraryKeyword k " +
                " on ki.tag = k.id_local " +
                " inner join AgLibraryKeywordImage ki2 " +
                " on Ki2.tag = k.id_local " +
                " inner join Adobe_images e2 " +
                " on e2.id_local = ki2.image " +
                " inner join AgLibraryFile f2 " +
                " on e2.rootFile = f2.id_local " +
                " inner join AgLibraryFolder b2 " +
                " on f2.folder = b2.id_local " +
                " inner join AgLibraryRootFolder p2 " +
                " on b2.rootFolder = p2.id_local " +
                "  where f.id_local = " + fileIdLocal + " " +
                "  and k.name like '" + generiquetag + "%' " +
                "  and b.pathFromRoot <> b2.pathFromRoot " +
                ";";
        ResultSet rs = select(sql);
        String newPath = "";
        while (rs.next()) {
            String absolutePath = rs.getString("absolutePath");
            String pathFromRoot = rs.getString("pathFromRoot");
            String lcIdxFilename = rs.getString("lcIdxFilename");
            String kiIdLocal = rs.getString("ki_id_local");
            newPath = normalizePath(absolutePath + pathFromRoot + File.separator + lcIdxFilename);
            ret.put("newPath", newPath);
            ret.put("kiIdLocal", kiIdLocal);
        }
        return ret;
    }

    public int removeKeywordImages(String kiIdLocal) throws SQLException {
        String sql = " delete " +
                "from AgLibraryKeywordImage  " +
                " where id_local = " + kiIdLocal + " " +
                " ; ";
        return executeUpdate(sql);
    }

    public int moveRepertory(String source, String destination) throws SQLException {
        Map<String, String> src = getIdlocalforRep(source);
        String dest = normalizePath(normalizePath(destination + File.separator).replace(normalizePath(src.get("absolutePath")), ""));

        String sql;
        sql = "" +
                "update AgLibraryFolder " +
                "set pathFromRoot = " +
                "replace( pathFromRoot, '" + src.get("pathFromRoot") + "' , '" + dest + "' ) " +
                "where id_local = '" + src.get("Folderidlocal") + "' " +
                ";";
//        return 1;
        return executeUpdate(sql);
    }

    public List<String> getFourRandomPreviewPhoto(String repertoire) throws SQLException, IOException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);

        List<String> listTmp = new ArrayList<>();

        Map<String, String> ret = new HashMap<>();
        String sql = "SELECT random() as TRI , * " +
                "FROM  AgLibraryFile " +
                "WHERE folder = '" + idLocalRep.get("Folderidlocal") + "'" +
                "and extension = '" + Context.JPG + "' " +
                "order by TRI " +
                ";";
        ResultSet rs = select(sql);
        int nb = 1;
        while (rs.next() && nb <= 4) {
            String absolutePath = idLocalRep.get("absolutePath");
            String pathFromRoot = idLocalRep.get("pathFromRoot");
            String lcIdxFilename = rs.getString("lc_idx_filename");
            listTmp.add(normalizePath(absolutePath + pathFromRoot + File.separator + lcIdxFilename));
            nb++;
        }


        return listTmp;
    }

    public List<String> getLstPhoto(String repertoire) throws SQLException, IOException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);

        List<String> listTmp = new ArrayList<>();

        Map<String, String> ret = new HashMap<>();
        String sql = "SELECT * " +
                "FROM  AgLibraryFile a " +
                "inner join Adobe_images e  " +
                " on a.id_local = e.rootFile    " +
                "WHERE a.folder = '" + idLocalRep.get("Folderidlocal") + "'" +
                "and a.extension = '" + Context.JPG + "' " +
                "order by e.captureTime asc " +
                ";";
        ResultSet rs = select(sql);
        while (rs.next()) {
            String absolutePath = idLocalRep.get("absolutePath");
            String pathFromRoot = idLocalRep.get("pathFromRoot");
            String lcIdxFilename = rs.getString("lc_idx_filename");
            listTmp.add(normalizePath(absolutePath + pathFromRoot + File.separator + lcIdxFilename));
        }
        return listTmp;
    }

    public List<String> getlistPhotoFlagRejeter() throws SQLException {
        List<String> listTmp = new ArrayList<>();

        String sql = "select p.absolutePath || b.pathFromRoot || a.lc_idx_filename as filepath" +
                " from Adobe_images e " +
                " inner join AgLibraryFile a " +
                "  on a.id_local = e.rootFile " +
                " inner join AgLibraryFolder b " +
                "  on a.folder = b.id_local  " +
                " inner join AgLibraryRootFolder p " +
                "  on b.rootFolder = p.id_local " +
                " where e.pick < 0 " +
                ";";
        ResultSet rs = select(sql);
        while (rs.next()) {
            listTmp.add(normalizePath(rs.getString("filepath")));
        }
        return listTmp;
    }

    public void visuProgress(JProgressBar progress, String txtPr, int numRow, int numRowMax) {
        progress.setMaximum(numRowMax);
        progress.setValue(numRow);
        progress.setString(txtPr + " - " + new DecimalFormat("#.##").format(numRow*100/numRowMax) + "%");
    }

    public String retWhereIAm(String methodName) {
        String ret = methodName;
        int length = methodName.length();
        if (length < 130) {
            int lngMid = (130 - length) / 2;
            int lngComp = 130 - (length + lngMid + lngMid);
            ret = StringUtils.repeat("-", lngMid) + methodName + StringUtils.repeat(" ", lngComp) + StringUtils.repeat("-", lngMid);
        }
        return ret;
    }

    int getQueryRowCount(String query) throws SQLException {
        ResultSet standardRS = select(query);
        int size = 0;
        while (standardRS.next()) {
            size++;
        }
        standardRS.close();
        return size;
    }
    public int getQueryRowCount(ResultSet standardRS) throws SQLException {
        //ResultSet standardRS = select(query);
        int size = 0;
        while (standardRS.next()) {
            size++;
        }
        standardRS.first();
        return size;
    }
}

