package com.malicia.mrg.model;

import com.malicia.mrg.param.importJson.ControleRepertoire;
import com.malicia.mrg.util.SQLiteJDBCDriverConnection;
import com.malicia.mrg.util.SystemFiles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.malicia.mrg.util.SystemFiles.normalizePath;

public class Database extends SQLiteJDBCDriverConnection {

    private Database(String catalogLrcat) {
        super(catalogLrcat);
    }

    public static Database chargeDatabaseLR(String catalogLrcat) {
        return new Database(catalogLrcat);
    }

    public void makeRepertory(String directoryName) throws SQLException {

        Map<String, String> idlocalforRep = getIdlocalforRep(new File(directoryName).getParent());
        String rootFolder = idlocalforRep.get("rootFolder");

        String pathFromRoot = normalizePath(normalizePath(directoryName).replace(normalizePath(idlocalforRep.get("absolutePath")),""));

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

    public void sqlmovefile(elementFichier source, String destination) throws IOException, SQLException {

        File fdest = new File(destination);

        Map<String, String> dst = getIdlocalforRep(fdest.getParent());

        String sql;
        sql = "" +
                "update AgLibraryFile " +
                "set folder =  " + dst.get("Folderidlocal") + " ," +
                " baseName =  '" + FilenameUtils.getBaseName(destination) + "' , " +
                " idx_filename =  '" + fdest.getName() + "' , " +
                " lc_idx_filename =  '" + fdest.getName().toLowerCase() + "'  " +
                "where id_local =  " + source.getFile_id_local() + " " +
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
        long id_Local = getIdlocalforFilePath(oldName).get("idlocal");
        if (id_Local > 0) {
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
                    "where id_local =  " + id_Local + " " +
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
        while (rsexist.next()) {
            ret.put("idlocal", rsexist.getLong("result"));
            ret.put("rootFolder",rsexist.getLong("rootFolder"));
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
            ret.put("rootFolder",rsexist.getString("rootFolder"));
            ret.put("absolutePath",rsexist.getString("absolutePath"));
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

    public long nbjourfolder(String repertoire) throws SQLException {
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
            captureTimeMin = new DateTime (rsexist.getLong("captureTimeMin") * 1000);
            captureTimeMax = new DateTime (rsexist.getLong("captureTimeMax") * 1000);
        }
        int days = Days.daysBetween(captureTimeMin, captureTimeMax).getDays()+1;
        return days;
    }

    public int nb_pick(String repertoire) throws SQLException {
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

    public Boolean isValueInTag(String getoValue, String tagAction) throws SQLException {
        ObservableList<String> listTag = getValueForTag(tagAction);
        return listTag.contains(getoValue);
    }

    public ObservableList<String> getValueForTag(String getcChamp) throws SQLException {
        ObservableList<String> listLcName = FXCollections.observableArrayList();
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
     * @param tempsAdherence the temps adherence
     * @param repertoire
     * @return the result set
     * @throws SQLException the sql exception
     */
    public ResultSet sqlgetListelementnewaclasser(String tempsAdherence, String repertoire) throws SQLException {
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
                        "strftime('%s', e.captureTime) as captureTime " +
                        "from AgLibraryFile a  " +
                        "inner join AgLibraryFolder b   " +
                        " on a.folder = b.id_local  " +
                        "inner join AgLibraryRootFolder p   " +
                        " on b.rootFolder = p.id_local  " +
                        "inner join Adobe_images e  " +
                        " on a.id_local = e.rootFile    " +
                        "LEFT JOIN AgHarvestedExifMetadata ahem " +
                        "ON e.id_local = ahem.image " +
                        "LEFT JOIN AgInternedExifCameraModel aiecm " +
                        "ON ahem.cameraModelRef = aiecm.id_local " +
                        "where '" + SystemFiles.normalizePath(repertoire) + "' like p.absolutePath || '%'  " +
                        "and p.absolutePath || b.pathFromRoot like '" + SystemFiles.normalizePath(repertoire) + "' || '%' " +
                        "order by captureTime asc " +
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
                    txtret += "ko = " + "file_id_local" + "(" + rs.getString("file_id_local") + ")" +  filepath.toString() + "\n";
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
            File filepath = new File(rs.getString("absolutePath") + rs.getString("pathFromRoot") );
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

    private int sqlDeleteRepertory(String folder_id_local) throws SQLException {
        String sql = " delete " +
                "from AgLibraryFolder  " +
                " where id_local = '" + folder_id_local + "' " +
                " ; ";
        return executeUpdate(sql);
    }
    private int sqlDeleteFile(String file_id_local) throws SQLException {
        String sql = " delete " +
                "from AgLibraryFile  " +
                " where id_local = '" + file_id_local + "' " +
                " ; ";
        return executeUpdate(sql);
    }
}

