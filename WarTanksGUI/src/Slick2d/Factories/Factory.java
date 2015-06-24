/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Slick2d.Factories;

import Slick2d.Fightable.Ennemy;
import Slick2d.Fightable.IFightable;
import Slick2d.Fightable.Player;
import Slick2d.HUD.Hud;
import Slick2d.GameObject.Map;
import Slick2d.GameObject.ScorePlayer;
import Slick2d.bullet.Bonus;

/**
 *
 * @author Simon
 */
public class Factory {
    private Map map = new Map();


    public void init(Map map)
    {
         this.map = map;
    }

    public Bonus bonusFactory() {
        int type = (int) (Math.random() * (9 + 1 - 1)) + 1;
        int randX = (int) (Math.random() * (32 * (this.map.getWidth() - 1)));
        int randY = (int) (Math.random() * (32 * (this.map.getHeight() - 1)));
        return new Bonus(map,type,randX,randY);
    }

    public Ennemy ennemyFactory(int ID) {
        int randX = (int) (Math.random() * (32 * (this.map.getWidth() - 1)));
        int randY = (int) (Math.random() * (32 * (this.map.getHeight() - 1)));
        int randDirection = (int) (Math.random() * (3 + 1 - 1)) + 1;
        return new Ennemy(map, randX, randY, randDirection, ID);
    }

    public Player playerFactory(int ID, Hud hud) {
        int randX = (int) (Math.random() * (32 * (this.map.getWidth() - 1)));
        int randY = (int) (Math.random() * (32 * (this.map.getHeight() - 1)));
        int randDirection = (int) (Math.random() * (3 + 1 - 1)) + 1;
        return new Player(map, randX, randY, randDirection, ID, hud);
    }

    public ScorePlayer scorePlayerFactory(IFightable player) {
        return new ScorePlayer(player);
    }
    
}