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
import Slick2d.Factories.Factory;
import Slick2d.GameObject.Map;
import Slick2d.GameObject.ScorePlayer;
import Slick2d.bullet.AlphaStrick;
import Slick2d.bullet.Bonus;
import Slick2d.bullet.Bullet;
import Slick2d.Fightable.Player;
import Slick2d.Fightable.Ennemy;
import Slick2d.PEnd.Victory;
import Slick2d.PEnd.Defeat;
import Slick2d.HUD.Hud;
import Slick2d.bullet.Laser;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class Game extends BasicGame {

    private int nbrPlayer;
    private int nbrBonus;
    private int gameTimeSec;
    private Sound shot;
    private Music background;
    private GameContainer container;
    private final Map map = new Map();
    private int dead = 0;
    private Factory factory;
    private final LinkedList<Bonus> listBonus = new LinkedList();
    private final LinkedList<Ennemy> ennemies = new LinkedList();
    private final LinkedList<ScorePlayer> scores = new LinkedList();
    private boolean[][] blocked;

    private Hud hud;
    private Player player;
    private float xCamera;
    private float yCamera;

    public Game(int nbrPlayer, int nbrBonus, int gameTimeSec) throws SlickException {
        super("Wartanks");
        shot = new Sound("src/ressources/sound/2.ogg");
        factory = new Factory();
        this.nbrPlayer = nbrPlayer;
        this.nbrBonus = nbrBonus;
        this.gameTimeSec = gameTimeSec;
    }

    public void setNbPlayer(int n) {
        this.nbrPlayer = n;
    }

    public int getNbrPlayer() {
        return nbrPlayer;
    }

    public int getNbrBonus() {
        return nbrBonus;
    }

    public int getGameTimeSec() {
        return gameTimeSec;
    }

    public void setNbBonus(int n) {
        this.nbrBonus = n;
    }

    public void setGameTimeSec(int n) {
        this.gameTimeSec = n;
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        this.map.init();
        factory.init(map);
        hud = new Hud(container);
        player = factory.playerFactory(0, hud);
        xCamera = player.getX();
        yCamera = player.getY();
        this.map.init();
        this.player.init();
        dead = 0;

        for (int i = 0; i < nbrBonus; i++) {
            listBonus.add(factory.bonusFactory());
        }

        for (int i = 0; i < nbrPlayer; i++) {
            Ennemy e = factory.ennemyFactory(i);
            ennemies.add(e);
            scores.add(new ScorePlayer(e));
        }
        for (Object bonus : listBonus) {
            ((Bonus) bonus).init();
        }

        for (Object ennemie : ennemies) {
            ((Ennemy) ennemie).init();
        }
        player.setEnnemyList(ennemies);
        background = new Music("src/ressources/sound/1.ogg");
        background.setVolume(.05f);
        background.loop();
        this.hud.init();
        hud.setLisEnnemyList(ennemies);


        // build a collision map based on tile properties in the TileD map
        blocked = new boolean[map.getWidth()][map.getHeight()];

        for (int xAxis=0;xAxis<map.getWidth(); xAxis++)
        {
            for (int yAxis=0;yAxis<map.getHeight(); yAxis++)
            {
                int tileID = map.getTiledMap().getTileId(xAxis, yAxis, 0);
                String value = map.getTiledMap().getTileProperty(tileID, "logic", "false");
                if ("true".equals(value))
                {
                    blocked[xAxis][yAxis] = true;
                }
            }
        }
    }
    private boolean isBlocked(float x, float y)
    {
        int xBlock = (int)x / 32;
        int yBlock = (int)y / 32;
        return blocked[xBlock][yBlock];
    }

    @Override
    public synchronized void render(GameContainer container, Graphics g) throws SlickException {
        g.translate(container.getWidth() / 2 - (int) xCamera, container.getHeight() / 2
                - (int) yCamera);
        this.map.renderBackground();

        for (Object bonus : listBonus) {
            ((Bonus) bonus).render(g);
        }
        for (Object ennemie : ennemies) {
            ((Ennemy) ennemie).render(g);
        }
        this.player.render(g);
        this.map.renderForeground();
        this.hud.render(g);

        dead = 0;
        for (Object ennemie : ennemies) {
            if (((Ennemy) ennemie).getHP() > 0) {
                break;
            } else {
                dead++;
            }
        }

        if (player.getHP() == 0) {
            //mort
            Defeat defeat = new Defeat();
            defeat.init();
            defeat.render(g);
        }
        if (dead == ennemies.size()) {
            Victory victory = new Victory();
            victory.init();
            victory.render(g);
        }

    }

    void restGame(GameContainer container) throws SlickException {
        player.getListBonus().clear();
        this.listBonus.clear();
        ennemies.clear();
        this.init(container);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        this.player.update(delta);
        updateCamera(container);
        for (int i = 0; i < ennemies.size(); i++) {
            (ennemies.get(i)).update(delta);
            isColisionWithEnnemy( ennemies.get(i), delta);
            for (int y = 0; y < player.getlistBullet().size(); y++) {
                if (isCollisionBulletEnnemy( ennemies.get(i), player.getlistBullet().get(y), delta)) {
                    if ((player.getlistBullet().get(y) instanceof AlphaStrick) && !((AlphaStrick) player.getlistBullet().get(y)).getStrick()) {
                        ( ennemies.get(i)).setHp();
                    }
                    else if ((player.getlistBullet().get(y) instanceof Laser) && !((Laser) player.getlistBullet().get(y)).getStrick()) {
                        ( ennemies.get(i)).setHp();
                    }else if (!(player.getlistBullet().get(y) instanceof AlphaStrick) && !(player.getlistBullet().get(y) instanceof Laser)) {
                        ( ennemies.get(i)).setHp();
                    }
                    hud.setLisEnnemyList(ennemies);
                    System.out.println("boooom");

                    if ((player.getlistBullet().get(y) instanceof AlphaStrick) && !(player.getlistBullet().get(y) instanceof Laser)) {
                        ((AlphaStrick) player.getlistBullet().get(y)).setStrick(true);
                        if (((AlphaStrick) player.getlistBullet().get(y)).getExplosion().isFinished()) {
                            player.getlistBullet().remove(y);
                        }
                    } else if (player.getlistBullet().get(y) instanceof Laser) {
                        ((Laser) player.getlistBullet().get(y)).setStrick(true);
                        if (((Laser) player.getlistBullet().get(y)).getFinished()) {
                            System.out.println("laser");
                            player.getlistBullet().remove(y);
                        }
                    } else {
                        player.getlistBullet().remove(y);
                    }
                    break;
                }
            }
        }
        for (int y = 0; y < player.getlistBullet().size(); y++) {
            if ((player.getlistBullet().get(y) instanceof AlphaStrick)) {
                if (((AlphaStrick) player.getlistBullet().get(y)).getExplosion().isFinished()) {
                    player.getlistBullet().remove(y);
                }
            }
        }
        isCollisionWithBonus(delta);
        Input input = container.getInput();
        if (input.isKeyDown(Input.KEY_UP)) {
            this.player.setDirection(0);
            if(player.getCol()) {
                this.player.setMoving(false);
            }
            else
            {
                this.player.setMoving(true);
            }
        }
         if (input.isKeyDown(Input.KEY_RIGHT)) {
         this.player.setDirection(3);
             if(player.getCol()) {
                 this.player.setMoving(false);
             }
             else
             {
                 this.player.setMoving(true);
             }
         }
         if (input.isKeyDown(Input.KEY_LEFT)) {
         this.player.setDirection(1);
             if(player.getCol()) {
                 this.player.setMoving(false);
             }
             else
             {
                 this.player.setMoving(true);
             }
         }
         if (input.isKeyDown(Input.KEY_DOWN)) {
         this.player.setDirection(2);
             if(player.getCol()) {
                 this.player.setMoving(false);
             }
             else
             {
                 this.player.setMoving(true);
             }
         }
    }

    boolean isCollisionBulletEnnemy(Ennemy e, Bullet b, int delta) {
        return (e.getX() > b.getX() || e.getX() + 28 > b.getX())
                && (e.getX() < b.getX() + b.getWidth())
                && (e.getY() > b.getY() || e.getY() + 28 > b.getY())
                && (e.getY() < b.getY() + b.getHeight());
    }

    void isColisionWithEnnemy(Ennemy e, int delta) {

        //direction droite
        if (isBetween(player.getFuturX(delta) + 26, e.getX(), e.getX() + 26) && player.getDirection() == 3
                && (isBetween(player.getFuturY(delta), e.getY(), e.getY() + 26))) {
            player.setMoving(false);
            player.setCollision(true);
        }
        if (isBetween(player.getFuturX(delta) + 26, e.getX(), e.getX() + 26) && player.getDirection() == 3
                && (isBetween(player.getFuturY(delta) + 26, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
            player.setCollision(true);
        }

        //direction haut
        if (isBetween(player.getFuturX(delta), e.getX(), e.getX() + 25) && player.getDirection() == 0
                && (isBetween(player.getFuturY(delta), e.getY(), e.getY() + 28))) {
            player.setMoving(false);
            player.setCollision(true);
        }
        if (isBetween(player.getFuturX(delta) + 25, e.getX(), e.getX() + 25) && player.getDirection() == 0
                && (isBetween(player.getFuturY(delta), e.getY(), e.getY() + 28))) {
            player.setMoving(false);
            player.setCollision(true);
        }

        //direction bas
        if (isBetween(player.getFuturX(delta), e.getX(), e.getX() + 25) && player.getDirection() == 2
                && (isBetween(player.getFuturY(delta) + 28, e.getY(), e.getY() + 28))) {
            player.setMoving(false);
            player.setCollision(true);
        }
        if (isBetween(player.getFuturX(delta) + 25, e.getX(), e.getX() + 25) && player.getDirection() == 2
                && (isBetween(player.getFuturY(delta) + 28, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
            player.setCollision(true);
        }
        //direction gauche
        if (isBetween(player.getFuturX(delta), e.getX(), e.getX() + 26) && player.getDirection() == 1
                && (isBetween(player.getFuturY(delta), e.getY(), e.getY() + 26))) {
            player.setMoving(false);
            player.setCollision(true);
        }
        if (isBetween(player.getFuturX(delta), e.getX(), e.getX() + 26) && player.getDirection() == 1
                && (isBetween(player.getFuturY(delta) + 26, e.getY(), e.getY() + 26))) {
            player.setMoving(false);
            player.setCollision(true);
        }

    }

    void isCollisionWithBonus(int delta) throws SlickException {
        int reload = 0;
        for (int i = 0; i < listBonus.size(); i++) {
            if ((this.player.getX() > ( listBonus.get(i)).getX() || this.player.getX() + 32 > listBonus.get(i).getX())
                    && (this.player.getX() < ( listBonus.get(i)).getX() + listBonus.get(i).getWidth())
                    && (this.player.getY() > listBonus.get(i).getY() || this.player.getY() + 32 > listBonus.get(i).getY())
                    && this.player.getY() < listBonus.get(i).getY() + listBonus.get(i).getHeight()) {
                // si on n'a pas 5 bonus on ajout le bonus
                if (player.getListBonus().size() < 5) {
                    reload = 0;
                    for (int y = 0; y < player.getListBonus().size(); y++) {
                        if (player.getListBonus().get(y).getType() == listBonus.get(i).getType()) {
                            player.getListBonus().get(y).setAvaliable(player.getListBonus().get(y).getAvaliable() + 3);
                            System.out.println("reload");
                            reload = 1;
                            break;
                        }
                    }
                    if (reload == 0) {
                        player.addBonus(listBonus.get(i));
                        System.out.println("add");
                    }
                } else {
                    //si non on va chercher si le joueur possed le bonus
                    for (int y = 0; y < player.getListBonus().size(); y++) {
                        if (player.getListBonus().get(y).getType() == listBonus.get(i).getType()) {
                            player.getListBonus().get(y).setAvaliable(player.getListBonus().get(y).getAvaliable() + 3);
                            System.out.println("reload");
                            break;
                        } else if (player.getListBonus().get(y).getAvaliable() == 0) {
                            player.getListBonus().set(y, listBonus.get(i));
                            System.out.println("change");
                            player.getListBonus().get(y).setAvaliable(player.getListBonus().get(y).getAvaliable() + 3);
                            hud.getListBonus().set(y, new Image("src/ressources/UI/bonus/bonus" + listBonus.get(i).getType() + ".png"));
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

    private boolean isBetween(double val, double b1, double b2) {
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
                case Input.KEY_SPACE: {
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
                    if (player.getListBonus().size() > 0 && player.getBonus(0).getAvaliable() > 0) {
                        System.out.println("launch spell Q");
                        System.out.println(player.getBonus(0).getAvaliable());
                        try {
                            player.launcheSpell(player.getBonus(0).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.getBonus(0).setAvaliable(player.getBonus(0).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_W:
                    System.out.println("");
                    if (player.getListBonus().size() > 1 && player.getBonus(1).getAvaliable() > 0) {
                        System.out.println("launch spell W");
                        System.out.println(player.getBonus(1).getAvaliable());
                        try {
                            player.launcheSpell(player.getBonus(1).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.getBonus(1).setAvaliable(player.getBonus(1).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_E:
                    System.out.println("");
                    if (player.getListBonus().size() > 2 && player.getBonus(2).getAvaliable() > 0) {
                        System.out.println("launch spell E");
                        System.out.println(player.getBonus(2).getAvaliable());
                        try {
                            player.launcheSpell(player.getBonus(2).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.getBonus(2).setAvaliable(player.getBonus(2).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_R:
                    System.out.println("");
                    if (player.getListBonus().size() > 3 && player.getBonus(3).getAvaliable() > 0) {
                        System.out.println("launch spell R");
                        System.out.println(player.getBonus(3).getAvaliable());
                        try {
                            player.launcheSpell(player.getBonus(3).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.getBonus(3).setAvaliable(player.getBonus(3).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_T:
                    System.out.println("");
                    if (player.getListBonus().size() > 4 && player.getBonus(4).getAvaliable() > 0) {
                        System.out.println("launch spell T");
                        System.out.println(player.getBonus(4).getAvaliable());
                        try {
                            player.launcheSpell(player.getBonus(4).getType());
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.getBonus(4).setAvaliable(player.getBonus(4).getAvaliable() - 1);
                    } else {
                        System.out.println("empty");
                    }
                    break;
                case Input.KEY_H:
                    player.setHp();
                    break;
            }
        }
    }
}
