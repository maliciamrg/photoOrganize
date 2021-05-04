package com.malicia.mrg.view;

import com.malicia.mrg.app.rep.EleChamp;
import com.malicia.mrg.app.rep.blocRetourRepertoire;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class RenameRepertoire {
    private DefaultListModel listModellist1;
    private DefaultListModel listModellist2;
    private DefaultListModel listModellist3;
    private DefaultListModel listModellist4;
    private int numeroRetRep;
    private JButton button1;
    private JList list1;
    private JList list2;
    private JList list3;
    private JList list4;
    private JButton button2;
    private JTextField textField1;
    private JPanel panel1;
    private JComboBox comboBox1;

    private List<blocRetourRepertoire> retourRepertoire;

    public RenameRepertoire(List<blocRetourRepertoire> retourRepertoireOri) {
        this.retourRepertoire = retourRepertoireOri;
        numeroRetRep = 0;

        listModellist1 = new DefaultListModel();
        listModellist2 = new DefaultListModel();
        listModellist3 = new DefaultListModel();
        listModellist4 = new DefaultListModel();
        list1.setModel(listModellist1);
        list2.setModel(listModellist2);
        list3.setModel(listModellist3);
        list4.setModel(listModellist4);

        button2.setText("Next");
        button1.setText("Validate Name");
        majPanel(retourRepertoire);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numeroRetRep++;
                majPanel(retourRepertoire);
            }
        });

    }

    public static void start(List<blocRetourRepertoire> blocRetourRepertoire) {
        JFrame frame = new JFrame("rename Repertoire");
        frame.setContentPane(new RenameRepertoire(blocRetourRepertoire).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }

    private void majPanel(List<blocRetourRepertoire> retourRepertoire) {
        textField1.setText(retourRepertoire.get(numeroRetRep).getRepertoire());

        listModellist1.removeAllElements();
        listModellist2.removeAllElements();
        listModellist3.removeAllElements();
        listModellist4.removeAllElements();

        ListIterator<String> champIte;
        //Resutlat analyse reprertoire
        champIte = retourRepertoire.get(numeroRetRep).getListOfControleNom().get(0).getInfoRetourControle().listIterator();
        while (champIte.hasNext()) {
            listModellist1.addElement(champIte.next());
        }
        //Resutlat analyse reprertoire
        champIte = retourRepertoire.get(numeroRetRep).getListOfControleNom().get(1).getInfoRetourControle().listIterator();
        while (champIte.hasNext()) {
            listModellist2.addElement(champIte.next());
        }
        //Resutlat analyse reprertoire
        champIte = retourRepertoire.get(numeroRetRep).getListOfControleNom().get(2).getInfoRetourControle().listIterator();
        while (champIte.hasNext()) {
            listModellist3.addElement(champIte.next());
        }
        //Resutlat analyse reprertoire
        champIte = retourRepertoire.get(numeroRetRep).getListOfControleNom().get(3).getInfoRetourControle().listIterator();
        while (champIte.hasNext()) {
            listModellist4.addElement(champIte.next());
        }

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
