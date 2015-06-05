/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.PEnd;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

/**
 *
 * @author Simon
 */
public class End {
    protected static final int P_DefeatVictory_X = 300;
    protected static final int P_DefeatVictory_Y = 100;
    protected static final int P_PLAY_X = 300;
    protected static final int P_PLAY_Y = 420;
    
    protected Image DefeatVictory;
    protected Image play;
    
    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.DefeatVictory, P_DefeatVictory_X, P_DefeatVictory_Y);
        g.drawImage(this.play, P_PLAY_X, P_PLAY_Y);
        g.drawString("Appuyer sur une touche", 300, 300);
    }
    
}
