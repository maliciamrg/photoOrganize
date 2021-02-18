package com.malicia.mrg;

import com.malicia.mrg.param.NommageRepertoire;
import com.malicia.mrg.param.RepertoirePhoto;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Context {
    private NommageRepertoire paramNommageRepertoire;
    private List<RepertoirePhoto> arrayRepertoirePhoto;
    private String repertoireRoamingAdobeLightroom;
    private String repertoireDestZip;
    private List<String> arrayNomSubdirectoryRejet;
    private String repertoire50Phototheque;
    private String repertoire00NEW;

    public Context() {
        chargeArrayNomSubdirectoryRejet();
        chargeRepertoireFonctionnel();
        chargeNommageRepertoire();
    }

    private void chargeNommageRepertoire() {
        //todo
        paramNommageRepertoire = null;
    }

    public static Context chargeParam() {
        return new Context();
    }

    public NommageRepertoire getParamNommageRepertoire() {
        return paramNommageRepertoire;
    }

    public List<RepertoirePhoto> getArrayRepertoirePhoto() {
        return arrayRepertoirePhoto;
    }

    public String getRepertoireDestZip() {
        int n = (int) Math.floor(Math.random() * 100000 + 1);
        NumberFormat formatter = new DecimalFormat("00000");
        String number = formatter.format(n);
        return repertoireDestZip.replace("%num%", number);
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return repertoireRoamingAdobeLightroom;
    }


    private void chargeRepertoireFonctionnel() {
        //TODO
        this.arrayRepertoirePhoto = null;
        this.repertoireDestZip = null; //"D:\95_Boite_a_outils\LightroomConfigSauve\LightroomSauve-00%num%.zip"
        this.repertoireRoamingAdobeLightroom = null; //"C:\Users\professorX\AppData\Roaming\Adobe\Lightroom"
        this.repertoire50Phototheque = null; //"D:\50_Phototheque\"
        this.repertoire00NEW = null; //"P:\00_NEW" "P:\50_Phototheque" "P:\20_Portfolio"
    }

    public List<String> getArrayNomSubdirectoryRejet() {
        return arrayNomSubdirectoryRejet;
    }

    private void chargeArrayNomSubdirectoryRejet() {
        //TODO
        this.arrayNomSubdirectoryRejet = null;
    }

    public String getRepertoire50Phototheque() {
        return repertoire50Phototheque;
    }

    public String getRepertoire00NEW() {
        return repertoire00NEW;
    }

}
