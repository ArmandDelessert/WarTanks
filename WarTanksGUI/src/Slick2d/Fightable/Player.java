/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.Fightable;

import Slick2d.GameObject.Explosion;
import Slick2d.bullet.AlphaStrick;
import Slick2d.bullet.Bonus;
import Slick2d.bullet.Bullet;
import Slick2d.bullet.Laser;
import Slick2d.GameObject.Map;
import Slick2d.bullet.Mine;
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
public class Player extends Observable implements IFightable{

    int playerID = 1; //sera attribuer par le serveur
    private final String name;
    private float x = 300, y = 300;
    private int HP;
    private int score;
    private Explosion e;
    private float speed;
    private int direction;
    private boolean moving = false;
    private boolean collision;
    private final LinkedList<Bonus> listBonus = new LinkedList();
    private final LinkedList<Bullet> listBullet = new LinkedList();
    private LinkedList<Ennemy> listEnnemy = new LinkedList();

    private final Animation[] animations = new Animation[8];

    private final Map map;

    private final int height = 32;
    private final int width = 32;

    private static final int UP = 0;
    private static final int LEFT = 1;
    private static final int DOWN = 2;
    private static final int RIGHT = 3;
    int olddir = 0;

    public Player(Map map, int x, int y, int randDirection, int id, Observer o) {
        this.map = map;
        this.name = "Player " + id;
        this.x = x;
        this.y = y;
        this.direction = 1;
        olddir = direction;
        score = 0;
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

    /**
     *
     * @param g
     * @throws SlickException
     */
    @Override
    public synchronized void render(Graphics g) throws SlickException {
        for (Object listBullet1 : listBullet) {
            ((Bullet) listBullet1).render(g);
        }
        
        g.setColor(new Color(0, 0, 0, .5f));
        g.drawAnimation(animations[direction + (moving ? 4 : 0)], x, y);
        
        if (HP == 0) {
            //recu les coord du joueur
            e.setX(x-16);
            e.setY(y-16);
            e.render(g);
        }
    }

    @Override
    public synchronized void update(int delta) throws SlickException {
        if (HP > 0) {
            if (this.moving) {
                float futurX = getFuturX(delta);
                float futurY = getFuturY(delta);
                    collision = this.map.isCollision(futurX, futurY, this.width, this.height, this.direction);

                if (collision) {
                    this.moving = false;
                } else {
                    this.x = futurX;
                    this.y = futurY;
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
            for (int i = 0; i < listBullet.size(); i++) {
                (listBullet.get(i)).update(delta);
                if ((listBullet.get(i)).getCollison()) {
                    listBullet.remove(i);
                }
            }
        } else {//mort
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
                futurX = this.x - speed * delta;
                break;
            case 3:
                futurX = this.x + speed * delta;
                break;
        }
        return futurX;
    }

    @Override
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

   public void setHp() {
        HP--;
    }

    public void setHp(int HP) {
        this.HP = HP;
    }

    public int getHP() {
        return HP;
    }

    public void addBonus(Bonus b) {
        System.out.println(b.name);
        b.setAvaliable(3);
        listBonus.add(b);
        setChanged();
        this.notifyObservers(b);
    }

    public void shoot() throws SlickException {
        listBullet.add(new Bullet(map, (int) x, (int) y, direction));
    }

    public void setEnnemyList(LinkedList ennemyList) {
        this.listEnnemy = ennemyList;
    }

    public LinkedList<Bullet> getlistBullet() {
        return listBullet;
    }

    public Bonus getBonus(int index) {
        if (index < listBonus.size()) {
            return listBonus.get(index);
        }
        return null;
    }

    public LinkedList<Bonus> getListBonus() {
        return listBonus;
    }

    public void launcheSpell(int type) throws SlickException {
        switch (type) {
            case 1:
                playerLauncheSpeedUp();
                break;
            case 2:
                playerLauncheMine();
                break;
            case 6:
                playerLauncheMultiShoot();
                break;
            case 4:
                playerLauncheLaser();
                break;
            case 8:
                playerLauncheALPHASTRIK();
            case 9:
                playerLauncheHeal();
            case 10:
                playerLauncheRandomAttack();
                break;

        }
    }

    void playerLauncheLaser() {
        try {
            listBullet.add(new Laser(map, (int) x, (int) y, direction,this));
        } catch (SlickException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    void playerLauncheMultiShoot() throws SlickException {
        listBullet.add(new Bullet(map, (int) x, (int) y, 0));
        listBullet.add(new Bullet(map, (int) x, (int) y, 1));
        listBullet.add(new Bullet(map, (int) x, (int) y, 2));
        listBullet.add(new Bullet(map, (int) x, (int) y, 3));
    }

    void playerLauncheALPHASTRIK() {
        try {
            if (direction == UP) {
                listBullet.add(new AlphaStrick(map, (int) x, (int) y - 160, direction));
            }
            if (direction == DOWN) {
                listBullet.add(new AlphaStrick(map, (int) x, (int) y + 160, direction));
            }
            if(direction == RIGHT){
                listBullet.add(new AlphaStrick(map, (int) x + 160, (int) y-16, direction));
            }
            if(direction == LEFT)
            {
                listBullet.add(new AlphaStrick(map, (int) x - 160, (int) y-16, direction));
            }
        } catch (SlickException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void playerLauncheRandomAttack()
    {
        int randomNum = 0 + (int)(Math.random()*listEnnemy.size()+1);
        System.out.println(randomNum);
        if(randomNum == 10)
            this.HP--;
        else
        {
         this.listEnnemy.get(randomNum).setHp();
        }

    }

    LinkedList<Ennemy> listEnnemy() {
        return listEnnemy;
    }
    void setScore(int s)
    {
        score = s;
    }
    int getScore()
    {
        return score;
    }
    void AddScore()
    {
        score++;
    }
    public boolean getCol()
    {
        return collision;
    }
    public void setCollision(boolean b)
    {
        collision = b;
    }

}
