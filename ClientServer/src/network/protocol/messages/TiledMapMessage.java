/**
 * Projet : WarTanks
 * Auteur : Armand Delessert
 * Date   : 06.06.2015
 * 
 * Description :
 * Classe pour la communication de la carte entre le serveur et le client.
 */

package network.protocol.messages;

//import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Armand Delessert
 */
public class TiledMapMessage {

	private final TiledMap tiledMap;

	public TiledMapMessage(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	class TiledMap {}
}
