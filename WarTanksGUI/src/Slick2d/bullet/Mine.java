/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.bullet;

import Slick2d.GameObject.Map;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Mine extends Bullet {

    public Mine(Map map, int x, int y, int direction) throws SlickException {
        super(map, x, y, direction);
        this.map = map;
        this.x = x + 32;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.init();
    }

    @Override
    public void render(Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(bullet, getX() + 8, getY() + 8);
    }

    @Override
    void init() throws SlickException {
        bullet = new Image("src/ressources/bullet/2.png");
    }

    @Override
    public void update(final int delta) throws SlickException {
        collision = this.map.isCollision(this.x, this.y, this.width, this.height, this.direction);
    }
}
