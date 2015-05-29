package network.protocol;

/**
 *
 * @author Armand Delessert
 */
public class SomeRequest {

	public String text;

	public SomeRequest() {
		text = "No request";
	}

	public SomeRequest(String request) {
		text = request;
	}
}
