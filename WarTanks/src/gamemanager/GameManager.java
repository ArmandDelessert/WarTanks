/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 03.05.2015
 * 
 * Description :
 * Gestion du plateau de jeu.
 */

package gamemanager;

import java.util.logging.Level;
import java.util.logging.Logger;
import map.Direction;
import map.Map;
import vehicle.Tank;

/**
 *
 * @author Armand Delessert
 */
public class GameManager implements Runnable {

	@Override
	public void run() {
		try {
			gameManager();
		} catch (InterruptedException ex) {
			System.out.println(ex);
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @throws InterruptedException 
	 */
	public static void gameManager() throws InterruptedException {

		Map map = new Map("map16x8", 16, 8);
		Tank tank1 = new Tank("tank1", map, 0, 0, Direction.east);

		map.addVehicle(tank1);
		System.out.println(map.vehicles.getInfo());
		System.out.println(map);

		int deplacement = -1;
		for (int k = 0; k < 2; k++) {
			deplacement *= -1;

			for (int i = 0; i < map.getSizeX(); i++) {
				map.vehicles.moveX(deplacement);

				Thread.sleep(200);
				System.out.println(map);
			}
			for (int i = 0; i < map.getSizeY(); i++) {
				map.vehicles.moveY(deplacement);

				Thread.sleep(200);
				System.out.println(map);
			}
		}

		Thread.currentThread().stop();
	}
}
