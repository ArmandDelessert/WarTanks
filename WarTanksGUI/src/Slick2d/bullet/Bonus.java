/*
 -----------------------------------------------------------------------------------
 Fichier     : bonus.java
 Auteur(s)   : Simon Baehle
 Date        : 11 mai 2015

 But         : class gérant les bonus qui aparaisse sur la map
               l'emplacement des bonus sera généré aleatoirement par le serveur

 Remarque(s) :
 -----------------------------------------------------------------------------------
 */
package Slick2d.bullet;

import Slick2d.Map;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Bonus {
    
    int avaliable = -1; //-1 si le bonus est au sol
    public String name = "bonus";
    private float x = 200, y = 200;
    private Image bonus;
    private int type = 1;

    private Map map;
    
    private final int height = 32;
    private final int width = 32;
    

    public Bonus(Map map,int type,int x, int y) {
        this.map = map;
        this.x = x;
        this.y = y;
        this.type = type;
        name = name + type;
    }

    public void init() throws SlickException {
        //chargement de l'image enb fonction du type de bonus généré
        bonus = new Image("src/ressources/UI/bonus/"+ type +".bmp");
    }


    public void render(Graphics g) throws SlickException {
        //rendu
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(bonus, x, y);
    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
    public int getType()
    {
        return type;
    }
    public void setAvaliable(int a)
    {
        this.avaliable = a;
    }
    public int getAvaliable()
    {
        return avaliable;
    }
}
