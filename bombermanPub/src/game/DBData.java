package game;

/**
 * Cette classe représente une case de la carte chargée depuis la base de données.
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class DBData {
	private int x;
	private int y;
	private int data;
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	
	/**
    * @param x the x to set
    */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
    * @return the y
    */
	public int getY() {
		return y;
	}
	
	/**
    * @param y the y to set
    */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
    * @return the data
    */
	public int getData() {
		return data;
	}
	
	/**
    * @param data the data to set
    */
	public void setData(int data) {
		this.data = data;
	}
}