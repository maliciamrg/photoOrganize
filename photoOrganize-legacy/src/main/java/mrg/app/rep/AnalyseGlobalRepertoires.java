package mrg.app.rep;

import mrg.Context;
import mrg.model.Database;
import com.malicia.mrg.util.WhereIAm;
import mrg.view.RenameRepertoire;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AnalyseGlobalRepertoires {
    private static final Logger LOGGER = LogManager.getLogger(AnalyseGlobalRepertoires.class);
    private final List<BlocRetourRepertoire> listOfretourNomRepertoire;
    private final List<BlocRetourRepertoire> listOfretourValRepertoire;

    public AnalyseGlobalRepertoires() {
        this.listOfretourNomRepertoire = new ArrayList<>();
        this.listOfretourValRepertoire = new ArrayList<>();
    }

    //    public static BlocRetourRepertoire calculateLesEleChampsDuRepertoire(String repertoire, RepertoirePhoto repPhoto, ControleRepertoire paramControleRepertoire) throws SQLException, IOException {
//        LOGGER.debug("isRepertoireOk :  {}", repertoire);
//
//        BlocRetourRepertoire retourControleRep = new BlocRetourRepertoire(repPhoto, repertoire);
//
//        String ancienNomDuRepertoire = new File(repertoire).getName();
//        String[] oldChamp = ancienNomDuRepertoire.split(ControleRepertoire.CARAC_SEPARATEUR);
//
//        //controle nom du repertoire
//        List<EleChamp> listOfChampNom = new ArrayList<>();
//        int i = 0;
//        ListIterator<String> nomRepertoireIterator = repPhoto.getZoneValeurAdmise().listIterator();
//        while (nomRepertoireIterator.hasNext()) {
//            String valeurAdmise = nomRepertoireIterator.next();
//
//            EleChamp eChamp;
//            if (i < oldChamp.length) {
//                eChamp = new EleChamp(valeurAdmise, oldChamp[i]);
//            } else {
//                eChamp = new EleChamp(valeurAdmise, "");
//            }
//            BlocRetourRepertoire.controleChamp(repertoire, repPhoto, eChamp);
//            listOfChampNom.add(eChamp);
//            i++;
//        }
//        retourControleRep.setListOfControleNom(listOfChampNom);
//
//        //controle contenu du repertoire
//        List<EleChamp> listOfChampCtrl = new ArrayList<>();
//
//        ListIterator<String> listControleRepertoireIterator = paramControleRepertoire.getlistControleRepertoire().listIterator();
//        while (listControleRepertoireIterator.hasNext()) {
//            EleChamp eChamp = new EleChamp();
//            String ele = listControleRepertoireIterator.next();
//            eChamp.setcChamp(ele);
//            BlocRetourRepertoire.controleChamp(repertoire, repPhoto, eChamp);
//            listOfChampCtrl.add(eChamp);
//        }
//        retourControleRep.setListOfControleValRepertoire(listOfChampCtrl);
//
//
//        //Recuperer 4 preview du repertoire
//        retourControleRep.setPreviewPhoto(dbLr.getFourRandomPreviewPhoto(repertoire));
//
//        //Recuperer lstphoto du rep
//        retourControleRep.setLstPhoto(dbLr.getLstPhoto(repertoire));
//
//        return retourControleRep;
//
//    }
//
//    public static void init(Context ctxIn, Database dbLrIn) {
//        ctx = ctxIn;
//        dbLr = dbLrIn;
//    }

    public List<BlocRetourRepertoire> getListOfretourValRepertoire() {
        return listOfretourValRepertoire;
    }

    public void add(BlocRetourRepertoire retourRepertoire) {
        if (Boolean.FALSE.equals(retourRepertoire.getValOk())) {
            this.listOfretourValRepertoire.add(retourRepertoire);
        }
        if (Boolean.FALSE.equals(retourRepertoire.getNomOk()) && !retourRepertoire.getLstPhoto().isEmpty()) {
            this.listOfretourNomRepertoire.add(retourRepertoire);
        }

    }

    public void action(boolean makeAction, Context ctx, Database dbLr) {
        WhereIAm.displayWhereIAm(Thread.currentThread().getStackTrace()[1].getMethodName(), LOGGER);
        if (makeAction) {
            if (!listOfretourNomRepertoire.isEmpty()) {
                RenameRepertoire.start2(dbLr, ctx, listOfretourNomRepertoire);
//            RenameRepertoire.start(dbLr, ctx, listOfretourNomRepertoire);
            }
        }
    }

    @Override
    public String toString() {
        return "AnalyseGlobalRepertoires{" +
                "nbListOfretourNomRepertoire=" + listOfretourNomRepertoire.size() +
                ", nbListOfretourValRepertoire=" + listOfretourValRepertoire.size() +
                '}';
    }


    public String toDisplay() {
        final String[] retour = {"\n"};
        listOfretourNomRepertoire.forEach(
                (blocRetourRep) -> {
                    retour[0] += blocRetourRep.toString() + "\n" ;
                }
        );
        retour[0] += "\n";
        retour[0] += "\n";
        retour[0] += "\n";
        listOfretourValRepertoire.forEach(
                (blocRetourRep) -> {
                    retour[0] += blocRetourRep.toString() + "\n" ;
                }
        );
        return retour[0];
    }
}
