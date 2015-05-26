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
 * @author Simon
 */
public class Mine extends Bullet {

    private Image mine;
    private Map map;
    private boolean collision;
    private final float height = 16;
    private final float width = 16;
    private float x = 0;
    private float y = 0;
    

    public Mine(Map map, int x, int y, int direction) throws SlickException {
        super(map, x, y, direction);
        this.map = map;
        this.x = x;
        this.y = y;
        this.init();
    }

    @Override
    public void render(Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(mine, getX() + 8, getY() + 8);
    }

    @Override
    public void init() throws SlickException {
        mine = new Image("src/ressources/bullet/2.png");
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
    boolean getCollison()
    {
        return collision;
    }

}
