package com.malicia.mrg.param;

import com.malicia.mrg.util.Serialize;
import javafx.collections.FXCollections;

import java.util.List;

public class NommageRepertoire extends Serialize {

    private List<String> champsNom;

    public NommageRepertoire() {
        champsNom = FXCollections.observableArrayList();
        champsNom.add("RepertoirePhoto.nomRepertoire");
        champsNom.add("starValue");
        champsNom.add("nbSelectionner");
        champsNom.add("nbphotoapurger");
    }
}
