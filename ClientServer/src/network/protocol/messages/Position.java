/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.protocol.messages;

/**
 *
 * @author xajkep
 */
public class Position extends Command {

	private final int x, y;

	public Position(int x, int y) {
		this.x = x;
                this.y = y;
	}

	public int getX() {
		return x;
	}
        
        public int getY() {
		return y;
	}
}
