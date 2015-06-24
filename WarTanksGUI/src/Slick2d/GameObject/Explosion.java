/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.GameObject;

import java.util.Timer;
import java.util.TimerTask;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Simon
 */
public class Explosion {

    int playerID = 1; //sera attribuer par le serveur
    private float x = 300, y = 300;
    private Sound shot;
    private boolean finished = false;

    private final Animation[] animations = new Animation[4];


    private final int height;
    private final int width;

    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    public Explosion(int x, int y) throws SlickException {
        this.x = x;
        this.y = y;
        this.height = this.width = 64;
        shot = new Sound("src/ressources/sound/DeathFlash.ogg");
    }

    public void init() throws SlickException {
        SpriteSheet spriteSheet = new SpriteSheet("src/ressources/bullet/explosion1.png", this.width, this.height);
        this.animations[0] = loadAnimation(spriteSheet, 0, 31, 0);

    }

    private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
        Animation animation = new Animation();
        
        for (int x = startX; x < 14; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 50);
        }
        for (int x = 15; x < 31; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 100);
        }
        return animation;
    }

    public synchronized void render(Graphics g) throws SlickException {
        if (!finished) {
            g.drawAnimation(animations[0], x, y);
        }
        TimerTask task;
        task = new TimerTask() {
            @Override
            public void run() {
                finished = true;
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2200);
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
    public boolean isFinished()
    {
        return finished;
    }
}
