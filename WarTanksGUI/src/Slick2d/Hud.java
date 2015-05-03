/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 *         this.defeat = new Image("src/ressources/UI/defeat.png");
 * @author Simon
 */
public class Hud {

    private Image playerbars;
    private Image actionbar;
    private static final int P_BAR_X = 10;
    private static final int P_BAR_Y = 10;
    private static final int P_BAR2_X = 210;
    private static final int P_BAR2_Y = 536;
    

    public void init() throws SlickException {
        this.playerbars = new Image("src/ressources/UI/player-bar2.png");
        this.actionbar = new Image("src/ressources/UI/action-bar.png");
    }

    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.playerbars, P_BAR_X, P_BAR_Y);
        g.drawImage(this.actionbar, P_BAR2_X, P_BAR2_Y);
    }

}
