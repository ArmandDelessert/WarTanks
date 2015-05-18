/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Simon
 */
public class Player extends Observable {

    int playerID = 1; //sera attribuer par le serveur
    private float x = 300, y = 300;
    private int HP;
    private float speed;
    private int direction = 0;
    private boolean moving = false;
    private LinkedList listBonus = new LinkedList();
    private LinkedList listBullet = new LinkedList();
    private LinkedList listEnnemy = new LinkedList();

    private Animation[] animations = new Animation[8];

    private Map map;

    private final int height = 32;
    private final int width = 32;

    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    public Player(Map map, Observer o) {
        this.map = map;
        HP = 3;
        speed = .1f;
        this.addObserver(o);
    }

    public void init() throws SlickException {
        SpriteSheet spriteSheet = new SpriteSheet("src/ressources/tanks/MulticolorTanks2.png", this.width, this.height);
        this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
        this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
        this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
        this.animations[4] = loadAnimation(spriteSheet, 1, 8, 0);
        this.animations[5] = loadAnimation(spriteSheet, 1, 8, 1);
        this.animations[6] = loadAnimation(spriteSheet, 1, 8, 2);
        this.animations[7] = loadAnimation(spriteSheet, 1, 8, 3);
    }

    private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
        Animation animation = new Animation();
        for (int x = startX; x < endX; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 100);
        }
        return animation;
    }

    public void render(Graphics g) throws SlickException {
        for (Object listBullet1 : listBullet) {
            ((Bullet) listBullet1).render(g);
        }
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawAnimation(animations[direction + (moving ? 4 : 0)], x, y);

    }

    public void update(int delta) throws SlickException {
        if (HP > 0) {
            if (this.moving) {
                float futurX = getFuturX(delta);
                float futurY = getFuturY(delta);
                boolean collision = this.map.isCollision(futurX, futurY, this.width, this.height, this.direction);
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
            for (int i = 0; i < listBullet.size(); i++) {
                ((Bullet) listBullet.get(i)).update(delta);
                if (((Bullet) listBullet.get(i)).getCollison()) {
                    listBullet.remove(i);
                }
            }
        } else {
            //mort

        }
    }

    public float getFuturX(int delta) {
        float futurX = this.x;
        switch (this.direction) {
            case 1:
                futurX = this.x - speed * delta;
                break;
            case 3:
                futurX = this.x + speed * delta;
                break;
        }
        return futurX;
    }

    public float getFuturY(int delta) {
        float futurY = this.y;
        switch (this.direction) {
            case UP:
                futurY = this.y - speed * delta;
                break;
            case DOWN:
                futurY = this.y + speed * delta;
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

    void setHp() {
        HP--;
    }

    void setHp(int HP) {
        this.HP = HP;
    }

    int getHP() {
        return HP;
    }

    void addBonus(Bonus b) {
        System.out.println(b.name);
        b.setAvaliable(3);
        listBonus.add(b);
        setChanged();
        this.notifyObservers(b);
    }

    void removeBonus(int i) {
        listBonus.remove(i);
    }

    void shoot() throws SlickException {
        listBullet.add(new Bullet(map, (int) x, (int) y, direction));
    }

    void setEnnemyList(LinkedList ennemyList) {
        this.listEnnemy = ennemyList;
    }

    LinkedList getlistBullet() {
        return listBullet;
    }

    Bonus getBonus(int index) {
        if (index < listBonus.size()) {
            return (Bonus) listBonus.get(index);
        }
        return null;
    }

    LinkedList getListBonus() {
        return listBonus;
    }

    void lauchSpell(int type) throws SlickException{
        switch (type) {
            case 1:
                playerLauncheSpeedUp();
                break;
            case 2:
                playerLauncheMine();
                break;
            case 9:
                playerLauncheHeal();
                break;

        }
    }

    void playerLauncheLaser() {

    }

    void playerLauncheMine() {
        try {
            listBullet.add(new Mine(map, (int) x, (int) y, direction));
        } catch (SlickException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void playerLauncheSpeedUp() {
        if (speed == .1f) {
            speed = .2f;
            System.out.println(speed);
            TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("SpeedDown");
                    speed = .1f;
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 10 * 1000);
        }
    }

    void playerLauncheHeal() {
        this.HP++;
    }

}
