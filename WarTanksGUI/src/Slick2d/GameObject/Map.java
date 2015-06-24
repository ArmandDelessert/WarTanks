
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.GameObject;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Simon
 */
public class Map {

    MouseOverArea GUI;
    GameContainer container;
    private TiledMap tiledMap;
    private int forground;
    private int logical;
    private Color color2;
    private Color color;
    int oldDir = 0;

    public void init() throws SlickException {
        this.tiledMap = new TiledMap("src/ressources/map/maps/map1.tmx");
        logical = tiledMap.getLayerIndex("logic");
        forground = tiledMap.getLayerIndex("for");

    }

    public void renderBackground() {
        this.tiledMap.render(0, 0);
        for(int i = 0 ; i < tiledMap.getLayerCount() ; i++)
        {
            if(i != logical && i != forground) {
                this.tiledMap.render(0, 0, i);
            }
        }
    }

    public void renderForeground() {
            //this.tiledMap.render(0, 0, forground);
    }

    public boolean isCollision(float x, float y, int width, int height, int direction) {
        double speed = .1f;
        int tileW = this.tiledMap.getTileWidth();
        int tileH = this.tiledMap.getTileHeight();
        Image tile = null;
        Image tile2 = null;
        boolean collision;

        if((y + height > this.tiledMap.getHeight() * tileH && direction == 2)|| (y < 0 && direction == 0)
                || (x + width > this.tiledMap.getWidth() * tileW && direction == 3) || (x < 0 && direction == 1))
        {
            return true;
        }
        if(direction == 0) {
            tile = this.tiledMap.getTileImage((int) (x +2) / tileW, (int) y / tileH, logical);
            tile2 = this.tiledMap.getTileImage((int) (x +width-2) / tileW, (int)  y / tileH, logical);
        }
        if(direction == 1) {
            tile = this.tiledMap.getTileImage((int) x / tileW, (int) (y+2) / tileH, logical);
            tile2 = this.tiledMap.getTileImage((int) x / tileW, (int)  (y+height-2) / tileH, logical);
        }
        if(direction == 2) {
            tile = this.tiledMap.getTileImage((int) (x+2) / tileW, (int) (y +height)/ tileH, logical);
            tile2 = this.tiledMap.getTileImage((int)  (x +width-2) / tileW, (int)  (y +height) / tileH, logical);
        }
        if(direction == 3) {
            tile = this.tiledMap.getTileImage((int) (x +width) / tileW, (int) (y+2) / tileH, logical);
            tile2 = this.tiledMap.getTileImage((int)  (x +width) / tileW, (int)  (y +height-2) / tileH, logical);
        }
        collision = (tile != null || tile2 != null);

        if (collision && direction == oldDir) {
            if(tile != null)
            {
                color = tile.getColor((int) x % tileW, (int) y % tileH);
                collision = color.getAlpha() > 0;
            }

            if(tile2 != null) {
                color2 = tile2.getColor((int) x % tileW, (int) y % tileH);
                collision = color2.getAlpha() > 0;
            }
            System.out.println("bim");
            return collision;
        }
        oldDir = direction;


        // Map border collisions management
        collision = collision || (y + height > this.tiledMap.getHeight() * tileH && direction == 2)|| (y < 0 && direction == 0)
                || (x + width > this.tiledMap.getWidth() * tileW && direction == 3) || (x < 0 && direction == 1);

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
    public TiledMap getTiledMap()
    {
        return tiledMap;
    }

}
