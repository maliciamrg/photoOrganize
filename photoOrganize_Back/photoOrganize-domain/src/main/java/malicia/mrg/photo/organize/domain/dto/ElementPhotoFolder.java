package malicia.mrg.photo.organize.domain.dto;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementPhotoFolder {
    private final String folderName;
    private final ElementRootFolder rootFolder;
    private final IPhysicalSystem physicalSystem;
    private final ILogicalSystem logicalSystem;
    private final String folderValideName;
    private final boolean isFolderNameValide;
    private final SimpleDateFormat repDateFormat;

    public ElementPhotoFolder(String folderName,
                              ElementRootFolder rootFolder,
                              IPhysicalSystem physicalSystem,
                              ILogicalSystem logicalSystem) {

        this.folderName = folderName;
        this.rootFolder = rootFolder;
        this.physicalSystem = physicalSystem;
        this.logicalSystem = logicalSystem;

        repDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //control nom du repertoire
        folderValideName = controlFolderName();
        isFolderNameValide = (folderValideName.compareTo(folderName) == 0);

    }

    private String controlFolderName() {

        List<String> valideName = new ArrayList<>();

        String[] arrayFolderName = folderName.split("_");

        for (int i = 0; i < rootFolder.getZoneValeurAdmise().size(); i++) {

            String elemChamp = i < arrayFolderName.length ? arrayFolderName[i] : "";
            valideName.add(testElementChamp(rootFolder.getZoneValeurAdmise().get(i), elemChamp));

        }
        valideName.addAll(Arrays.asList(arrayFolderName));
        return String.join("_", valideName);
    }

    private String testElementChamp(String valuePossible, String elemChamp) {
        boolean champOk = false;
        String retValue = valuePossible;

        String[] listValuePossible = valuePossible.split("\\|");

        for (String listValuePossibleElem : listValuePossible) {

            switch (listValuePossibleElem) {
                case "£DATE£":
                    champOk = champOk | elemChamp.compareTo(repDateFormat.format(logicalSystem.getFolderFirstDate(folderName))) == 0;
                    break;
                case "@10_Action@":
                case "@10_Piece@":
                case "@10_Chantier@":
                case "@00_EVENT@":
                case "@00_PHOTOGRAPHY@":
                case "@00_WHERE@":
                case "@00_WHAT@":
                case "@00_WHO@":
                    champOk = champOk | (
                            logicalSystem.isValueInKeyword(
                                    elemChamp,
                                    listValuePossibleElem.replaceAll("@", "")
                            )
                    );
                    break;
                default:
                    champOk = false;
            }
        }
        if (champOk) {
            retValue = elemChamp;
        }
        return retValue;
    }

    @Override
    public String toString() {
        return "ElementPhotoFolder{" +
                "folderName='" + folderName + '\'' +
                ", folderValideName='" + folderValideName + '\'' +
                ", isFolderNameValide=" + isFolderNameValide +
                '}';
    }
}
