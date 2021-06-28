package com.malicia.mrg.app.rep;

import java.util.ArrayList;
import java.util.List;

public class EleChamp {

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


    public void setRetourToFalse(String iretourControleString, List<String> tagRetour) {
        List<String> listTmp = new ArrayList<>();
        listTmp.add(iretourControleString);
        setRetourToFalse(listTmp, tagRetour);
    }
    public void setRetourToFalse(String iretourControleString, String tagRetour) {
        List<String> listTmp = new ArrayList<>();
        listTmp.add(iretourControleString);
        List<String> listTmpRet = new ArrayList<>();
        listTmpRet.add(tagRetour);
        setRetourToFalse(listTmp, listTmpRet);
    }
    public void setRetourToFalse(List<String> iretourControle, List<String> tagRetour) {
        this.compTagRetour.addAll(tagRetour);
        this.infoRetourControle.addAll(iretourControle);
        retourControle = false;
    }

    public void setRetourToTrue() {
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
