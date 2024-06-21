package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.IPoc;
import malicia.mrg.photo.organize.domain.ddd.DomainService;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@DomainService
public class PocImpl implements IPoc {
    private static final Logger LOGGER_TO_SYNC_PH_FILE = LogManager.getLogger("loggerToSyncPhFile");
    private final ILogicalSystem logicalSystem;
    private final IPhysicalSystem physicalSystem;
    private final IParams params;

    private List<String> listFilesPh = new ArrayList<>();
    private List<String> listFilesLog = new ArrayList<>();
    private final List<String> listFilesNotInLog = new ArrayList<>();

    public PocImpl(ILogicalSystem logicalSystem, IPhysicalSystem physicalSystem, IParams params) {
        this.logicalSystem = logicalSystem;
        this.physicalSystem = physicalSystem;
        this.params = params;
    }


    private List<String> getAllRootPathsLogiques() {
        List<String> fileList = new ArrayList<>();

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

    private void generateListFilesNotInLog(List<String> listFilesPh, List<String> listFilesLog) {
        int koPhy = 0;
        int koLog = 0;

//        int posLog = 0;
        int nbLog = listFilesLog.size();

//        int posPhy = 0;
        int nbPhy = listFilesPh.size();

        LOGGER_TO_SYNC_PH_FILE.info("Differences between listFilesPh and listFilesLog:");

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
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesLog - " + elemPh);
                listFilesNotInLog.add(elemPh);
                koPhy++;
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
            } else if (compareResult > 0) {
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesPh  - " + elemLo + " ");
                koLog++;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            }
        }
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
    public Analysis analyseFileFPhysiqueAndLogic() {
        List<String> rootPaths = getAllRootPathsLogiques();
        List<String> allowedExtensions = params.getAllowedExtensions();
        List<String> excludeSubdirectoryRejet = params.getSubdirectoryRejet();

        listFilesPh = getAllPhysicalFiles(rootPaths, allowedExtensions, excludeSubdirectoryRejet);
        listFilesLog = getAllFilesLogiques();

        List<String> listFilesNotInLog = new ArrayList<>();
        List<String> listFilesNotInPhy = new ArrayList<>();
        int koPhy = 0;
        int koLog = 0;
        Collections.sort(listFilesPh);
        Collections.sort(listFilesLog);
        Analysis result = new Analysis();


//        int posLog = 0;
        int nbLog = listFilesLog.size();

//        int posPhy = 0;
        int nbPhy = listFilesPh.size();

        LOGGER_TO_SYNC_PH_FILE.info("Differences between listFilesPh and listFilesLog:");

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
                //              LOGGER_TO_SYNC_PH_FILE.info("not in listFilesLog - " + elemPh + "");
                listFilesNotInLog.add(elemPh);
                koPhy++;
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
            } else if (compareResult > 0) {
                //              LOGGER_TO_SYNC_PH_FILE.info("not in listFilesPh  - " + elemLo + " ");
                listFilesNotInPhy.add(elemLo);
                koLog++;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            } else {
                // Elements are equal, move both iterators forward
                elemPh = iterPh.hasNext() ? iterPh.next() : null;
                elemLo = iterLo.hasNext() ? iterLo.next() : null;
            }
        }


        //LOGGER.info(" nb path logique  = " + nbLog + " : absent logique in physique = " + koLog + "\n");
        LOGGER_TO_SYNC_PH_FILE.info(" nb path logique  = " + nbLog + " : absent logique in physique = " + koLog + "\n");
        //LOGGER.info(" nb path physique = " + nbPhy + " : absent physique in logique = " + koPhy + "\n");
        LOGGER_TO_SYNC_PH_FILE.info(" nb path physique = " + nbPhy + " : absent physique in logique = " + koPhy + "\n");

        result.logicNb = nbLog;
        result.logicKo = koLog;
        result.phyNb = nbPhy;
        result.phyKo = koPhy;
        result.listFilesNotInPhy = listFilesNotInPhy;
        result.listFilesNotInLog= listFilesNotInLog;
//        correctPhysique_lc_duplicate(listFilesPh, listFilesNotInLog);

//        correctLogicalLowercase(listFilesLog, listFilesNotInLog);

//        correctPhysiqueLostSidecar(listFilesNotInLog);

//        correctPhysiqueAlreadyHash(listFilesNotInLog);

//        correctPhysiqueStartDot(listFilesNotInLog);

        //todo
        // - gif 2 mp4
        // - bmp 2 jpeg
        // - mp3 2 mp4

//        LOGGER.info("\n");
        return result;
    }


}
