package com.malicia.mrg.param.importjson;

import com.malicia.mrg.util.Serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionRepertoire extends Serialize {

    public Map<String, String> listeAction = new HashMap<>();

    public ActionRepertoire() {
        // Do nothing because of X and Y
    }

    public ActionRepertoire(String collections) {

    }

    public void setListeAction(Map<String, String> listeAction) {
        this.listeAction = listeAction;
    }

    public void addAction(String Nom, String Destination) {
        listeAction.put(Nom, Destination);
    }

    public void populate(Map<String, String> folderCollection) {
        listeAction = folderCollection;
    }
}

