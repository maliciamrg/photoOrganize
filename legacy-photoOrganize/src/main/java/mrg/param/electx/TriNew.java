package mrg.param.electx;

import com.malicia.mrg.util.Serialize;


import java.util.ArrayList;
import java.util.List;

public class TriNew {

    public static final String FORMATDATE_YYYY_MM_DD = "yyyy-MM-dd";
    public String repertoire50NEW;
    public String repertoireKidz;
    public String repertoireBazar;
    public long thresholdNew;
    public String tempsAdherence;
    public List<String> listeModelKidz = new ArrayList<>();


    public TriNew() {
        // Do nothing because of X and Y
    }

    public String getRepertoireBazar() {
        return repertoireBazar;
    }

    public void setRepertoireBazar(String repertoireBazar) {
        this.repertoireBazar = repertoireBazar;
    }

    public String getTempsAdherence() {
        return tempsAdherence;
    }

    public void setTempsAdherence(String tempsAdherence) {
        this.tempsAdherence = tempsAdherence;
    }

    public String getRepertoire50NEW() {
        return repertoire50NEW;
    }

    public void setRepertoire50NEW(String repertoire50NEW) {
        this.repertoire50NEW = repertoire50NEW;
    }

    public String getRepertoireKidz() {
        return repertoireKidz;
    }

    public void setRepertoireKidz(String repertoireKidz) {
        this.repertoireKidz = repertoireKidz;
    }

    public long getThresholdNew() {
        return thresholdNew;
    }

    public void setThresholdNew(long thresholdNew) {
        this.thresholdNew = thresholdNew;
    }

    public List<String> getListeModelKidz() {
        return listeModelKidz;
    }

    public void setListeModelKidz(List<String> listeModelKidz) {
        this.listeModelKidz = listeModelKidz;
    }


    @Override
    public String toString() {
        return "TriNew{" +
                "repertoire50NEW='" + repertoire50NEW + '\'' +
                ", repertoireKidz='" + repertoireKidz + '\'' +
                ", repertoireBazar='" + repertoireBazar + '\'' +
                ", thresholdNew=" + thresholdNew +
                ", tempsAdherence='" + tempsAdherence + '\'' +
                ", listeModelKidz=" + listeModelKidz +
                '}';
    }
}

