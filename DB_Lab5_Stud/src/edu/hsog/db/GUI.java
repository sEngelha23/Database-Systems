package edu.hsog.db;

import javax.swing.*;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;

public class GUI {
    public GUI() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        InitConPool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Globals.initConnectionPool();
            }
        });
        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int c = DBQueries.count();
                countLabel.setText("Count: " + c);

            }
        });
        ImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setName("imageJFch");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
//Hier erscheint das Bild im jLabel2
                    Icon icon = Converter.loadIconFromFile(selectedFile.getAbsolutePath());
                    ImageLabel.setIcon(icon);
                    ImageLabel.setText("");
                }
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = username.getText();
                String passwd = password.getText();

                boolean log = DBQueries.login(user, passwd);

                if (log) StatusLabel.setText("logged in");
                else StatusLabel.setText("not logged in");
            }
        });
    }

    private JPanel MasterPanel;
    private JButton exitButton;
    private JButton InitConPool;
    private JButton countButton;
    private JLabel countLabel;
    private JLabel ImageLabel;
    private JButton ImageButton;
    private JTextField username;
    private JTextField password;
    private JButton loginButton;
    private JLabel StatusLabel;


    public JPanel getMasterPanel() {
        return MasterPanel;
    }
}
