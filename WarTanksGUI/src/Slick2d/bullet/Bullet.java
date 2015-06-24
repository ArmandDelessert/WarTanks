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
public class Bullet {

    //nombre de fois que le spell est disponible
    protected int avaliable = 0;
    float x = 0;
    float y = 0;
    private final float speed;
    int direction = 0;
    private boolean moving = true;
    Image bullet;
    boolean collision = false;

    Map map;

    int height = 16;
    int width = 6;

    static final int UP = 0;
    static final int LEFT = 1;
    static final int DOWN = 2;
    static final int RIGHT = 3;

    public Bullet(Map map, int x, int y, int direction) throws SlickException {
        this.map = map;
        speed = .2f;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.init();
    }

    void init() throws SlickException {
        bullet = new Image("src/ressources/bullet/1.png");
        if (direction == 0) {
            bullet.rotate(-90);
            this.x += 9;
        }
        if (direction == 1) {
            bullet.rotate(180);
        }
        if (direction == 2) {
            bullet.rotate(90);
            this.x += 9;
        }

    }

    public void render(Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawImage(bullet, x, y + 13);
    }

    public void update(final int delta) throws SlickException {
        if (this.moving) {
            float futurX = getFuturX(delta);
            float futurY = getFuturY(delta);
            
            //quand isCollison serra debug il faudra changer cette fonction
            //car les bullet s'arreterons en cas d'obstacle
            collision = this.map.isCollision(futurX, futurY, this.width, this.height, this.direction);
            if (collision) {
                this.moving = false;
            } else {
                this.x = futurX;
                this.y = futurY;
            }
            switch (this.direction) {
                case UP:
                    this.y -= speed * delta;
                    break;
                case LEFT:
                    this.x -= speed * delta;
                    break;
                case DOWN:
                    this.y += speed * delta;
                    break;
                case RIGHT:
                    this.x += speed * delta;
                    break;
            }
        }
    }

    float getFuturX(int delta) {
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

    float getFuturY(int delta) {
        float futurY = this.y;
        switch (this.direction) {
            case UP:
                futurY = this.y - .1f * delta;
                break;
            case DOWN:
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
    public boolean getCollison()
    {
        return collision;
    }
    Image getImage()
    {
        return bullet;
    }
    void setImage(Image i)
    {
       this.bullet =  i;
    }
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
