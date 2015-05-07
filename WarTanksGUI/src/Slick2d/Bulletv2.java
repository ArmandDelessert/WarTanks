/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Simon
 */
public class Bulletv2 {

    private float x = 300, y = 300;
    private int HP;
    private float speed;
    private int direction = 0;
    private boolean moving = false;
    Image bullet;

    private Map map;

    public Bulletv2(Map map) {
        this.map = map;
        speed = .1f;
    }

    public void init() throws SlickException {

    }
    public void render(Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawOval(x, y, 10, 10);
    }

    public void update(int delta) throws SlickException {
        if (HP > 0) {
            if (this.moving) {
                float futurX = getFuturX(delta);
                float futurY = getFuturY(delta);
                boolean collision = this.map.isCollision(futurX, futurY);
                
                boolean slowed = this.map.isSlowed(futurX, futurY);
                if (collision) {
                    this.moving = false;
                } else {
                    this.x = futurX;
                    this.y = futurY;
                }

                if (slowed) {
                    speed = 0.02f;
                } else {
                    speed = 0.1f;
                }
                switch (this.direction) {
                    case 0:
                        this.y -= speed * delta;
                        break;
                    case 1:
                        this.x -= speed * delta;
                        break;
                    case 2:
                        this.y += speed * delta;
                        break;
                    case 3:
                        this.x += speed * delta;
                        break;
                }
            }
        }else  {
            //mort

        }
    }

    public float getFuturX(int delta) {
        float futurX = this.x;
        switch (this.direction) {
            case 1:
                futurX = this.x - .1f * delta;
                break;
            case 3:
                futurX = this.x + .1f * delta;
                break;
        }
        return futurX;
    }

    public float getFuturY(int delta) {
        float futurY = this.y;
        switch (this.direction) {
            case 0:
                futurY = this.y - .1f * delta;
                break;
            case 2:
                futurY = this.y + .1f * delta;
                break;
        }
        return futurY;
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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}
