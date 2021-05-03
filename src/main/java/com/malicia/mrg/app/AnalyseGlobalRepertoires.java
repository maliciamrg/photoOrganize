package com.malicia.mrg.app;

import com.malicia.mrg.Context;
import com.malicia.mrg.Main;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import com.malicia.mrg.util.WhereIAm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;

public class AnalyseGlobalRepertoires {
    private static final Logger LOGGER = LogManager.getLogger(AnalyseGlobalRepertoires.class);

    public void add(RepertoirePhoto repPhoto, String repertoire, List<EleChamp> listOfChamp) {

    }

    public void action(Context ctx, Database dbLr, JFrame frame) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);

    }
}
