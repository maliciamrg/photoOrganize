package malicia.mrg.photo.organize.domain.dto;

import java.util.List;

public class ActionPoc {
    private final List<String> listFilesToDo;
    private final int nbDone;

    public ActionPoc(List<String> listFilesToDo, int nbDone) {

        this.listFilesToDo = listFilesToDo;
        this.nbDone = nbDone;
    }
}
