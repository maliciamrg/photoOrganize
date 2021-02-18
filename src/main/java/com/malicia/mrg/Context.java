package com.malicia.mrg;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Context {
    private String repertoireRoamingAdobeLightroom;

    public String getRepertoireDestZip() {
        int n = (int) Math.floor(Math.random() * 100000 + 1);
        NumberFormat formatter = new DecimalFormat("00000");
        String number = formatter.format(n);
        return repertoireDestZip.replace("%num%",number);
    }

    private String repertoireDestZip;
    private List<String> arraynomsubdirectoryrejet;
    private String repertoire50Phototheque;
    private String repertoire00NEW;
    public Context() {
        ChargeArraynomsubdirectoryrejet();
        ChargeRepertoireFonctionnel();
    }

    public static Context chargeParam() {
        Context ctx = new Context();
        return ctx;
    }

    public String getRepertoireRoamingAdobeLightroom() {
        return repertoireRoamingAdobeLightroom;
    }


    private void ChargeRepertoireFonctionnel() {
        //TODO
        this.repertoireDestZip = null; //"D:\95_Boite_a_outils\LigthroomConfigSauve\ligthroomSauve-00%num%.zip"
        this.repertoireRoamingAdobeLightroom = null; //"C:\Users\professorX\AppData\Roaming\Adobe\Lightroom"
        this.repertoire50Phototheque = null; //"D:\50_Phototheque\"
        this.repertoire00NEW = null; //"P:\00_NEW" "P:\50_Phototheque" "P:\20_Portfolio"
    }

    public List<String> getArraynomsubdirectoryrejet() {
        return arraynomsubdirectoryrejet;
    }

    private void ChargeArraynomsubdirectoryrejet() {
        //TODO
        this.arraynomsubdirectoryrejet = null;
    }

    public String getrepertoire50Phototheque() {
        return repertoire50Phototheque;
    }

    public String getrepertoire00NEW() {
        return repertoire00NEW;
    }

}
