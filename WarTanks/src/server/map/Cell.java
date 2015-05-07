/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package map;


/**
 *
 * @author Armand
 */
public abstract class Cell {

	/*** Getters ***/

	public Class cellType() {
		return this.getClass();
	}

	public Cell destroyCell() {
		return null;
	}

	/*** Méthodes ***/

	public String toString() {
		return null;
	}
}

/*** Cases libres ***/

// Case libre
class FreeCell extends Cell {
	public String toString() {
		return " ";
	}
}

// Mur cassé
class BrokenWall extends Cell {
	public String toString() {
		return " ";
	}
}

// Tache noire (après une explosion)
class BlackSpot extends Cell {
	public String toString() {
		return " ";
	}
}

/*** Obstacles cassables ***/

// Mur cassable
class BreakableWall extends Cell {
	public String toString() {
		return "+";
	}

	public Cell destroyCell() {
		return GlobalCell.brokenWall;
	}
}

// Epave
class Wreck extends Cell {
	public String toString() {
		return "w";
	}

	public Cell destroyCell() {
		return GlobalCell.blackSpot;
	}
}

/*** Obstacles incassables ***/

// Mur incassable
class UnbreakableWall extends Cell {
	public String toString() {
		return "x";
	}
}
