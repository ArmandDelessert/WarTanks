/**
 * Projet : ClientServer
 * Auteur : Armand Delessert
 * Date   : 05.05.2015
 * 
 * Description :
 * Classe Message pour les tests de communication.
 */

package clientserver;

import java.io.Serializable;

/**
 *
 * @author Armand Delessert
 */
public class Message implements Serializable {

	String author;
	String text;
	int size;

	public Message() {}

	public Message(String author, String text) {
		this.author = author;
		this.text = text;
		this.size = text.length();
	}

	public void afficher() {
		System.out.println(">> " + text);
		System.out.println("Author: " + author);
	}
}
