/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

/**
 *
 * @author Simon
 */
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.Display.getHeight;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class Game extends BasicGame {

    Sound shot;
    Music background;
    private GameContainer container;
    private Map map = new Map();

    private LinkedList listBonus = new LinkedList();
    private LinkedList listEnnemy = new LinkedList();

    private Hud hud = new Hud();
    private Player player = new Player(map, hud);
    private float xCamera = player.getX(), yCamera = player.getY();

    public Game() throws SlickException {
        super("Wartanks");
        shot = new Sound("src/ressources/sound/2.ogg");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        this.map.init();
        this.player.init();
        int type = 1;
        int randx = 0;
        int randy = 0;
        int randDirection;
        for (int i = 0; i < 10; i++) {
            type = (int) (Math.random() * (9 + 1 - 1)) + 1;
            randx = (int) (Math.random() * (32 * (this.map.getWidth() - 1)));
            randy = (int) (Math.random() * (32 * (this.map.getHeight() - 1)));
            listBonus.add(new Bonus(map, type, randx, randy));
        }

        for (int i = 0; i < 10; i++) {
            type = (int) (Math.random() * (10 + 1 - 1)) + 1;
            randx = (int) (Math.random() * (32 * (this.map.getWidth() - 1)));
            randy = (int) (Math.random() * (32 * (this.map.getHeight() - 1)));
            randDirection = (int) (Math.random() * (3 + 1 - 1)) + 1;
            listEnnemy.add(new Ennemy(map, randx, randy, randDirection, i));
        }
        for (Object listBonu : listBonus) {
            ((Bonus) listBonu).init();
        }

        for (Object listEnnemy1 : listEnnemy) {
            ((Ennemy) listEnnemy1).init();
        }
        player.setEnnemyList(listEnnemy);
        background = new Music("src/ressources/sound/1.ogg");
        background.setVolume(.05f);
        background.loop();
        this.hud.init();
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        g.translate(container.getWidth() / 2 - (int) xCamera, container.getHeight() / 2
                - (int) yCamera);
        this.map.renderBackground();
        this.player.render(g);
        for (Object listBonu : listBonus) {
            ((Bonus) listBonu).render(g);
        }
        for (int i = 0; i < listEnnemy.size(); i++) {
            ((Ennemy) listEnnemy.get(i)).render(g);
            if (((Ennemy) listEnnemy.get(i)).getHP() <= 0) {
                listEnnemy.remove(i);
            }
        }
        this.map.renderForeground();
        this.hud.render(g);
        if (player.getHP() == 0) {
            //mort
            Defeat defeat = new Defeat();
            defeat.init();
            defeat.render(g);
        }
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        this.player.update(delta);
        updateCamera(container);
        for (int i = 0; i < listEnnemy.size(); i++) {
            isColisionWithEnnemy((Ennemy) listEnnemy.get(i), delta);
            for (int y = 0; y < player.getlistBullet().size(); y++) {
                if (isCollisionBulletEnnemy((Ennemy) listEnnemy.get(i), (Bullet) player.getlistBullet().get(y), delta)) {
                    ((Ennemy) listEnnemy.get(i)).setHp();
                    System.out.println("boooom");
                    player.getlistBullet().remove(y);
                    break;
                }
            }
        }
        isCollisionWithBonus(delta);
    }

    public boolean isCollisionBulletEnnemy(Ennemy e, Bullet b, int delta) {
        return (e.getX() > b.getX() || e.getX() + 32 > b.getX())
                && (e.getX() < b.getX() + b.getWidth())
                && (e.getY() > b.getY() || e.getY() + 32 > b.getY())
                && (e.getY() < b.getY() + b.getHeight());
    }

    public void isColisionWithEnnemy(Ennemy e, int delta) {

        //direction droite
        if (isbetween(player.getFuturX(delta) + 26, e.getX(), e.getX() + 26) && player.getDirection() == 3
                && (isbetween(player.getFuturY(delta), e.getY(), e.getY() + 26))) {
            player.setMoving(false);
        }
        if (isbetween(player.getFuturX(delta) + 26, e.getX(), e.getX() + 26) && player.getDirection() == 3
                && (isbetween(player.getFuturY(delta) + 26, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
        }

        //direction haut
        if (isbetween(player.getFuturX(delta), e.getX(), e.getX() + 25) && player.getDirection() == 0
                && (isbetween(player.getFuturY(delta), e.getY(), e.getY() + 28))) {
            player.setMoving(false);
        }
        if (isbetween(player.getFuturX(delta) + 25, e.getX(), e.getX() + 25) && player.getDirection() == 0
                && (isbetween(player.getFuturY(delta), e.getY(), e.getY() + 28))) {
            player.setMoving(false);
        }

        //direction bas
        if (isbetween(player.getFuturX(delta), e.getX(), e.getX() + 25) && player.getDirection() == 2
                && (isbetween(player.getFuturY(delta) + 28, e.getY(), e.getY() + 28))) {
            player.setMoving(false);
        }
        if (isbetween(player.getFuturX(delta) + 25, e.getX(), e.getX() + 25) && player.getDirection() == 2
                && (isbetween(player.getFuturY(delta) + 28, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
        }
        //direction gauche
        if (isbetween(player.getFuturX(delta), e.getX(), e.getX() + 26) && player.getDirection() == 1
                && (isbetween(player.getFuturY(delta), e.getY(), e.getY() + 26))) {
            player.setMoving(false);
        }
        if (isbetween(player.getFuturX(delta), e.getX(), e.getX() + 26) && player.getDirection() == 1
                && (isbetween(player.getFuturY(delta) + 26, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
        }

    }

    public void isCollisionWithBonus(int delta) throws SlickException {
        int reload = 0;
        for (int i = 0; i < listBonus.size(); i++) {
            if ((this.player.getX() > ((Bonus) listBonus.get(i)).getX() || this.player.getX() + 32 > ((Bonus) listBonus.get(i)).getX())
                    && (this.player.getX() < ((Bonus) listBonus.get(i)).getX() + ((Bonus) listBonus.get(i)).getWidth())
                    && (this.player.getY() > ((Bonus) listBonus.get(i)).getY() || this.player.getY() + 32 > ((Bonus) listBonus.get(i)).getY())
                    && this.player.getY() < ((Bonus) listBonus.get(i)).getY() + ((Bonus) listBonus.get(i)).getHeight()) {
                // si on n'a pas 5 bonus on ajout le bonus
                if (player.getListBonus().size() < 5) {
                    reload = 0;
                    for (int y = 0; y < player.getListBonus().size(); y++) {
                        if (((Bonus) player.getListBonus().get(y)).getType() == ((Bonus) listBonus.get(i)).getType()) {
                            ((Bonus) player.getListBonus().get(y)).setAvaliable(((Bonus) player.getListBonus().get(y)).getAvaliable() + 3);
                            System.out.println("reload");
                            reload = 1;
                            break;
                        }
                    }
                    if (reload == 0) {
                        player.addBonus((Bonus) listBonus.get(i));
                        System.out.println("add");
                    }
                } else {
                    //si non on va chercher si le joueur possed le bonus
                    for (int y = 0; y < player.getListBonus().size(); y++) {
                        if (((Bonus) player.getListBonus().get(y)).getType() == ((Bonus) listBonus.get(i)).getType()) {
                            ((Bonus) player.getListBonus().get(y)).setAvaliable(((Bonus) player.getListBonus().get(y)).getAvaliable() + 3);
                            System.out.println("reload");
                            break;
                        } else if (((Bonus) player.getListBonus().get(y)).getAvaliable() == 0) {
                            player.getListBonus().set(y, ((Bonus) listBonus.get(i)));
                            System.out.println("change");
                            hud.getListBonus().set(y, new Image("src/ressources/UI/bonus/bonus" + ((Bonus) listBonus.get(i)).getType() + ".png"));
                            break;
                        } else {
                            System.out.println("nothing");
                        }
                    }
                }
                listBonus.remove(i);
            }

        }
    }

    private boolean isbetween(double val, double b1, double b2) {
        return val >= b1 && val < b2;
    }

    private boolean isInTrigger(int id) {
        return this.player.getX() > this.map.getObjectX(id)
                && this.player.getX() < this.map.getObjectX(id) + this.map.getObjectWidth(id)
                && this.player.getY() > this.map.getObjectY(id)
                && this.player.getY() < this.map.getObjectY(id) + this.map.getObjectHeight(id);
    }

    private void teleport(int objectID) {
        this.player.setX(Float.parseFloat(this.map.getObjectProperty(objectID, "dest-x",
                Float.toString(this.player.getX()))));
        this.player.setY(Float.parseFloat(this.map.getObjectProperty(objectID, "dest-y",
                Float.toString(this.player.getY()))));
    }

    private void changeMap(int objectID) throws SlickException {
        teleport(objectID);
        String newMap = this.map.getObjectProperty(objectID, "dest-map", "undefined");
        if (!"undefined".equals(newMap)) {
            this.map.changeMap("map/" + newMap);
        }
    }

    private void updateCamera(GameContainer container) {
        int w = container.getWidth() / 4;
        if (this.player.getX() > this.xCamera + w) {
            this.xCamera = this.player.getX() - w;
        } else if (this.player.getX() < this.xCamera - w) {
            this.xCamera = this.player.getX() + w;
        }
        int h = container.getHeight() / 4;
        if (this.player.getY() > this.yCamera + h) {
            this.yCamera = this.player.getY() - h;
        } else if (this.player.getY() < this.yCamera - h) {
            this.yCamera = this.player.getY() + h;
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        this.player.setMoving(false);
        if (Input.KEY_ESCAPE == key) {
            this.container.exit();
        }
    }

    Map getMap() {
        return map;
    }

    /**
     *
     * @param key
     * @param c
     * @throws SlickException
     */
    @Override
    public void keyPressed(int key, char c) {
        if (player.getHP() > 0) {
            switch (key) {
                case Input.KEY_UP:
                    this.player.setDirection(0);
                    this.player.setMoving(true);
                    break;
                case Input.KEY_LEFT:
                    this.player.setDirection(1);
                    this.player.setMoving(true);
                    break;
                case Input.KEY_DOWN:
                    this.player.setDirection(2);
                    this.player.setMoving(true);
                    break;
                case Input.KEY_RIGHT:
                    this.player.setDirection(3);
                    this.player.setMoving(true);
                    break;
                case Input.KEY_SPACE:
                    System.out.println("shoot");
                     {
                        try {
                            player.shoot();
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    shot.play();
                    break;
                case Input.KEY_Q:
                    System.out.println("");
                    if (player.getListBonus().size() > 0 && ((Bonus) player.getBonus(0)).getAvaliable() > 0) {
                        System.out.println("launch spell Q");
                        System.out.println(((Bonus) player.getBonus(0)).getAvaliable());
                        try {
                            player.lauchSpell(((Bonus) player.getBonus(0)).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ((Bonus) player.getBonus(0)).setAvaliable(((Bonus) player.getBonus(0)).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_W:
                    System.out.println("");
                    if (player.getListBonus().size() > 1 && ((Bonus) player.getBonus(1)).getAvaliable() > 1) {
                        System.out.println("launch spell W");
                        System.out.println(((Bonus) player.getBonus(1)).getAvaliable());
                        try {
                            player.lauchSpell(((Bonus) player.getBonus(1)).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ((Bonus) player.getBonus(1)).setAvaliable(((Bonus) player.getBonus(1)).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
            }
        }
    }
}
