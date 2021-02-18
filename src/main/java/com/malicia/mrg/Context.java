package com.malicia.mrg;

import java.util.List;

public class Context {
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

    private void ChargeRepertoireFonctionnel() {
        //TODO
        this.repertoire50Phototheque = null;
        this.repertoire00NEW = null;
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
