package com.malicia.mrg.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;


/**
 * The type Actionfichier repertoire.
 */
public class SystemFiles {

    private static final Logger LOGGER = LogManager.getLogger(SystemFiles.class);

    private SystemFiles() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Delete dir boolean.
     *
     * @param dir the dir
     * @return the boolean
     * @throws IOException the io exception
     */
    public static void deleteDir(File dir) throws IOException {
        Files.delete(dir.toPath());
        LOGGER.log(Level.DEBUG, "delete_dir: {0} ", dir);
    }


    /**
     * Normalize path string.
     *
     * @param path the path
     * @return the string
     */
    public static String normalizePath(String path) {
        return path.replace("\\", "/").replace("//", "/");
    }

    /**
     * Mkdir.
     *
     * @param directoryName the directory name
     * @throws SQLException the sql exception
     */
    public static void mkdir(String directoryName) {
        File fdirectoryName = new File(directoryName);
        if (!fdirectoryName.exists()) {
            fdirectoryName.mkdir();
        }
    }

    /**
     * Move file.
     *
     * @param source      the source
     * @param destination the destination
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public static void moveFile(String source, String destination) throws IOException {
        File fsource = new File(source);
        File fdest = new File(destination);
        if (fsource.compareTo(fdest) != 0) {
            if ((!fsource.exists() || !fsource.isFile())&&!fdest.exists() ) {
                    throw new IllegalStateException("non existance : " + fsource.toString());
            }
            if (fdest.exists() && fsource.exists()) {
                    throw new IllegalStateException("existance     : " + fdest.toString());
            }
            if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                LOGGER.debug(() -> "move_file p=" + fsource.toString() + " -> " + fdest.toString());
                Path success = Files.move(fsource.toPath(), fdest.toPath());
                if (!Files.exists(success)) {
                    // File was not successfully renamed
                    throw new java.io.IOException("file was not successfully renamed");
                }
            }
        }
    }

    /**
     * Move file.
     *
     * @param source      the source
     * @param destination the destination
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public static void moveRepertory(String source, String destination) throws IOException {
        File fsource = new File(source);
        File fdest = new File(destination);
        if (fsource.compareTo(fdest) != 0) {
            if (!fsource.exists() || !fsource.isDirectory()) {
                throw new IllegalStateException("non existance : " + fsource.toString());
            }
            if (fdest.exists()) {
                throw new IllegalStateException("existance     : " + fdest.toString());
            }
            LOGGER.debug(() -> "move_file p=" + fsource.toString() + " -> " + fdest.toString());
            Files.move(fsource.toPath(), fdest.toPath());
        }
    }

}
