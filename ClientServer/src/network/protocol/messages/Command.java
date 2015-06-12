/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 31.05.2015
 * 
 * Description :
 * Classe pour la communication des commandes du client au serveur.
 */

package network.protocol.messages;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class Command implements Serializable {
/*
	public CommandType typeCmd;
	public Command_Movement cmdMovement;
	public Command_Fire cmdFire;

	public Command() {

		this.typeCmd = null;
		this.cmdMovement = null;
		this.cmdFire = null;
	}

	public Command(CommandType typeCmd, Command_Movement cmd) {

		this.typeCmd = typeCmd;
		this.cmdMovement = cmd;
		this.cmdFire = null;
	}

	public Command(CommandType typeCmd, Command_Fire cmd) {

		this.typeCmd = typeCmd;
		this.cmdMovement = null;
		this.cmdFire = cmd;
	}

	public void newCommand(CommandType typeCmd, Command_Movement cmd) {

		this.typeCmd = typeCmd;
		this.cmdMovement = cmd;
		this.cmdFire = null;
	}

	public void newCommand(CommandType typeCmd, Command_Fire cmd) {

		this.typeCmd = typeCmd;
		this.cmdMovement = null;
		this.cmdFire = cmd;
	}

	public enum CommandType {

		MOVEMENT,
		FIRE;
	}

	public enum Command_Movement {

		UP,
		DOWN,
		LEFT,
		RIGHT;
	}

	public enum Command_Fire {

		FIRE;
	}
*/
}
