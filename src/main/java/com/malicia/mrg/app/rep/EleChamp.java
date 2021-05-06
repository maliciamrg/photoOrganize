package com.malicia.mrg.app.rep;

import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class EleChamp {

    private static final Logger LOGGER = LogManager.getLogger(EleChamp.class);

    private String cChamp;
    private String oValue;
    private List<String> infoRetourControle;
    private List<String> compTagRetour;
    private boolean retourControle;

    public EleChamp() {
        this.cChamp = "";
        this.oValue = "";
        this.infoRetourControle = new ArrayList<>();
        this.compTagRetour = new ArrayList<>();
    }

    public EleChamp(String codeChamp, String oldValue) {
        this.cChamp = codeChamp;
        this.oValue = oldValue;
        this.infoRetourControle = new ArrayList<>();
        this.compTagRetour = new ArrayList<>();
    }

    public List<String> getCompTagRetour() {
        return compTagRetour;
    }

    public boolean isRetourControle() {
        return retourControle;
    }

    public void setRetourControle(boolean retourControle) {
        this.retourControle = retourControle;
    }

    public List<String> getInfoRetourControle() {
        return infoRetourControle;
    }

    public void setRetourToFalse(String iretourControleString, String tagRetour) {
        List<String> listTmp = new ArrayList<>();
        listTmp.add(iretourControleString);
        setRetourToFalse(listTmp, tagRetour);
    }

    public void setRetourToFalse(List<String> iretourControle, String tagRetour) {
        this.compTagRetour.add(tagRetour);
        this.infoRetourControle.addAll(iretourControle);
        retourControle = false;
    }

    public void setRetourToTrue() {
        //this.infoRetourControle.add(ControleRepertoire.CARAC_EMPTY);
        this.compTagRetour = new ArrayList<>();
        retourControle = true;
    }

    public String getcChamp() {
        return cChamp;
    }

    public void setcChamp(String codeChamp) {
        this.cChamp = codeChamp;
    }

    public String getoValue() {
        return oValue;
    }

    public void setoValue(String oldValue) {
        this.oValue = oldValue;
    }


    @Override
    public String toString() {
        return "EleChamp{" +
                "cChamp='" + cChamp + '\'' +
                ", oValue='" + oValue + '\'' +
                ", infoRetourControle='" + infoRetourControle.toString() + '\'' +
                ", compTagRetour=" + compTagRetour +
                ", retourControle=" + retourControle +
                '}';
    }
}
