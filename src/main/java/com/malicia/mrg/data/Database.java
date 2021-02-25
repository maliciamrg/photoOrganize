package com.malicia.mrg.data;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Database extends SQLiteJDBCDriverConnection {

    private Database(String catalogLrcat) {
        super(catalogLrcat);
    }

    public static Database chargeDatabaseLR(String catalogLrcat) {
        return new Database(catalogLrcat);
    }

    public static String normalizePath(String path) {
        return path.replace("\\", "/");
    }

    public void renameFileLogique(String oldName, String newName) throws SQLException {
        long id_Local = getIdlocalforFilePath(oldName);
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

    public void renommerRepertoireLogique(String repertoire, String newRepertoire) throws SQLException {
//        String sql;
//        sql = "" +
//                "update AgLibraryFolder " +
//                "set pathFromRoot = " +
//                "replace( pathFromRoot, '" + pathFromRootsrc + "' , '" + pathFromRootdest + "' ) , " +
//                " rootFolder = " + rootFolderdest + " " +
//                "where pathFromRoot like '" + pathFromRootsrc + "%' " +
//                " and rootFolder = " + rootFoldersrc + "" +
//                ";";
//        executeUpdate(sql);
    }

    public long getIdlocalforFilePath(String path) throws SQLException {
        File fpath = new File(path);
        String baseName = FilenameUtils.getBaseName(path);
        String ext = FilenameUtils.getExtension(path);
        ResultSet rsexist = select(
                "select p.absolutePath ,  p.id_local , fo.rootFolder , fo.pathFromRoot , fo.id_local , fi.folder , fi.originalFilename , fi.id_local as result  " +
                        "from AgLibraryRootFolder as p , " +
                        "AgLibraryFolder as fo , " +
                        "AgLibraryFile as fi " +
                        "where '" + normalizePath(fpath.getParent()) + "' like p.absolutePath || '%'  " +
                        "and fo.rootFolder = p.id_local " +
                        "and '" + normalizePath(fpath.getParent() + File.separator) + "' = p.absolutePath || fo.pathFromRoot  " +
                        "and fi.folder = fo.id_local " +
                        "and fi.baseName =  '" + baseName + "' " +
                        "and fi.extension =  '" + ext + "'   " +
                        ";");
        long idlocal = 0;
        while (rsexist.next()) {
            idlocal = rsexist.getLong("result");
        }
        return idlocal;
    }


    public long getIdlocalforRep(String repertoire) throws SQLException {
        ResultSet rsexist = select(
                "select p.absolutePath ,  p.id_local , fo.rootFolder , fo.pathFromRoot , fo.id_local as result  " +
                        "from AgLibraryRootFolder as p , " +
                        "AgLibraryFolder as fo " +
                        "where '" + normalizePath(repertoire) + "' like p.absolutePath || '%'  " +
                        "and fo.rootFolder = p.id_local " +
                        "and '" + normalizePath(repertoire + File.separator) + "' = p.absolutePath || fo.pathFromRoot  " +
                        ";");
        long idlocal = 0;
        while (rsexist.next()) {
            idlocal = rsexist.getLong("result");
        }
        return idlocal;
    }

    public  Map<String, Integer> getStarValue(long idLocalRep) throws SQLException {
        Map<String, Integer> idlocal = new HashMap<>();
        idlocal.put("0",0);
        idlocal.put("1",0);
        idlocal.put("2",0);
        idlocal.put("3",0);
        idlocal.put("4",0);
        idlocal.put("5",0);

        ResultSet rsexist = select(
                "select " +
                        "e.rating , " +
                        "count(*) as result " +
                        "from AgLibraryFile a  " +
                        "inner join Adobe_images e  " +
                        " on a.id_local = e.rootFile    " +
                        " where " + idLocalRep + " = a.folder  " +
                        " group by e.rating " +
                        ";");

        while (rsexist.next()) {
            String rating = rsexist.getString("rating")==null?"0":rsexist.getString("rating");
            boolean res = idlocal.replace(rating, idlocal.get(rating), idlocal.get(rating) + rsexist.getInt("result"));
        }
        return idlocal;
    }
}
