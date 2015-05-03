/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Defeat {
    private Image defeat;
    
    private static final int P_DEFEAT_X = 320;
    private static final int P_DEFEAT_Y = 220;

    public void init() throws SlickException {
       this.defeat = new Image("src/ressources/UI/defeat.png");
    }

    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.defeat, P_DEFEAT_X, P_DEFEAT_Y);
    }
}
