/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.PEnd;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Simon
 */
public class Victory extends End{
    
    public void init() throws SlickException {
       this.DefeatVictory = new Image("src/ressources/UI/victory.png");
       this.play = new Image("src/ressources/UI/play.png");
    }
}
