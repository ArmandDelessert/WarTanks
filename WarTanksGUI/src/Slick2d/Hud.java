/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * this.defeat = new Image("src/ressources/UI/defeat.png");
 *
 * @author Simon
 */
public class Hud implements Observer {

    private Image playerbars;
    private Image actionbar;
    private LinkedList listBonusImg = new LinkedList();
    private LinkedList AmountAmmo = new LinkedList();
    private Image bonus;
    private static final int P_BAR_X = 10;
    private static final int P_BAR_Y = 10;
    private static final int P_BAR2_X = 210;
    private static final int P_BAR2_Y = 525;

    private static final int P_BONUS_X = 255;
    private static final int P_BONUS_Y = 525+35;

    
    

    public void init() throws SlickException {
        this.playerbars = new Image("src/ressources/UI/player-bar2.png");
        this.actionbar = new Image("src/ressources/UI/action-bar.png");
    }

    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.playerbars, P_BAR_X, P_BAR_Y);
        g.drawImage(this.actionbar, P_BAR2_X, P_BAR2_Y);
        for(int i = 0; i < listBonusImg.size() ; i++)
        {
          g.drawImage((Image) listBonusImg.get(i), P_BONUS_X + i * 38, P_BONUS_Y  );
          g.drawString(AmountAmmo.get(i).toString(), P_BONUS_X + i * 38, P_BONUS_Y + 18);
        }
        
    }
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Notified");
        try {
            listBonusImg.add(new Image("src/ressources/UI/bonus/bonus" + ((Bonus)arg).getType() + ".png"));
            AmountAmmo.add(((Bonus)arg).getAvaliable());
        } catch (SlickException ex) {
            Logger.getLogger(Hud.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public LinkedList getListBonus()
    {
        return listBonusImg;
    }

}
