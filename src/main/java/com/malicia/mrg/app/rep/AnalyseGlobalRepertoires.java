package com.malicia.mrg.app.rep;

import com.malicia.mrg.Context;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.param.importjson.TriNew;
import com.malicia.mrg.util.WhereIAm;
import com.malicia.mrg.view.RenameRepertoire;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyseGlobalRepertoires {
    private static final Logger LOGGER = LogManager.getLogger(AnalyseGlobalRepertoires.class);
    private List<blocRetourRepertoire> listOfretourNomRepertoire;
    private List<blocRetourRepertoire> listOfretourValRepertoire;

    public AnalyseGlobalRepertoires() {
        this.listOfretourNomRepertoire = new ArrayList<>() ;
        this.listOfretourValRepertoire = new ArrayList<>() ;
    }

    public void add(blocRetourRepertoire retourRepertoire) {
        if (!retourRepertoire.getValOk()) {
            this.listOfretourValRepertoire.add(retourRepertoire);
        }
        if (!retourRepertoire.getNomOk()) {
            this.listOfretourNomRepertoire.add(retourRepertoire);
        }
    }

    public void action(Context ctx, Database dbLr, JFrame frame) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        RenameRepertoire.start(listOfretourNomRepertoire);
    }

    @Override
    public String toString() {
        return "AnalyseGlobalRepertoires{" +
                "nbListOfretourNomRepertoire=" + listOfretourNomRepertoire.size() +
                ", nbListOfretourValRepertoire=" + listOfretourValRepertoire.size() +
                '}';
    }
}
