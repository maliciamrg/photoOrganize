package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Tools {

    static int moveFile(String source, String newPath, IPhysicalSystem iPhysicalSystem, ILogicalSystem iLogicalSystem) throws IOException, SQLException {
        int ret = 0;
        sqlMkdirRepertoryPhyAndLog(iPhysicalSystem.getFilegetParent(newPath) + iPhysicalSystem.getFileseparator(),iPhysicalSystem,iLogicalSystem);

        ret = iPhysicalSystem.movePhysique(source, newPath);

        long scrFileIdLocal = iLogicalSystem.getIdlocalforFilePath(source).get("idlocal");
        if (scrFileIdLocal > 0) {
            ret = iLogicalSystem.sqlmovefile(scrFileIdLocal, newPath);
            //move sidecar also
            String[] extensionArray = iLogicalSystem.sqlGetSidecarExtensions(scrFileIdLocal).split(",");
            for (String newExtension : extensionArray) {
                String modifiedSource = modifyPath(source, newExtension);
                String modifiedNewPath = modifyPath(newPath, newExtension);
                iPhysicalSystem.movePhysique(modifiedSource, modifiedNewPath);
            }
        }
        return ret;
    }
    private static void sqlMkdirRepertoryPhyAndLog(String directoryName,IPhysicalSystem iPhysicalSystem, ILogicalSystem iLogicalSystem) throws SQLException {

        File fdirectoryName = new File(directoryName);
        if (!fdirectoryName.exists()) {
            iPhysicalSystem.mkdir(directoryName);
            if (iLogicalSystem == null) {
                iLogicalSystem.makeRepertory(directoryName);
            }
        }
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
}
