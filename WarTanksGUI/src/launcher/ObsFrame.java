/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launcher;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Simon
 */
public class ObsFrame extends JFrame{
     public ObsFrame(){
       this.setTitle("Obs");
        JPanel panel = new JPanel();
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        //On prévient notre JFrame que notre JPanel sera son content pane
        
        GridLayout gl = new GridLayout(6, 2);
        gl.setHgap(5); //Cinq pixels d'espace entre les colonnes (H comme Horizontal)
        gl.setVgap(5); //Cinq pixels d'espace entre les lignes (V comme Vertical) 
        this.setLayout(gl);
        this.setResizable(false);
    }
}
