package mrg.param.electx;

import com.malicia.mrg.util.Serialize;

import java.util.*;

public class ActionRepertoire {

    private Map<String, String> listeAction = new HashMap<>();

    public ActionRepertoire() {
        // Do nothing because of X and Y
    }

    public Map<String, String> getListeAction() {
        return listeAction;
    }

    public void populate(HashMap<String, String> folderCollection) {
        HashMap tmp = new HashMap<>();
        tmp.putAll(listeAction);
        tmp.putAll(folderCollection);
        listeAction= sort(tmp);
    }

    private static HashMap sort(HashMap map) {
        List linkedlist = new LinkedList(map.entrySet());
        Collections.sort(linkedlist, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = linkedlist.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}

