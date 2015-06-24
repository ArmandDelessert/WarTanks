/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.bullet;

import Slick2d.Fightable.Player;
import Slick2d.GameObject.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Laser extends Bullet {

    private boolean strick = false;
    private boolean finished = false;

    private Player launcher;

    public Laser(Map map, int x, int y, int direction, Player p) throws SlickException {
        super(map, x, y, direction);
        launcher = p;
        if (direction == UP || direction == DOWN) {
            height = 200;
            width = 32;
        }
        if (direction == RIGHT || direction == LEFT) {
            height = 32;
            width = 200;
        }
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public void render(final Graphics g) throws SlickException {
        if (!finished) {
            g.setColor(new Color(0, 0, 0, .5f));
            if (launcher.getDirection() == UP || launcher.getDirection() == DOWN) {
                height = 200;
                width = 32;
            }
            if (launcher.getDirection() == RIGHT || launcher.getDirection() == LEFT) {
                height = 32;
                width = 200;
            }
            if (launcher.getDirection() == UP) {
                bullet = new Image("src/ressources/bullet/laser.png");
                g.fillRect(x, y, width, height);
                g.fillRect(x, y, width, height, bullet, 0, 0);
            }
            if (launcher.getDirection() == DOWN) {
                bullet = new Image("src/ressources/bullet/laser.png");
                g.fillRect(x, y, width, height);
                g.fillRect(x, y, width, height, bullet, 0, 0);
            }
            if (launcher.getDirection() == RIGHT) {
                bullet = new Image("src/ressources/bullet/laserRight.png");
                g.fillRect(x, y, width, height);
                g.fillRect(x, y, width, height, bullet, 0, 0);
            }
            if (launcher.getDirection() == LEFT) {
                bullet = new Image("src/ressources/bullet/laserRight.png");
                g.fillRect(x, y, width, height);
                g.fillRect(x, y, width, height, bullet, 0, 0);
            }
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

    @Override
    public void init() throws SlickException {
        bullet = new Image("src/ressources/bullet/laser.png");

    }
    public boolean getFinished()
    {
        return finished;
    }

    public void setStrick(boolean b) {
        this.strick = b;
    }

    public boolean getStrick() {
        return strick;
    }

    @Override
    public void update(final int delta) throws SlickException {
        if (direction == UP || direction == DOWN) {
            height = 200;
            width = 32;
        }
        if (direction == RIGHT || direction == LEFT) {
            height = 32;
            width = 200;
        }

        collision = this.map.isCollision(launcher.getX(), launcher.getY(), this.width, this.height, this.direction);
        this.x = launcher.getX();
        this.y = launcher.getY();

        if (launcher.getDirection() == RIGHT) {
           this.x = launcher.getX() + 32;
            this.y = launcher.getY();
        }
        if (launcher.getDirection() == LEFT) {
            this.x = launcher.getX() - 200;
            this.y = launcher.getY();
        }
        if(launcher.getDirection() == UP) {
            this.x = launcher.getX() ;
            this.y = launcher.getY() -200;
        }
        if(launcher.getDirection() == DOWN) {
            this.x = launcher.getX() +32 ;
            this.y = launcher.getY();
        }
    }
}
