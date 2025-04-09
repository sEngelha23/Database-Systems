package edu.hsog.db;

import javax.swing.*;

import java.awt.*;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Hello world!");
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        JFrame frame = generateJFrame();
        frame.setVisible(true);
        Globals.initConnectionPool();
    }
    public static JFrame generateJFrame() {
// An object of our GUI, which we add to our master container
        GUI gui = new GUI();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(gui.getMasterPanel());
        frame.setSize(800,600);
        gui.getMasterPanel().setPreferredSize(new Dimension(800, 600)); // For testing!!!
        return frame;
    }

}