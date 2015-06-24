/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.GameObject;

import Slick2d.Fightable.IFightable;

/**
 *
 * @author Simon
 */
public class ScorePlayer {
    int kill;
    int death;
    IFightable player;
    
    public ScorePlayer(IFightable player)
    {
        int kill = 0;
        int death = 0;
        this.player = player;
    }
    
}
