package malicia.mrg.photo.organize.domain.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analysis {
    private HashMap<String, ActionPoc> actions = new HashMap<String, ActionPoc>();
    public void addResult(Analysis analysis) {
        actions.putAll(analysis.getActions());
    }

    public Map<String, ActionPoc> getActions() {
        return actions;
    }

    public void add(String label,List<String> listFilesToDo, int nbDone) {
        actions.put(label ,new ActionPoc(listFilesToDo,nbDone));
    }
}
