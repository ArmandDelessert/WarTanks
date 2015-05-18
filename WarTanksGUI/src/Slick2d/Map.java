/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Simon
 */
public class Map {

    MouseOverArea GUI;
    GameContainer container;
    TiledMap tiledMap;

    public void init() throws SlickException {
        this.tiledMap = new TiledMap("src/ressources/map/maps/test2.tmx");

    }

    public void renderBackground() {
        this.tiledMap.render(0, 0);
        this.tiledMap.render(0, 0, 0);
        this.tiledMap.render(0, 0, 1);
        this.tiledMap.render(0, 0, 2);
        this.tiledMap.render(0, 0, 3);
        this.tiledMap.render(0, 0, 4);
    }

    public void renderForeground() {
        //this.tiledMap.render(0, 0, 2);
    }

    public boolean isCollision(float x, float y, int width, int height, int direction) {
        int tileW = this.tiledMap.getTileWidth();
        int tileH = this.tiledMap.getTileHeight();
        int logicLayer = this.tiledMap.getLayerIndex("logic");
        Image tile = null;
        boolean collision;

//        // Coin haut - gauche
//        if (this.tiledMap.getTileImage((int) (x) / tileW, (int) (y) / tileH, logicLayer) != null) {
//            tile = this.tiledMap.getTileImage((int) (x) / tileW, (int) (y) / tileH, logicLayer);
//
//            // Coin haut - droite
//        } else if (this.tiledMap.getTileImage((int) (x + width) / tileW, (int) (y) / tileH, logicLayer) != null) {
//            tile = this.tiledMap.getTileImage((int) (x + width) / tileW, (int) (y) / tileH, logicLayer);
//
//            // Coin bas - gauche
//        } else if (this.tiledMap.getTileImage((int) x / tileW, (int) (y + height) / tileH, logicLayer) != null) {
//            tile = this.tiledMap.getTileImage((int) x / tileW, (int) (y + height) / tileH, logicLayer);
//            // Coin bas - droite
//        } else if (this.tiledMap.getTileImage((int) (x + width) / tileW, (int) (y + height) / tileH, logicLayer) != null) {
//            tile = this.tiledMap.getTileImage((int) (x + width) / tileW, (int) (y + height) / tileH, logicLayer);
//        }

        collision = tile != null;

        if (collision) {
            Color color = tile.getColor((int) x % tileW, (int) y % tileH);
            collision = color.getAlpha() > 0;
        }

        // Map border collisions management
        collision = collision || (y + height - 2 > this.tiledMap.getHeight() * tileH && direction == 2)|| (y - 2 < 0 && direction == 0)
                || (x + width - 2 > this.tiledMap.getWidth() * tileW && direction == 3) || (x - 2 < 0 && direction == 1);

        return collision;
    }

    public boolean isSlowed(float x, float y) {
        int tileW = this.tiledMap.getTileWidth();
        int tileH = this.tiledMap.getTileHeight();
        int logicLayer = this.tiledMap.getLayerIndex("slow");
        Image tile = this.tiledMap.getTileImage((int) x / tileW, (int) y / tileH, logicLayer);
        boolean collision = tile != null;
        if (collision) {
            System.out.println("slowed");
            Color color = tile.getColor((int) x % tileW, (int) y % tileH);
            collision = color.getAlpha() > 0;
        }
        return collision;
    }

    public void changeMap(String file) throws SlickException {
        this.tiledMap = new TiledMap(file);
    }

    public int getObjectCount() {
        return this.tiledMap.getObjectCount(0);
    }

    public String getObjectType(int objectID) {
        return this.tiledMap.getObjectType(0, objectID);
    }

    public float getObjectX(int objectID) {
        return this.tiledMap.getObjectX(0, objectID);
    }

    public float getObjectY(int objectID) {
        return this.tiledMap.getObjectY(0, objectID);
    }

    public float getObjectWidth(int objectID) {
        return this.tiledMap.getObjectWidth(0, objectID);
    }

    public float getObjectHeight(int objectID) {
        return this.tiledMap.getObjectHeight(0, objectID);
    }

    public String getObjectProperty(int objectID, String propertyName, String def) {
        return this.tiledMap.getObjectProperty(0, objectID, propertyName, def);
    }

    public int getHeight() {
        return this.tiledMap.getHeight();
    }
    public int getWidth() {
        return this.tiledMap.getWidth();
    }

}
