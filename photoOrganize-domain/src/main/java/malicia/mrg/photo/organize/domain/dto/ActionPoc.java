package malicia.mrg.photo.organize.domain.dto;

import java.util.List;

public class ActionPoc {
    private List<String> listFilesToDo;
    private int nbDone;

    public ActionPoc(List<String> listFilesToDo, int nbDone) {

        this.listFilesToDo = listFilesToDo;
        this.nbDone = nbDone;
    }

    public List<String> getListFilesToDo() {
        return listFilesToDo;
    }

    public void setListFilesToDo(List<String> listFilesToDo) {
        this.listFilesToDo = listFilesToDo;
    }

    public int getNbDone() {
        return nbDone;
    }

    public void setNbDone(int nbDone) {
        this.nbDone = nbDone;
    }

    @Override
    public String toString() {
        return "ActionPoc{" +
                "listFilesToDo=" + listFilesToDo +
                ", nbDone=" + nbDone +
                '}';
    }
}
