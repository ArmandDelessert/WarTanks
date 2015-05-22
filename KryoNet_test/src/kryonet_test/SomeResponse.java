package kryonet_test;

/**
 *
 * @author Armand Delessert
 */
public class SomeResponse {

	public String text;

	public SomeResponse() {
		text = "No reponse";
	}

	public SomeResponse(String reponse) {
		text = reponse;
	}
}
