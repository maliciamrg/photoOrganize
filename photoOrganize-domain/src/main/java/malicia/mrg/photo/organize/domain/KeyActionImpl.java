package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.IKeyAction;
import malicia.mrg.photo.organize.domain.dto.Analysis;
import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyActionImpl implements IKeyAction {
    private static final Logger LOGGER = LogManager.getLogger("loggerToSyncPhFile");
    private final ILogicalSystem logicalSystem;
    private final IPhysicalSystem physicalSystem;
    private final IParams params;

    public KeyActionImpl(ILogicalSystem logicalSystem, IPhysicalSystem physicalSystem, IParams params) {
        this.logicalSystem = logicalSystem;
        this.physicalSystem = physicalSystem;
        this.params = params;
    }

    @Override
    public Analysis makeActionRapprochement() {
        Analysis result = new Analysis();
        List<String> listMove= new ArrayList<>();

        Map<String, Map<String, String>> fileToGo = logicalSystem.getFileForGoTag(params.getTag_action_go_rapprochement());
        LOGGER.info("nb de fichier tagger : " + params.getTag_action_go_rapprochement() + " => " + String.format("%05d", fileToGo.size()), fileToGo.size());
        int nb = 0;
        for (String scrFileIdLocal : fileToGo.keySet()) {
            Map<String, String> forGoTag = logicalSystem.getNewPathForGoTagandFileIdlocal(params.getTag_org(), scrFileIdLocal);
            if (forGoTag.size() > 0) {
                nb++;
                String source = fileToGo.get(scrFileIdLocal).get("oldPath");
                String newPath = forGoTag.get("newPath");
                if (new File(source).exists() && !new File(newPath).exists()) {
                    LOGGER.debug("---move " + params.getTag_action_go_rapprochement() + " : " + source + " -> " + newPath);
                    try {
                        Tools.moveFile(source, newPath,physicalSystem,logicalSystem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    listMove.add(source);
                    logicalSystem.removeKeywordImages(forGoTag.get("kiIdLocal"));
                }
            }
        }
        LOGGER.info("move " + String.format("%05d", nb) + " - " + params.getTag_action_go_rapprochement(), nb);
        result.add("move - " + params.getTag_action_go_rapprochement() ,listMove,nb);
        return result;
    }

    @Override
    public Analysis makeActionMoveToRepertory() {
        Analysis result = new Analysis();
        List<String> listMove= new ArrayList<>();

        Map<String, String> listeAction = logicalSystem.getFolderCollection(params.getCollections(), params.getTag_org(), "");
        int nb = 0;
        for (String key : listeAction.keySet()) {
            Map<String, Map<String, String>> fileToTag = logicalSystem.sqllistAllFileWithTagtoRep(key, listeAction.get(key));
            LOGGER.info("move " + String.format("%05d", fileToTag.size()) + " - " + key, fileToTag.size());
            for (String scrFileIdLocal : fileToTag.keySet()) {
                nb++;
                String oldPath = fileToTag.get(scrFileIdLocal).get("oldPath");
                String newPath = fileToTag.get(scrFileIdLocal).get("newPath");
                if (new File(oldPath).exists() && !new File(newPath).exists()) {
                    LOGGER.debug("---move " + key + " : " + oldPath + " -> " + newPath);
                    try {
                        Tools.moveFile(oldPath, newPath,physicalSystem,logicalSystem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    listMove.add(oldPath);
                    logicalSystem.removeKeywordImages(fileToTag.get(scrFileIdLocal).get("kiIdLocal"));
                }
            }
        }
        result.add("move - " + params.getTag_action_go_rapprochement() ,listMove,nb);
        return result;
    }


}
