package com.malicia.mrg;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.util.SystemFiles;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static com.malicia.mrg.util.SystemFiles.normalizePath;

public class Function {
    public static int moveFile(String source, String newPath, Database dbLr) throws IOException, SQLException {
        int ret = 0;
        sqlMkdirRepertoryPhyAndLog(new File(newPath).getParent() + File.separator, dbLr);

        ret = movePhysique(source, newPath);

        long scrFileIdLocal = dbLr.getIdlocalforFilePath(source).get("idlocal");
        if (scrFileIdLocal > 0) {
            ret = dbLr.sqlmovefile(scrFileIdLocal, newPath);
            //move sidecar also
            String[] extensionArray = dbLr.sqlGetSidecarExtensions(scrFileIdLocal).split(",");
            for (String newExtension : extensionArray) {
                String modifiedSource = modifyPath(source, newExtension);
                String modifiedNewPath = modifyPath(newPath, newExtension);
                movePhysique(modifiedSource, modifiedNewPath);
            }
        }
        return ret;
    }

    private static int movePhysique(String source, String newPath) throws IOException {
        if (normalizePath(source).compareTo(normalizePath(newPath)) != 0) {
            File fsource = new File(source);
            File fdest = new File(newPath);
            if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                SystemFiles.moveFile(source, newPath);
                return 1;
            }
        }
        return 0;
    }

    private static String modifyPath(String originalPath, String newExtension) {
        int lastSeparatorIndex = originalPath.lastIndexOf(File.separator);
        int lastDotIndex = originalPath.lastIndexOf('.');

        // Extract path, filename, and current extension
        String path = originalPath.substring(0, lastSeparatorIndex + 1);
        String fileName = originalPath.substring(lastSeparatorIndex + 1, lastDotIndex);
        String currentExtension = originalPath.substring(lastDotIndex + 1);

        // Replace the current extension with the new extension
        String modifiedFileName = fileName + "." + newExtension;

        // Construct the modified path
        String modifiedPath = path + modifiedFileName;

        return modifiedPath;
    }

    public static String modifyPathIfExistWithRandom(String newPath) {
        File fdest = new File(newPath);
        if (fdest.exists()) {
            newPath = fdest.getParent() + "\\" + Math.floor(Math.random() * (100 - 0 + 1) + 0) + fdest.getName();
        }
        return newPath;
    }

    private static void sqlMkdirRepertoryPhyAndLog(String directoryName, Database dbLr) throws SQLException {

        File fdirectoryName = new File(directoryName);
        if (!fdirectoryName.exists()) {
            SystemFiles.mkdir(directoryName);
            if (dbLr == null) {
                dbLr.makeRepertory(directoryName);
            }
        }
    }
}
