/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.GameObject;

import it.marteEngine.ResourceManager;
import it.marteEngine.entity.Entity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class destroyable extends Entity{
    
    public static String DESTORYABLE = "destoyable";
    private int framCont = 0;
    private int nextFrame = 500;
    private int health;
    public destroyable(float x, float y) {
        super(x, y);
        addType(DESTORYABLE);
        setHitBox(8, 1, 32, 32);
        Image destroyable = ResourceManager.getImage("destroyable");
        setGraphic(destroyable);
    }
    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        super.update(gc, delta);
        
    }
}
