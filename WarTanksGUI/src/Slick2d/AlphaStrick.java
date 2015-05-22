/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class AlphaStrick extends Bullet {

    private Image glyphe;
    private Map map;
    private boolean collision;
    private final float height = 64;
    private final float width = 64;
    private Explosion e ;
    private float x = 0;
    private float y = 0;

    public AlphaStrick(Map map, int x, int y, int direction) throws SlickException {
        super(map, x, y, direction);
        this.map = map;
        this.x = x -16;
        this.y = y;
        e = new Explosion(map, (int) this.x, (int) y);
        e.init();
        this.init();
    }

    @Override
    public void render(final Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(glyphe, getX(), getY());   
        e.render(g);
    }

    @Override
    public void init() throws SlickException {
        
        glyphe = new Image("src/ressources/bullet/glyphe64.png");
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    float getWidth() {
        return width;
    }

    @Override
    float getHeight() {
        return height;
    }

    @Override
    boolean getCollison() {
        return collision;
    }
    public Explosion getExplosion()
    {
        return e;
    }

}
