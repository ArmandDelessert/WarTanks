/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

/**
 *
 * @author Simon
 */
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.Animation;

public class Tank {
    private float x = 300, y = 300;
    private int direction = 0;
    private boolean moving = false;
    private Animation[] animations = new Animation[8];
    public Tank()
    {
        
    }
    public Animation getAnimation(int i)
    {
        return animations[i];
    }
    public void setAnimation(Animation animation, int i)
    {
        animations[i] = animation;
    }
}
