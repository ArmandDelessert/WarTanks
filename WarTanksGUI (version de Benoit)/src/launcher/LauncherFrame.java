package launcher;
/*
 --------------------------------------------------------------------------------
 Laboratoire : Laboratoire 02
 Fichier     : Fenetre.java
 Auteur(s)   : Baehler & Gardel 
 Date        : 19 mars 2014

 But         : Application qui génère des cercles et des carrés quand nous
 appuyons sur une touche
 --------------------------------------------------------------------------------
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LauncherFrame extends JFrame implements ActionListener {

    private JPanel pan = new JPanel();
    private JButton btnCreate = new JButton("Create a game");
    private JButton btnJoin = new JButton("Join a game");
    private JButton btnObs = new JButton("Observat a game");
    private JButton btnQuit = new JButton("Quit");
    private JButton btnHelp = new JButton("Help");

    public LauncherFrame() throws IOException {
        this.setTitle("WarTanks");
        JPanel panel = new JPanel();
        this.setSize(400, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        //On prévient notre JFrame que notre JPanel sera son content pane

        GridLayout gl = new GridLayout(6, 1);
        gl.setHgap(5); //Cinq pixels d'espace entre les colonnes (H comme Horizontal)
        gl.setVgap(5); //Cinq pixels d'espace entre les lignes (V comme Vertical) 
        this.setLayout(gl);
        this.getContentPane().add(panel);
        this.getContentPane().add(btnCreate);
        this.getContentPane().add(btnJoin);
        this.getContentPane().add(btnObs);
        this.getContentPane().add(btnQuit);
        this.getContentPane().add(btnHelp);
        btnCreate.addActionListener(this);
        btnJoin.addActionListener(this);
        btnObs.addActionListener(this);
        btnHelp.addActionListener(this);
        btnQuit.addActionListener(this);
        this.setResizable(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((JButton) e.getSource() == btnCreate) {
            CreateFrame create = new CreateFrame();
        }
        if ((JButton) e.getSource() == btnJoin) {
            JoinFrame join = new JoinFrame();
        }
        if ((JButton) e.getSource() == btnObs) {
            ObsFrame obs = new ObsFrame();
        }
        if ((JButton) e.getSource() == btnHelp) {
            HelpFrame obs = new HelpFrame();
        }
        if ((JButton) e.getSource() == btnQuit) {
            System.exit(0);
        }
    }
}
