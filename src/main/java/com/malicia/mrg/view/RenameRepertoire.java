package com.malicia.mrg.view;

import com.malicia.mrg.app.rep.blocRetourRepertoire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class RenameRepertoire {
    private JButton button1;
    private JList list1;
    private JList list2;
    private JList list3;
    private JList list4;
    private JButton button2;
    private JTextField textField1;
    private JPanel panel1;
    private JComboBox comboBox1;

    private static HashMap<String, Component> componentMap;

    public RenameRepertoire() {
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "button press");
            }
        });
    }

    public static void start(blocRetourRepertoire blocRetourRepertoire) {

//        list1 = new JList(blocRetourRepertoire.getListOfControleNom().toArray());
//        list2 = new JList(blocRetourRepertoire.getListOfControleNom().toArray());
//        list3 = new JList(blocRetourRepertoire.getListOfControleNom().toArray());

        JFrame frame = new JFrame("rename Repertoire");
        frame.setContentPane(new RenameRepertoire().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        createComponentMap(frame);
        JList jl = (JList) componentMap.get("list1");
        jl = new JList(blocRetourRepertoire.getListOfControleNom().toArray());


        frame.revalidate();
        frame.repaint();
    }

    private static void createComponentMap(JFrame frame) {
        componentMap = new HashMap<String,Component>();
        Component[] components = frame.getContentPane().getComponents();
        for (int i=0; i < components.length; i++) {
            componentMap.put(components[i].getName(), components[i]);
        }
    }

}
