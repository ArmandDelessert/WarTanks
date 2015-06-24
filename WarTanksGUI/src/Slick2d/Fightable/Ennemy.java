/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.Fightable;

import Slick2d.GameObject.Explosion;
import Slick2d.GameObject.Map;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Simon
 */
public class Ennemy implements IFightable{

    private int playerID = 1; //sera attribuer par le serveur
    private final String name;
    private float x = 350, y = 350;
    private Explosion e;
    private int HP;
    private boolean kaboomed;
    private float speed;
    private int direction = 0;
    private boolean moving = false;
    private final Animation[] animations = new Animation[8];
    private Animation[] diedAnimation = new Animation[32];

    private final Map map;

    private final int height = 32;
    private final int width = 32;

    private static final int UP = 0;
    private static final int LEFT = 1;
    private static final int DOWN = 2;
    private static final int RIGHT = 3;

    public Ennemy(Map map, int x, int y, int randDirection, int id) {
        this.x = x;
        kaboomed = false;
        this.name = "Ennemy " + id;
        this.y = y;
        this.direction = randDirection;
        this.map = map;
        HP = 3;
        playerID = id;
        speed = .1f;
    }

    public void init() throws SlickException {
        SpriteSheet spriteSheet = new SpriteSheet("src/ressources/tanks/MulticolorTanksRed.png", this.width, this.height);
        this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
        this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
        this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
        this.animations[4] = loadAnimation(spriteSheet, 1, 8, 0);
        this.animations[5] = loadAnimation(spriteSheet, 1, 8, 1);
        this.animations[6] = loadAnimation(spriteSheet, 1, 8, 2);
        this.animations[7] = loadAnimation(spriteSheet, 1, 8, 3);
        e = new Explosion((int) x-16, (int) y-16);
        e.init();
    }

    private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
        Animation animation = new Animation();
        for (int x = startX; x < endX; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 100);
        }
        return animation;
    }

    public void render(Graphics g) throws SlickException {
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawAnimation(animations[direction + (moving ? 4 : 0)], x, y);
        if (HP == 0) {
            e.setX(x-16);
            e.setY(y-16);
            e.render(g);
        }
        kaboomed = true;
    }

    @Override
    public void update(int delta) throws SlickException {
        if (HP > 0) {
            if (this.moving) {
                float futurX = getFuturX(delta);
                float futurY = getFuturY(delta);
                boolean collision = this.map.isCollision(futurX, futurY, this.width, this.height, this.direction);
                boolean slowed = this.map.isCollision(futurX, futurY, this.width, this.height, this.direction);
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
        } else {
            SpriteSheet spriteSheet = new SpriteSheet("src/ressources/tanks/MulticolorTanksBlack.png", this.width, this.height);
            this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
            this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
            this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
            this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
            this.animations[4] = loadAnimation(spriteSheet, 1, 8, 0);
            this.animations[5] = loadAnimation(spriteSheet, 1, 8, 1);
            this.animations[6] = loadAnimation(spriteSheet, 1, 8, 2);
            this.animations[7] = loadAnimation(spriteSheet, 1, 8, 3);

        }
    }

    @Override
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

    @Override
    public float getFuturY(int delta) {
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

    public int getDirection() {
        return direction;
    }

    @Override
    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void setHp() {
        if(HP == 0)
        {
            HP = 0;
        }else{
            HP--;
        }
    }

    public void setHp(int HP) {
        this.HP = HP;
    }

    public int getHP() {
        return HP;
    }
}
