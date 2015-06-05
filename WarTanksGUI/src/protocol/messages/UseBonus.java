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
public class UseBonus extends Command {
    private int type;
    
    public UseBonus(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
}
