
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.HUD;

import Slick2d.Fightable.Player;
import Slick2d.bullet.Bonus;
import Slick2d.Fightable.Ennemy;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * this.defeat = new Image("src/ressources/UI/defeat.png");
 *
 * @author Simon
 */
public class Hud implements Observer {

    private Image playerbars;
    private Image actionbar;
    private Image lifebar;
    private Player p;
    private LinkedList listBonusImg = new LinkedList();
    private LinkedList shortCut = new LinkedList();
    private LinkedList AmountAmmo = new LinkedList();
    private LinkedList ListEnnemy = new LinkedList();
    private MouseOverArea[] areas = new MouseOverArea[4];

    private static final int P_BAR_X = 10;
    private static final int P_BAR_Y = 10;

    private static final int E_BAR_X = 10;
    private static final int E_BAR_Y = 120;

    private static final int P_BAR2_X = 210;
    private static final int P_BAR2_Y = 525;

    private static final int P_BONUS_X = 255;
    private static final int P_BONUS_Y = 525 + 35;
    GameContainer container;

    public Hud(GameContainer container) {
        this.container = container;
    }

    public void init() throws SlickException {
        listBonusImg.clear();
        this.playerbars = new Image("src/ressources/UI/player-bar2.png");
        this.lifebar = new Image("src/ressources/UI/lifebar.png");
        this.actionbar = new Image("src/ressources/UI/action-bar.png");
        shortCut.add(new Image("src/ressources/UI/shortcuts/Q.png"));
        shortCut.add(new Image("src/ressources/UI/shortcuts/W.png"));
        shortCut.add(new Image("src/ressources/UI/shortcuts/E.png"));
        shortCut.add(new Image("src/ressources/UI/shortcuts/R.png"));
        shortCut.add(new Image("src/ressources/UI/shortcuts/T.png"));

    }

    public void render(Graphics g) {
        g.resetTransform();
        g.drawImage(this.playerbars, P_BAR_X, P_BAR_Y);
        g.drawImage(this.actionbar, P_BAR2_X, P_BAR2_Y);
        for (int i = 0; i < listBonusImg.size(); i++) {
            g.drawImage((Image) listBonusImg.get(i), P_BONUS_X + i * 38, P_BONUS_Y);
            g.drawImage((Image) shortCut.get(i), P_BONUS_X + i * 38, P_BONUS_Y);
            g.drawString(AmountAmmo.get(i).toString(), P_BONUS_X + i * 38, P_BONUS_Y + 18);
        }
        for (int i = 0; i < ListEnnemy.size(); i++) {
            if (((Ennemy) ListEnnemy.get(i)).getHP() > 0) {
                g.fillRect(E_BAR_X, E_BAR_Y + i * 15, ((Ennemy) ListEnnemy.get(i)).getHP() * 40, 10, lifebar, 0, 0);
                g.drawRect(E_BAR_X, E_BAR_Y + i * 15, 120, 10);
            } else {
                g.drawRect(E_BAR_X, E_BAR_Y + i * 15, 120, 10);
            }

        }
        for (int i = 0; i < 4; i++) {
            if (areas[i] != null) {
                areas[i].render(container, g);
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        System.out.println("Notified");

        try {
            listBonusImg.add(new Image("src/ressources/UI/bonus/bonus" + ((Bonus) arg).getType() + ".png"));
            AmountAmmo.add(((Bonus) arg).getAvaliable());
        } catch (SlickException ex) {
            Logger.getLogger(Hud.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LinkedList getListBonus() {
        return listBonusImg;
    }

    public void setLisEnnemyList(LinkedList l) {
        this.ListEnnemy = l;
    }
}