package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.ddd.DomainService;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@DomainService
public class PocImpl implements IPoc {

    public static final String FOLDERDELIM = "\\";
    public static final long GMT01JAN200112AM = 978307200;
    private static final Logger LOGGER = LogManager.getLogger("loggerToSyncPhFile");
    private final ILogicalSystem logicalSystem;
    private final IPhysicalSystem physicalSystem;
    private final IParams params;

    private List<String> listFilesPh = new ArrayList<>();
    private List<String> listFilesLog = new ArrayList<>();
    private List<String> listFilesNotInLog = new ArrayList<>();
    public PocImpl(ILogicalSystem logicalSystem, IPhysicalSystem physicalSystem, IParams params) {
        this.logicalSystem = logicalSystem;
        this.physicalSystem = physicalSystem;
        this.params = params;
    }


    private List<String> getAllRootPathsLogiques() {
        List<String> fileList;

        fileList = logicalSystem.getAllRootPathsLogiques();
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    private List<String> getAllFilesLogiques() {
        List<String> fileList = new ArrayList<>();

        for (String rootPath : getAllRootPathsLogiques()) {
            fileList.addAll(logicalSystem.getAllFilesLogiques(rootPath));
        }
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    private List<String> getAllPhysicalFiles(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet) {
        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {

            // Make sure the root path is a directory
            fileList.addAll(physicalSystem.listFiles(rootPath, allowedExtensions, excludeSubdirectoryRejet));
        }
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    @Override
    public List<String> getPhysicalFilesNotLogic() {

        List<String> rootPaths = getAllRootPathsLogiques();
        List<String> allowedExtensions = params.getAllowedExtensions();
        List<String> excludeSubdirectoryRejet = params.getSubdirectoryRejet();

        listFilesPh = getAllPhysicalFiles(rootPaths, allowedExtensions, excludeSubdirectoryRejet);
        listFilesLog = getAllFilesLogiques();

        listFilesPh.removeAll(listFilesLog);

        // Sort the file list alphabetically
        Collections.sort(listFilesPh);
        return listFilesPh;
    }

    @Override
    public Analysis analyseFilePhysiqueAndLogic() {
        List<String> rootPaths = getAllRootPathsLogiques();
        List<String> allowedExtensions = params.getAllowedExtensions();
        List<String> excludeSubdirectoryRejet = params.getSubdirectoryRejet();

        listFilesPh = getAllPhysicalFiles(rootPaths, allowedExtensions, excludeSubdirectoryRejet);
        listFilesLog = getAllFilesLogiques();

        listFilesNotInLog = new ArrayList<>();
        List<String> listFilesNotInPhy = new ArrayList<>();
        int koPhy = 0;
        int koLog = 0;
        Collections.sort(listFilesPh);
        Collections.sort(listFilesLog);
        Analysis result = new Analysis();


        int nbLog = listFilesLog.size();

        int nbPhy = listFilesPh.size();

        LOGGER.info("Differences between listFilesPh and listFilesLog:");

        Iterator<String> iterPh = listFilesPh.listIterator();
        Iterator<String> iterLo = listFilesLog.listIterator();

        String elemPh = iterPh.hasNext() ? iterPh.next() : null;
        String elemLo = iterLo.hasNext() ? iterLo.next() : null;

        while (elemPh != null || elemLo != null) {
            int compareResult;

            if (elemPh == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemLo == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemPh.compareTo(elemLo);
            }

            if (compareResult < 0) {
                //not in listFilesLog
                listFilesNotInLog.add(elemPh);
                koPhy++;
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
            } else if (compareResult > 0) {
                //not in listFilesPh
                listFilesNotInPhy.add(elemLo);
                koLog++;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            }
        }

        LOGGER.info(" nb path logique  = " + nbLog + " : absent logique in physique = " + koLog + "\n");
        LOGGER.info(" nb path physique = " + nbPhy + " : absent physique in logique = " + koPhy + "\n");

        result.logicNb = nbLog;
        result.logicKo = koLog;
        result.phyNb = nbPhy;
        result.phyKo = koPhy;
        result.listFilesNotInPhy = listFilesNotInPhy;
        result.listFilesNotInLog= listFilesNotInLog;

        return result;
    }


    @Override
    public Analysis synchroDatabase() throws SQLException, IOException, ParseException {

        analyseFilePhysiqueAndLogic();

        Analysis result = new Analysis();

        correctPhysique_lc_duplicate(listFilesPh, listFilesNotInLog);

        correctLogicalLowercase(listFilesLog, listFilesNotInLog);

        correctPhysiqueLostSidecar(listFilesNotInLog);

        correctPhysiqueAlreadyHash(listFilesNotInLog);

        correctPhysiqueStartDot(listFilesNotInLog);

        //todo
        // - gif 2 mp4
        // - bmp 2 jpeg
        // - mp3 2 mp4
        return result;
    }


    private void correctPhysique_lc_duplicate(List<String> listFilesPh, List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //correct physique file in double in lowercase (db can handler only one unique folder+lc_idx_filename)
        List<String> listFilesToRename = new ArrayList<>();

        Collections.sort(listFilesNotInLog);

        Iterator<String> iterPhDup = listFilesPh.listIterator();
        Iterator<String> iterPhNotLo = listFilesNotInLog.listIterator();

        String elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
        String elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;

        while (elemPhDup != null || elemPhNotLo != null) {
            int compareResult;

            if (elemPhDup == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemPhNotLo == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemPhDup.toLowerCase().compareTo(elemPhNotLo.toLowerCase());
            }

            if (compareResult < 0) {
                elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
            } else if (compareResult > 0) {
                elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
                while (elemPhDup != null && elemPhDup.toLowerCase().compareTo(elemPhNotLo.toLowerCase()) == 0 ) {
                    listFilesToRename.add(elemPhDup);
                    elemPhDup = iterPhDup.hasNext() ? iterPhDup.next() : null;
                }
                elemPhNotLo = iterPhNotLo.hasNext() ? iterPhNotLo.next() : null;
            }
        }

        Iterator<String> iterPhRen = listFilesToRename.listIterator();
        String elemPhRep = iterPhRen.hasNext() ? iterPhRen.next() : null;
        int koRenameDo = 0;
        while (elemPhRep != null ) {

            String generatedString = new Random().ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            //rename to rejet dans meme repertoire
            String oldName = elemPhRep;
            String newName = elemPhRep.substring(0,elemPhRep.lastIndexOf(".")) + "_" + generatedString + elemPhRep.substring(elemPhRep.lastIndexOf("."));
            koRenameDo = koRenameDo + moveFile(oldName, newName);
            elemPhRep = iterPhRen.hasNext() ? iterPhRen.next() : null;
        }
        LOGGER.info("    --- corrige Physique lc_duplicate            : toDo = " + listFilesToRename.size() + " , Done = " + koRenameDo + "\n");
        //------------------------------------------------
    }
    

    private void correctLogicalLowercase(List<String> listFilesLog, List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //correct logique extension lowercase/uppercase
        //correct logique filename lowercase/uppercase
        List<List<String>> listLoIdxFilenameToUpdate = new ArrayList<>();

        Collections.sort(listFilesNotInLog);

        Iterator<String> iterLoRe = listFilesLog.listIterator();
        Iterator<String> iterPhNotLog = listFilesNotInLog.listIterator();

        String elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
        String elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;

        while (elemLoRe != null || elemPhNotLog != null) {
            int compareResult;

            if (elemLoRe == null) {
                compareResult = 1; // listFilesPh is exhausted
            } else if (elemPhNotLog == null) {
                compareResult = -1; // listFilesLog is exhausted
            } else {
                compareResult = elemLoRe.toLowerCase().compareTo(elemPhNotLog.toLowerCase());
            }

            if (compareResult < 0) {
                elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
            } else if (compareResult > 0) {
                elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                if (elemLoRe.compareTo(elemPhNotLog)!=0){
                    listLoIdxFilenameToUpdate.add(Arrays.asList(elemLoRe,elemPhNotLog));
                }
                elemLoRe = iterLoRe.hasNext() ? iterLoRe.next() : null;
                elemPhNotLog = iterPhNotLog.hasNext() ? iterPhNotLog.next() : null;
            }
        }

        Iterator<List<String>> iterLoIdxUp = listLoIdxFilenameToUpdate.listIterator();
        List<String> elemLoIdxUp = iterLoIdxUp.hasNext() ? iterLoIdxUp.next() : null;
        int koLoExtUpdateDo = 0;
        while (elemLoIdxUp != null ) {
            int ret = moveFile(elemLoIdxUp.get(0), elemLoIdxUp.get(1));
            if (ret>0) {
                koLoExtUpdateDo = koLoExtUpdateDo + ret;
                listFilesNotInLog.remove(elemLoIdxUp.get(1));
            }
            elemLoIdxUp = iterLoIdxUp.hasNext() ? iterLoIdxUp.next() : null;
        }
        LOGGER.info("    --- corrige Correct logique U/L case         : toDo = " + listLoIdxFilenameToUpdate.size() + " , Done = " + koLoExtUpdateDo + "\n");
        //------------------------------------------------
    }

    private void correctPhysiqueLostSidecar(List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //move physique lost sidecar to rejet
        List<String> listPhLostSidecarToRejet = new ArrayList<>();
        int koPhLostSidecarToRejetDo =0;
        for (String element : listFilesNotInLog) {
            for (String suffix : params. getExtensionFileRejetSup()) {
                if (element.toLowerCase().endsWith(suffix.toLowerCase())) {
                    listPhLostSidecarToRejet.add(element);
                    break;  // If a match is found, break out of the inner loop
                }
            }
        }
        koPhLostSidecarToRejetDo = putThatInRejet(listPhLostSidecarToRejet);
        LOGGER.info("    --- corrige physique lost sidecar to rejet   : toDo = " + listPhLostSidecarToRejet.size() + " , Done = " + koPhLostSidecarToRejetDo + "\n");
        //------------------------------------------------
    }

    private void correctPhysiqueAlreadyHash(List<String> listFilesNotInLog) throws SQLException, IOException, ParseException {
        //------------------------------------------------
        //move physique already hash in database to rejet
        int koPhHashExistToRejetDo =0;
        for (String element : listFilesNotInLog) {
            if (logicalSystem.getFileByHash(lrHashOf(element)).compareTo("")!=0) {
                koPhHashExistToRejetDo = koPhHashExistToRejetDo + moveTo99Rejet(element);
            }
        }
        LOGGER.info("    --- corrige physique hash already exist      : -------------- Done = " + koPhHashExistToRejetDo + "\n");
    }

    private void correctPhysiqueStartDot(List<String> listFilesNotInLog) throws IOException, SQLException {
        //------------------------------------------------
        //move physique file start with dot
        int koPhStartDotDo = 0;
        String baseNameNew = "";
        for (String element : listFilesNotInLog) {
            String baseName = FilenameUtils.getBaseName(element);
            if (baseName.startsWith(".")) {
                baseNameNew = baseName;
                while (baseNameNew.startsWith(".")) {
                    baseNameNew = baseNameNew.substring(1);
                }
                koPhStartDotDo = koPhStartDotDo + moveFile(element, element.replace(baseName,baseNameNew));
            }
        }
        LOGGER.info("    --- corrige physique start dot               : -------------- Done = " + koPhStartDotDo + "\n");
    }


    private String lrHashOf(String file) throws IOException, ParseException {
        //706893605.66357:img_2337.MOV:86632353
        //modtime(windows):filename:sizeinbytes

        Path filePath = Paths.get(file);
        FileTime fileTime = Files.getLastModifiedTime(filePath);
        // Convert the FileTime to microsecond
        long unixTimeMicro = fileTime.to(TimeUnit.MICROSECONDS);
        double dbFileTime = ((((double) unixTimeMicro) /10) / 100000) - GMT01JAN200112AM;//GMT: Monday, January 1, 2001 12:00:00 AM
        String PerfectHash = new DecimalFormat("#.#####").format(dbFileTime) + ":" + filePath.getFileName() + ":" + String.valueOf(Files.size(filePath));
        String timestamp = new DecimalFormat("#.#####").format(dbFileTime);
        String hashLike = timestamp.substring(0, timestamp.length() - 3) + "%:%" + filePath.getFileName() + ":" + String.valueOf(Files.size(filePath));
        return hashLike;
    }
   
    private int putThatInRejet(List<String> arrayFichierRejetStr) throws IOException, SQLException {

        int countMove = 0;
        Map<String, Integer> countExt = new HashMap<>();
        Map<String, Integer> countExtDo = new HashMap<>();
        Map<String, Integer> countExtDel = new HashMap<>();
        LOGGER.debug("arrayFichierRejet     => " + String.format("%05d", arrayFichierRejetStr.size()));

        for (int y = 0; y < arrayFichierRejetStr.size(); y++) {
            String fichierStr = arrayFichierRejetStr.get(y);

//            visuProgress(progress,fichierStr,y,arrayFichierRejetStr.size());

            String fileExt = FilenameUtils.getExtension(fichierStr).toLowerCase();
            String filepath = FilenameUtils.getFullPath(fichierStr).toLowerCase();

            if (countExt.containsKey(fileExt)) {
                countExt.replace(fileExt, countExt.get(fileExt), countExt.get(fileExt) + 1);
            } else {
                countExt.put(fileExt, 1);
                countExtDo.put(fileExt, 0);
                countExtDel.put(fileExt, 0);
            }

            if (fileExt.toLowerCase().compareTo("zip") == 0) {
                LOGGER.info("unzip :" + fichierStr);
                physicalSystem.extractZipFile(new File(fichierStr));
            }

            if (params.getExtensionsUseFile().contains(fileExt.toLowerCase()) ||
                    (
                            params.getExtFileRejet().compareTo(fileExt.toLowerCase()) == 0
                                    && !params.getNomSubdirectoryRejet().stream().anyMatch(filepath::contains)
                    )
            ) {
                String generatedString =  new Random().ints(48, 122 + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(5)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                //rename to rejet dans meme repertoire
                String oldName = fichierStr;
                String newName = addRejetSubRepToPath(oldName) + "." + generatedString + "." + params.getExtFileRejet();
                File fsource = new File(oldName);
                File fdest = new File(newName);
                if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                    countExtDo.replace(fileExt, countExtDo.get(fileExt), countExtDo.get(fileExt) + 1);
                    countMove = countMove + moveFile(oldName, newName);
                }
            } else {
                if (params. getExtensionFileRejetSup().contains(fileExt.toLowerCase())) {
                    int retNb = moveTo99Rejet(fichierStr);
                    countMove = countMove + retNb;
                    countExtDel.replace(fileExt, countExtDel.get(fileExt), countExtDel.get(fileExt) + retNb);

                }
            }
        }
//        progress.setString("");
        Iterator it = countExt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info("arrayFichierRejet." + pair.getKey() + " => " + String.format("%05d", pair.getValue()) + " => do : " + String.format("%05d", countExtDo.get(pair.getKey())) + " => del : " + String.format("%05d", countExtDel.get(pair.getKey())));
        }
        return countMove;
    }

    private int moveTo99Rejet(String fichierStr) throws IOException, SQLException {
        //move to 99-rejet
        int countMove = 0;

        if (params.getRepertoire("Rejet") != "") {

            String oldName = fichierStr;
            File fsource = new File(oldName);

            File fdest = new File(params.getRepertoire50Phototheque() + params.getRepertoire("Rejet") + FOLDERDELIM + oldName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"));
            String newName = fdest.toString();

            if (fsource.exists() && fsource.isFile() && !fdest.exists()) {
                countMove = countMove + moveFile(oldName, newName);
            } else {
                LOGGER.debug("oldName " + oldName + " move impossile to newName : " + newName);
                LOGGER.debug("fsource.exists() " + fsource.exists());
                LOGGER.debug("fsource.isFile()  " + fsource.isFile());
                LOGGER.debug("!fdest.exists() " + !fdest.exists());
            }
        }

        return countMove;
    }

    private String addRejetSubRepToPath(String fichierStr) throws SQLException {
        if (fichierStr.toLowerCase(Locale.ROOT).contains("\\rejet\\")) {
            return fichierStr;
        } else {
            String rejetPath = FilenameUtils.concat(FilenameUtils.getFullPath(fichierStr), "rejet");
            return FilenameUtils.concat(rejetPath, FilenameUtils.getName(fichierStr));
        }
    }
    public int moveFile(String source, String newPath) throws IOException, SQLException {
        int ret = 0;
        sqlMkdirRepertoryPhyAndLog(new File(newPath).getParent() + File.separator);

        ret = physicalSystem.movePhysique(source, newPath);

        long scrFileIdLocal = logicalSystem.getIdlocalforFilePath(source).get("idlocal");
        if (scrFileIdLocal > 0) {
            ret = logicalSystem.sqlmovefile(scrFileIdLocal, newPath);
            //move sidecar also
            String[] extensionArray = logicalSystem.sqlGetSidecarExtensions(scrFileIdLocal).split(",");
            for (String newExtension : extensionArray) {
                String modifiedSource = modifyPath(source, newExtension);
                String modifiedNewPath = modifyPath(newPath, newExtension);
                physicalSystem.movePhysique(modifiedSource, modifiedNewPath);
            }
        }
        return ret;
    }

    private String modifyPath(String originalPath, String newExtension) {
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

    public String modifyPathIfExistWithRandom(String newPath) {
        File fdest = new File(newPath);
        if (fdest.exists()) {
            newPath = fdest.getParent() + "\\" + Math.floor(Math.random() * (100 - 0 + 1) + 0) + fdest.getName();
        }
        return newPath;
    }

    private void sqlMkdirRepertoryPhyAndLog(String directoryName) throws SQLException {

        File fdirectoryName = new File(directoryName);
        if (!fdirectoryName.exists()) {
            physicalSystem.mkdir(directoryName);
            if (logicalSystem == null) {
                logicalSystem.makeRepertory(directoryName);
            }
        }
    }
}
