/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 21.03.2015
 */

package wartanks;

import map.Map;
import vehicle.*;


/**
 *
 * @author Armand
 */
public class WarTanks {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws InterruptedException {
		Map map = new Map("map16x8", 16, 8);
		Tank tank1 = new Tank("tank1", map, 0, 0, "est");

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
	}
}
