/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import java.util.LinkedList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Laser extends Bullet {

    private float height = 64;
    private boolean strick = false;
    private Image laser;
    private float width = 64;
    private int direction;
    
    private float x = 0;
    private float y = 0;

    public Laser(Map map, int x, int y, int direction) throws SlickException {
        super(map, x, y, direction);
        if (direction == UP || direction == DOWN) {
            height = 200;
            width = 32;
        }
        if(direction == RIGHT ||  direction == LEFT )
        {
            height = 32;
            width = 200;
        }
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public void render(final Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        //g.drawRect(getX(), getY()-200, width, height);
        if(direction == UP)
        {
            laser = new Image("src/ressources/bullet/laser.png");
            g.fillRect(getX(), getY()-200, width, height);
            g.fillRect(getX(), getY() - 200, width, height, laser, 0, 0);
        }
        if(direction == DOWN)
        {
            laser = new Image("src/ressources/bullet/laser.png");
            g.fillRect(getX(), getY()+32, width, height);
            g.fillRect(getX(), getY()+32, width, height, laser, 0, 0);
        }
        if(direction == RIGHT)
        {
            laser = new Image("src/ressources/bullet/laserRight.png");
            g.fillRect(getX()+32, getY(), width, height);
            g.fillRect(getX()+32, getY(), width, height, laser, 0, 0);
        }
        if(direction == LEFT)
        {
            laser = new Image("src/ressources/bullet/laserRight.png");
            g.fillRect(getX()-200, getY(), width, height);
            g.fillRect(getX()-200, getY(), width, height, laser, 0, 0);
        }
    }

    @Override
    public void init() throws SlickException {
        laser = new Image("src/ressources/bullet/laser.png");

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

    public void setStrick(boolean b) {
        this.strick = b;
    }

    public boolean getStrick() {
        return strick;
    }

}
