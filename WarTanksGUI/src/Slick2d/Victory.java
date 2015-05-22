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
public class Victory {
    private Image victory;
    private Image play;
    
    private static final int P_DEFEAT_X = 320;
    private static final int P_DEFEAT_Y = 220;
    private static final int P_PLAY_X = 320;
    private static final int P_PLAY_Y = 420;

    public void init() throws SlickException {
       this.victory = new Image("src/ressources/UI/victory.png");
       this.play = new Image("src/ressources/UI/play.png");
    }

    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.victory, P_DEFEAT_X, P_DEFEAT_Y);
        g.drawImage(this.play, P_PLAY_X, P_PLAY_Y);
    }
}
