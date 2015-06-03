/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.Fightable;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public interface IFightable {
   public void render(Graphics g)throws SlickException;
   public void update(int delta) throws SlickException;
   public float getFuturX(int delta);
   public float getFuturY(int delta);
   public float getX();
   public float getY();
   public void setX(float x);
   public void setY(float x);
   public void setDirection(int direction);
   public boolean isMoving();
   public void setMoving(boolean moving);
}
