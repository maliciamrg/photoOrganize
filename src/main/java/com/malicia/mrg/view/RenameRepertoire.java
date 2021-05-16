package com.malicia.mrg.view;

import com.malicia.mrg.Context;
import com.malicia.mrg.app.WorkWithRepertory;
import com.malicia.mrg.app.rep.EleChamp;
import com.malicia.mrg.app.rep.blocRetourRepertoire;
import com.malicia.mrg.model.Database;
import com.malicia.mrg.param.importjson.ControleRepertoire;
import com.malicia.mrg.param.importjson.RepertoirePhoto;
import javafx.scene.canvas.GraphicsContext;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.malicia.mrg.param.importjson.ControleRepertoire.CARAC_SEPARATEUR;
import static com.malicia.mrg.param.importjson.ControleRepertoire.lstTAG;

public class RenameRepertoire {
    private static Database dbLr;
    private static Context ctx;
    private static Color bgOk = Color.GRAY;
    private static Color bgKO = Color.PINK;
    private static JFrame frame;
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
    private JLabel labCompteur;
    private JLabel img1;
    private JLabel img2;
    private JLabel img3;
    private JLabel img4;
    private List<blocRetourRepertoire> retourRepertoire;

    public RenameRepertoire(List<blocRetourRepertoire> retourRepertoireOri) {
        this.retourRepertoire = retourRepertoireOri;
        numeroRetRep = 0;
        bgOk = tree1.getBackground();

        //initialize button
        button1.setText("Validate Name");
        button2.setText("Next");

        //initializecombobox
        List<RepertoirePhoto> listRepPhoto = ctx.getArrayRepertoirePhoto();
        for (RepertoirePhoto o : listRepPhoto) {
            comboBox1.addItem(o);
        }


        majPanel(retourRepertoire);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numeroRetRep++;
                if (numeroRetRep >= retourRepertoire.size()) {
//                    numeroRetRep = retourRepertoire.size() - 1;
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
                majPanel(retourRepertoire);
            }
        });

        int i;
        for (i = 0; i < 4; i++) {
            tree[i].addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    enableButtonValidation();
                }
            });
            tree[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getButton() == 3) {
                        GeneratePopUpJTree(e);
                    }
                }
            });
        }

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                int i;
//                for (i = 0; i < 4; i++) {
//                    if (Arrays.asList(lstTAG).contains(tree[i].getLastSelectedPathComponent().toString())){
//                        createAndSelectNewTag(tree[i].getLastSelectedPathComponent().toString(),tree[i]);
//                    }
//                }

                String dest = ctx.getRepertoire50Phototheque() + File.separator
                        + comboBox1.getSelectedItem().toString() + File.separator
                        + tree[0].getLastSelectedPathComponent().toString() + CARAC_SEPARATEUR
                        + tree[1].getLastSelectedPathComponent().toString() + CARAC_SEPARATEUR
                        + tree[2].getLastSelectedPathComponent().toString() + CARAC_SEPARATEUR
                        + tree[3].getLastSelectedPathComponent().toString();

                int ret = JOptionPane.showConfirmDialog(frame, "Done : " + System.lineSeparator() + textField1.getText() + System.lineSeparator() + " to " + System.lineSeparator() + dest);
                if (ret == 0) {
                    try {
                        WorkWithRepertory.renommerRepertoire(textField1.getText(), dest, dbLr);
                        button2.doClick();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

    }

    public static void start(Database dbLr, Context ctx, List<blocRetourRepertoire> blRetourRepertoire) {
        RenameRepertoire.dbLr = dbLr;
        RenameRepertoire.ctx = ctx;
        frame = new JFrame("rename Repertoire");
        frame.setContentPane(new RenameRepertoire(blRetourRepertoire).panel1);
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

    private void GeneratePopUpJTree(MouseEvent e) {
        // create a popup menu
        JPopupMenu pm = new JPopupMenu("Message");

        int i;
        for (i = 0; i < ((JTree) e.getComponent()).getModel().getChildCount(((JTree) e.getComponent()).getModel().getRoot()); i++) {
            String lblTag = ((JTree) e.getComponent()).getModel().getChild(((JTree) e.getComponent()).getModel().getRoot(), i).toString();

            if (Arrays.asList(lstTAG).contains(lblTag)) {

                // create a label
                JLabel l = new JLabel("add new Tag :" + lblTag);
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        String newTag = JOptionPane.showInputDialog("new Tag pour le Parent " + lblTag);
                        if (newTag != null) {
                            if (newTag.length() > 0) {
                                try {
                                    dbLr.sqlcreateKeyword(ControleRepertoire.nettoyageTag(lblTag), newTag);
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                majPanel(retourRepertoire);
                            }
                        }
                    }
                });
                // add the label to the popup
                pm.add(l);

            }
        }

        // add the popup to the frame
        pm.show(frame, frame.getMousePosition().x, frame.getMousePosition().y);
    }

    private void majPanel(List<blocRetourRepertoire> retourRepertoire) {
        try {
            blocRetourRepertoire retourRepertoireEle = retourRepertoire.get(numeroRetRep);

            //enable button validate
            button1.setEnabled(false);

            //repertoire actuel
            textField1.setText(retourRepertoireEle.getRepertoire());

            //combobox reprtoire
            comboBox1.setSelectedItem(retourRepertoireEle.getRepPhoto());

            //imagepreview
            img1.setText("");
            Icon icon1 = getIcon(retourRepertoireEle, 0);
            img1.setIcon(icon1);
            img2.setText("");
            Icon icon2 = getIcon(retourRepertoireEle, 1);
            img2.setIcon(icon2);
            img3.setText("");
            Icon icon3 = getIcon(retourRepertoireEle, 2);
            img3.setIcon(icon3);
            img4.setText("");
            Icon icon4 = getIcon(retourRepertoireEle, 3);
            img4.setIcon(icon4);

            int size = retourRepertoireEle.lstPhoto.size();
            if (size == 0) {
                //compteur
                labCompteur.setText((numeroRetRep + 1) + "/" + retourRepertoire.size());

                button2.doClick();
            } else {
                //compteur
                labCompteur.setText((numeroRetRep + 1) + "/" + retourRepertoire.size() + " - " + size + " jpg ");

                //Alimentation Jtree
                List<EleChamp> listOfControleNom = retourRepertoireEle.getListOfControleNom();
                int i;
                for (i = 0; i < listOfControleNom.size(); i++) {

                    tree[i].clearSelection();
                    tree[i].getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

                    TreePath selectTree = setTreeModel(listOfControleNom, i);

                    expandAllNodes(tree[i], 0, tree[i].getRowCount());

                    if (listOfControleNom.get(i).isRetourControle()) {
                        tree[i].setBackground(bgOk);
                        if (selectTree != null) {
                            tree[i].setSelectionPath(selectTree);
                            tree[i].scrollPathToVisible(selectTree);
                        }
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

                enableButtonValidation();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Icon getIcon(blocRetourRepertoire retourRepertoireEle, int index) {
        Icon icon;
        Icon imageError = resizeImageIcon(new ImageIcon(Context.ERR_404_JPG), null);
        if (index < retourRepertoireEle.previewPhoto.size()) {
            icon = resizeImageIcon(new ImageIcon(retourRepertoireEle.previewPhoto.get(index)), imageError);
        } else {
            icon = imageError;
        }
        return icon;
    }

    private TreePath setTreeModel(List<EleChamp> listOfControleNom, int i) throws SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        List<String> ele = listOfControleNom.get(i).getInfoRetourControle();
        TreePath selectTree = null;
        for (String elechamp : ele) {
            root.add(new DefaultMutableTreeNode(elechamp));
            DefaultMutableTreeNode enc = root.getLastLeaf();
            String[] streamNames = sortList(dbLr.getValueForKeyword(ControleRepertoire.nettoyageTag(elechamp))).toArray(new String[0]);
            for (Object o : streamNames) {
                enc.add(new DefaultMutableTreeNode(o));
                if (o.equals(listOfControleNom.get(i).getoValue())) {
                    selectTree = new TreePath(enc.getLastLeaf().getPath());
                }
            }
            if (elechamp.equals(listOfControleNom.get(i).getoValue())) {
                selectTree = new TreePath(enc.getLastLeaf().getPath());
            }
        }
        treeModelArray[i] = new DefaultTreeModel(root);
        tree[i].setModel(treeModelArray[i]);
        return selectTree;
    }

    private void enableButtonValidation() {
        int i;
        //enable button validate
        boolean readyToValide = true;
        for (i = 0; i < 4; i++) {
            readyToValide = readyToValide && !tree[i].isSelectionEmpty();
        }
        if (readyToValide) {
            button1.setEnabled(true);
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

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private Icon resizeImageIcon(ImageIcon imageIcon, Icon imageIconError) {
        Image image = imageIcon.getImage(); // transform it
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int coef = (Context.INT_WIDTH * 100) / w;
        int width = w * coef / 100;
        int height = h * coef / 100;
        Image newimg;
        if (width < 10 || height < 10) {
            return imageIconError;
        } else {
            newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        }
        return new ImageIcon(newimg);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
