/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.bullet;

import Slick2d.GameObject.Explosion;
import Slick2d.GameObject.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class AlphaStrick extends Bullet {

    private boolean strick = false;
    private Explosion e ;

    public AlphaStrick(Map map, int x, int y, int direction) throws SlickException {
        super(map, x, y, direction);
        this.map = map;
        this.x = x -16;
        this.width = 64;
        this.height = 64;
        this.y = y;
        e = new Explosion((int) this.x, (int) y);
        e.init();
        this.init();
    }

    @Override
    public void render(final Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(bullet, getX(), getY());   
        e.render(g);
    }

    @Override
    public void init() throws SlickException {
        
        bullet = new Image("src/ressources/bullet/glyphe64.png");
    }
    @Override
    public void update(final int delta) throws SlickException {
        collision = this.map.isCollision(this.x, this.y, this.width, this.height, this.direction);
    }
    public Explosion getExplosion()
    {
        return e;
    }
    public void setStrick(boolean b)
    {
        this.strick = b;
    }
    public boolean getStrick()
    {
        return strick;
    }

}
