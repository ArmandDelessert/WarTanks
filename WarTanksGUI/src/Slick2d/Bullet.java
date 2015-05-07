/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

// Bullet Class
import it.marteEngine.ME;
import it.marteEngine.ResourceManager;
import it.marteEngine.entity.Entity;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
 
 
public class Bullet extends Entity
{
    public static String BULLET = "bullet";
    private final String[] destroyables = { };
    public Bullet(float x, float y) {
        super(x, y);
        Image bullet = ResourceManager.getImage("bullet");
        setGraphic(bullet);
        setHitBox(2, 1, 4, 6);
        addType(BULLET);
    }
    @Override
    public void update(GameContainer gc, int delta) throws SlickException{
        y -=(.7 * delta);
        if(collide(destroyables, x, y) != null)
            this.destroy();
        if(y <= 0)
        {
            ME.world.remove(this);
        }
        super.update(gc, delta);
    }
}