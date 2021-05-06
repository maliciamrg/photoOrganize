package com.malicia.mrg.view;

import com.malicia.mrg.Context;
import com.malicia.mrg.app.rep.EleChamp;
import com.malicia.mrg.app.rep.blocRetourRepertoire;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.RepertoirePhoto;


import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class RenameRepertoire {
    private static Database dbLr;
    private static Context ctx;
    private static Color bgOk = Color.GRAY;
    private static Color bgKO = Color.PINK;
    private TreeModel treeModel1;
    private TreeModel treeModel2;
    private TreeModel treeModel3;
    private TreeModel treeModel4;
    private final TreeModel[] treeModelArray = {treeModel1, treeModel2, treeModel3, treeModel4};
    private int numeroRetRep;
    private JButton button1;
    private JButton button2;
    private JTextField textField1;
    private JPanel panel1;
    private JComboBox comboBox1;
    private JTree tree1;
    private JTree tree2;
    private JTree tree3;
    private JTree tree4;
    private final JTree[] tree = {tree1, tree2, tree3, tree4};
    private List<blocRetourRepertoire> retourRepertoire;

    public RenameRepertoire(List<blocRetourRepertoire> retourRepertoireOri) {
        this.retourRepertoire = retourRepertoireOri;
        numeroRetRep = 0;
        bgOk=tree1.getBackground();


        button1.setText("Validate Name");
        button2.setText("Next");

        List<RepertoirePhoto> listRepPhoto = ctx.getArrayRepertoirePhoto();
        for (RepertoirePhoto o : listRepPhoto) {
            comboBox1.addItem(o);
        }


        majPanel(retourRepertoire);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numeroRetRep++;
                if (numeroRetRep>=retourRepertoire.size()){numeroRetRep=retourRepertoire.size()-1;}
                majPanel(retourRepertoire);
            }
        });

    }

    public static void start(Database dbLr, Context ctx, List<blocRetourRepertoire> blocRetourRepertoire) {
        RenameRepertoire.dbLr = dbLr;
        RenameRepertoire.ctx = ctx;
        JFrame frame = new JFrame("rename Repertoire");
        frame.setContentPane(new RenameRepertoire(blocRetourRepertoire).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }

    private static List<String> prefixAllElementsList(String prefix, List<String> valueForKeyword) {
        List<String> listRetour = new ArrayList<>();

        ListIterator<String> valueForKeywordIterator = valueForKeyword.listIterator();
        while (valueForKeywordIterator.hasNext()) {
            String ele = valueForKeywordIterator.next();
            listRetour.add(prefix + ele);
        }

        return listRetour;
    }

    private void majPanel(List<blocRetourRepertoire> retourRepertoire) {
        try {
            blocRetourRepertoire blocRetourRepertoire = retourRepertoire.get(numeroRetRep);

            //repertoire actuel
            textField1.setText(blocRetourRepertoire.getRepertoire());

            //combobox reprtoire
            comboBox1.setSelectedItem(blocRetourRepertoire.getRepPhoto());

            //Alimentation Jtree
            List<EleChamp> listOfControleNom = blocRetourRepertoire.getListOfControleNom();
            int i;
            for (i = 0; i < listOfControleNom.size(); i++) {
                tree[i].clearSelection();
                tree[i].getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
                List<String> ele = listOfControleNom.get(i).getInfoRetourControle();
                for (String elechamp : ele) {
                    root.add(new DefaultMutableTreeNode(elechamp));
                    DefaultMutableTreeNode enc = root.getLastLeaf();
                    String[] streamNames = sortList(dbLr.getValueForKeyword(elechamp)).toArray(new String[0]);
                    for (Object o : streamNames) {
                        enc.add(new DefaultMutableTreeNode(o));
                    }
                }
                treeModelArray[i] = new DefaultTreeModel(root);
                tree[i].setModel(treeModelArray[i]);
                expandAllNodes( tree[i], 0,  tree[i].getRowCount());
                if (listOfControleNom.get(i).isRetourControle()){
                    tree[i].setBackground(bgOk);
                } else {
                    tree[i].setBackground(bgKO);
                }
            }
            for (i = listOfControleNom.size(); i < 4; i++) {
                tree[i].clearSelection();
                tree[i].getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
                treeModelArray[i] = new DefaultTreeModel(root);
                tree[i].setModel(treeModelArray[i]);
                tree[i].setBackground(bgOk);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private List<String> sortList(List<String> listIn) {
        List<String> sortListOut = new ArrayList<>();
        String[] streamNames = listIn.toArray(new String[0]);
        Arrays.sort(streamNames);
        for (String o : streamNames) {
            sortListOut.add(o);
        }
        return sortListOut;
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
