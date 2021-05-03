package com.malicia.mrg.model;

import com.malicia.mrg.Context;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.util.SQLiteJDBCDriverConnection;
import com.malicia.mrg.util.SystemFiles;

import com.malicia.mrg.util.WhereIAm;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public void AddKeywordToFile(String fileIdLocal, String tag) throws SQLException {
        String idlocaltag = sqlcreateKeyword(Context.TAGORG, tag).get("keyWordIdlocal");
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

    public void AddKeywordToRep(String repertoire, String tag) throws SQLException {
        //todo
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '" + tag + "' " +
                " where rootFile in ( " +
                " select a.id_local as file_id_local " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b   " +
                " on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder p   " +
                " on b.rootFolder = p.id_local  " +
                "where '" + SystemFiles.normalizePath(repertoire) + "' like p.absolutePath || '%'  " +
                "and p.absolutePath || b.pathFromRoot like '" + SystemFiles.normalizePath(repertoire) + "' || '%' " +
                " ) " +
                ";";
        executeUpdate(sql);
    }

    public int topperARed50NEW(String repertoire50NEW) throws SQLException {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '" + Context.RED + "' " +
                " where colorLabels <> '" + Context.RED + "' " +
                " and rootFile in ( " +
                getStringAllIdLocalFromNew(repertoire50NEW)  +
                " ) " +
                ";";
        return executeUpdate(sql);

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

    public int deTopperARedOld50NEW(String repertoire50NEW) throws SQLException {
        String sql;
        sql = "update Adobe_images " +
                "set colorLabels = '' " +
                " where colorLabels = '" + Context.RED + "' " +
                " and rootFile not in ( " +
                getStringAllIdLocalFromNew(repertoire50NEW)  +
                " ) "+
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
        if (idlocal > 0) {
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
                    " lc_idx_filenameExtension =  '" + ext.toLowerCase() + "'  " +
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
        }
        return ret;
    }

    public Map<String, Integer> getStarValue(String repertoire) throws SQLException {
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
                        " group by e.rating " +
                        ";");

        while (rsexist.next()) {
            String rating = String.valueOf(rsexist.getLong("rating")) == null ? "0" : String.valueOf(rsexist.getLong("rating"));
            boolean res = idlocal.replace(rating, idlocal.get(rating), idlocal.get(rating) + rsexist.getInt("result"));
        }
        return idlocal;
    }

    public double nbjourfolder(String repertoire) throws SQLException {
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

    public int nbPick(String repertoire) throws SQLException {
        Map<String, String> idLocalRep = getIdlocalforRep(repertoire);
        ResultSet rsexist = select(
                " select  count(*) as result" +
                        " from AgLibraryFile a  " +
                        " inner join Adobe_images e " +
                        " on a.id_local = e.rootFile " +
                        " where " + idLocalRep.get("Folderidlocal") + " = a.folder " +
                        " and e.pick >= 0 " +
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


    public String pathAbsentPhysique() throws SQLException {
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
        while (rs.next()) {
            File filepath = new File(rs.getString("absolutePath") + rs.getString("pathFromRoot") + rs.getString("lc_idx_filename"));
            nb += 1;
            if (!filepath.exists()) {
                txtret += "ko = " + "file_id_local" + "(" + rs.getString("file_id_local") + ")" + filepath.toString() + " ";
                ko += 1;
                koCor += sqlDeleteFile(rs.getString("file_id_local"));
            }

        }
        txtret += " nb path logique = " + nb + " : absent physique = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String folderAbsentPhysique() throws SQLException {
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
        while (rs.next()) {
            File filepath = new File(rs.getString("absolutePath") + rs.getString("pathFromRoot"));
            nb += 1;
            if (!filepath.exists()) {
                txtret += "ko = " + "folder_id_local" + "(" + rs.getString("folder_id_local") + ")" + filepath.toString() + "\n";
                ko += 1;
                koCor += sqlDeleteRepertory(rs.getString("folder_id_local"));
            }

        }
        txtret += " nb folder logique = " + nb + " : absent physique = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String fileWithoutFolder() throws SQLException {
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
        while (rs.next()) {
            txtret += "ko = " + "file_id_local" + "(" + rs.getString("file_id_local") + ")" + " lc_idx_filename => " + rs.getString("lc_idx_filename") + "\n";
            ko += 1;
            koCor += sqlDeleteFile(rs.getString("file_id_local"));
        }
        txtret += " nb file without Folder = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String AdobeImagesWithoutLibraryFile() throws SQLException {
        String sql = "SELECT e.id_local  from Adobe_images e " +
                "left join AgLibraryFile f " +
                "on f.id_local = e.rootFile " +
                "where lc_idx_filename is NULL" +
                ";";
        ResultSet rs = select(sql);
        String txtret = "";
        int ko = 0;
        int koCor = 0;
        while (rs.next()) {
            txtret += "ko = " + "rootFile" + "(" + rs.getString("id_local") + ")" + "\n";
            ko += 1;
            koCor += sqlDeleteAdobe_images(rs.getString("id_local"));
        }
        txtret += " nb Images Without File = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String KeywordImageWithoutImages() throws SQLException {
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
        while (rs.next()) {
            txtret += "ko = " + "KeywordImage" + "(" + rs.getString("id_local") + ")" + "\n";
            ko += 1;
            koCor += removeKeywordImages(rs.getString("id_local"));
        }
        txtret += " nb KeyImg Without Img  = " + ko + "\n";
        txtret += "    --- corrige         = " + koCor + "\n";
        return txtret;
    }

    public String folderWithoutRoot() throws SQLException {
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
        while (rs.next()) {
            txtret += "ko = " + "folder_id_local" + "(" + rs.getString("folder_id_local") + ")" + " pathFromRoot => " + rs.getString("pathFromRoot") + "\n";
            ko += 1;
            koCor += sqlDeleteRepertory(rs.getString("folder_id_local"));
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

    public  Map<String, String> sqlcreateKeyword(String keywordmaitre, String keyword) throws SQLException {
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
                    "'" + UUID.randomUUID().toString() + "', " +
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
            ret.put("keyWordIdlocal",String.valueOf(idlocal));
            ret.put("NewkeyWordIdlocal",String.valueOf(Boolean.TRUE));
            return ret;
        }
        ret.put("keyWordIdlocal",keyWordIdlocal);
        ret.put("NewkeyWordIdlocal",String.valueOf(Boolean.FALSE));
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
        for(int i = 0; i < lstIdKey.size(); i++) {
            array += lstIdKey.get(i) + sepa;
        }
        if (lstIdKey.size()>0) {
            array = array.substring(0,array.length() - sepa.length());
        }

        String sql = " delete " +
                "from AgLibraryKeyword  " +
                " where name like '" + tagorg + "%' " +
                " and name <> '" + tagorg + "' " +
                " and id_local not in ( " +
                " " + array + " " +
                " ) " +
                " ; ";
        return executeUpdate(sql);
    }

    public String keywordImageWithoutKeyword() throws SQLException {
        String sql = " select k.id_local " +
                "from AgLibraryKeywordImage k " +
                "left join AgLibraryKeyword i " +
                " on k.tag = i.id_local " +
                "where lc_name is NULL " +
                ";";
        ResultSet rs = select(sql);
        int ko = 0;
        while (rs.next()) {
            ko ++;
        }
        int koCor = 0;
        if (ko > 0 ) {
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

    public HashMap<String, String> getFolderCollection(String collections, String tagorg) throws SQLException {
        HashMap<String, String> ret = new HashMap<>();
        String sql = " select * " +
                "from AgLibraryFolder " +
                "where pathFromRoot REGEXP  '\\/"+collections+"\\/[@&#a-zA-Z 0-9-]*\\/$' " +
                ";";
        ResultSet rs = select(sql);
        while (rs.next()) {
            String pathFromRoot = rs.getString("pathFromRoot");
            String[] split = pathFromRoot.split("/");
            String tag = split[split.length-1] + tagorg;
            ret.put(tag, pathFromRoot);
        }
        return ret;
    }

    public Map<String, Map<String, String>> sqlmoveAllFileWithTagtoRep(String tag, String destPath) throws SQLException, IOException {
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
                " where k.name = '" + tag +"' " +
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
                "  and k.name like '"+ generiquetag +"%' " +
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
            ret.put("newPath",newPath);
            ret.put("kiIdLocal",kiIdLocal);
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
}

