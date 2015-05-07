/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package map;

import java.io.File;
import vehicle.Tank;
import vehicle.Vehicle;


/**
 *
 * @author Armand
 */
public class Map {
	String name;
	private int sizeX;
	private int sizeY;
	private Cell map[][] = null;
	public Vehicle vehicles;
	File mapFile;

	/*** Constructeurs ***/

	/**
	 * Création d'une carte vide.
	 * 
	 * @param name
	 * @param sizeX
	 * @param sizeY 
	 */
	public Map(String name, int sizeX, int sizeY) {
		this.name = name;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.map = new Cell[sizeX][sizeY];

		// Initialisation de la carte avec des cases vides
		for (int i = 0; i < this.sizeY; i++) {
			for (int j = 0; j < this.sizeX; j++) {
				map[j][i] = GlobalCell.freeCell;
			}
		}
	}

	/**
	 * Chargement d'une carte à partir d'un fichier.
	 * 
	 * @param map 
	 */
	public Map(File map) {
		// Chargement des propriétés de la carte
		name = name;
		sizeX = sizeX;
		sizeY = sizeY;

		// Chargement de la map
	}

	/*** Getters ***/

	public String getName() {return this.name;}
	public int getSizeX() {return this.sizeX;}
	public int getSizeY() {return this.sizeY;}

	/*** Setters ***/

	public void setName(String name) {this.name = name;}

	/*** Methodes ***/

	/**
	 * Afficher la carte.
	 * 
	 * @return 
	 */
	@Override
	public String toString() {
		String str = new String();

		// Affichage du bord supérieur
		for (int i = 0; i < this.sizeX +2; i++) {
			str += '*';
		}
		str += '\n';

		// Affichage de la carte
		for (int i = this.sizeY -1; i >= 0; i--) {
			str += '*';
			for (int j = 0; j < this.sizeX; j++) {
				// Affichage de la cellule
				if (i == vehicles.getPosY() && j == vehicles.getPosX()) {
					str += vehicles;
				}
				else {
					str += this.map[j][i];
				}
			}
			str += "*\n";
		}

		// Affichage du bord inférieur
		for (int i = 0; i < this.sizeX +2; i++) {
			str += '*';
		}
		str += '\n';

		return str;
	}

	/**
	 * Ajouter un véhicule sur la carte.
	 * 
	 * @param vehicle 
	 */
	public void addVehicle(Vehicle vehicle) {
		vehicles = new Tank(vehicle.getName(), this, vehicle.getPosX(), vehicle.getPosY(), vehicle.getDirection());
	}
}
