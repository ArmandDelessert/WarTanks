/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol.messages;

/**
 *
 * @author xajkep
 */
public class Movement extends Command {
    private int direction;
    
    public Movement(int direction) {
        this.direction = direction;
    }
    
    public int getDirection() {
        return direction;
    }
}
