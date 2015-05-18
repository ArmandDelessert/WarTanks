/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d;

import java.util.LinkedList;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class laser extends Bullet{

    public laser(Map map, int x, int y, int direction, LinkedList listEnnemy) throws SlickException {
        super(map, x, y, direction);
    }
    
}
