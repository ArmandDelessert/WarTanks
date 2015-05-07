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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class Game extends BasicGame {

    Sound shot;
    Music background;
    private Bullet b;
    private GameContainer container;
    private Map map = new Map();
    private Player player = new Player(map);
    private float xCamera = player.getX(), yCamera = player.getY();
    private Hud hud = new Hud();

    public Game() throws SlickException {
        super("Wartanks");
        shot = new Sound("src/ressources/sound/2.ogg");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        this.map.init();
        this.player.init();
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
                case Input.KEY_SPACE:
                    System.out.println("shoot");
                    shot.play();
                    break;
                case Input.KEY_H:
                    System.out.println("HP");
                    player.setHp();
                    break;
                case Input.KEY_B:
                    System.out.println("bonus");
                     {
                        try {
                            hud.addBonus("src/ressources/UI/bonus/bonus1.png");
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case Input.KEY_Q:
                    System.out.println("mine");
                    Bulletv2 b = new Bulletv2(map);
                     {
                        try {
                            b.init();
                        } catch (SlickException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
            }
        }
    }
}
