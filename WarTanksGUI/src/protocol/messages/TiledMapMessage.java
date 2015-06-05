/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol.messages;

import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author xajkep
 */
public class TiledMapMessage {
    private TiledMap tiledMap;
    
    public TiledMapMessage(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }
    
    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
