package malicia.mrg.photo.organize.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class SynchroPhysicalAndLogical {
    private static final Logger LOGGER_TO_SYNC_PH_FILE = LogManager.getLogger("loggerToSyncPhFile");

    List<String> listFilesPh = new ArrayList<>();
    List<String> listFilesLog = new ArrayList<>();
    List<String> listFilesNotInLog = new ArrayList<>();

    public SynchroPhysicalAndLogical(Params params) {
        List<String> rootPaths = getAllRootPathsLogiques();
        List<String> allowedExtensions = params.getAllowedExtensions();
        List<String> excludeSubdirectoryRejet = params.getSubdirectoryRejet();
        
        listFilesPh = getAllPhysicalFiles(rootPaths,allowedExtensions,excludeSubdirectoryRejet);
        listFilesLog = getAllFilesLogiques();
    }

    private List<String> getAllRootPathsLogiques() {
    }

    private List<String> getAllFilesLogiques() {
    }

    public List<String> getAllPhysicalFiles(List<String> rootPaths, List<String> allowedExtensions, List<String> excludeSubdirectoryRejet){
        List<String> fileList = new ArrayList<>();

        for (String rootPath : rootPaths) {

            // Make sure the root path is a directory
            try {
                fileList.addAll(listFiles(rootPath, allowedExtensions,excludeSubdirectoryRejet));
            } catch (IOException e) {
                System.out.println("Error due to: " + e.getMessage());
            }
        }
        // Sort the file list alphabetically
        Collections.sort(fileList);
        return fileList;
    }

    public void generateListFilesNotInLog(List<String> listFilesPh,List<String> listFilesLog){
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
                LOGGER_TO_SYNC_PH_FILE.info("not in listFilesLog - " + elemPh + "");
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

}
