package navalbattle.protocol.beaconing.server;

import navalbattle.protocol.common.BeaconingParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlhelper.XMLHelper;


public class ServerBeaconingHelper {
    public static Document getBeaconingMessage(BeaconingParameters beaconParameters)
    {
        Document doc = XMLHelper.createBaseDocument();

        Element rootElement = doc.createElement("response");
        rootElement.setAttribute("type", "endpointsDiscovery");
        rootElement.setAttribute("protocol", "1.0");

        Element name = doc.createElement("name");
        name.setTextContent(beaconParameters.getServerName());

        Element auth = doc.createElement("auth");
        auth.setTextContent(beaconParameters.isAuthenticationRequired() ? "true" : "false");

        Element players = doc.createElement("players");
        players.setTextContent(Integer.toString(beaconParameters.getPlayersConnected()));

        Element isFull = doc.createElement("is_full");
        isFull.setTextContent(beaconParameters.isServerFull() ? "true" : "false");

        Element mapSize = doc.createElement("size");

        // S, M or L
        mapSize.setTextContent(beaconParameters.getMapSize().name().substring(0, 1).toUpperCase());

        rootElement.appendChild(name);
        rootElement.appendChild(auth);
        rootElement.appendChild(players);
        rootElement.appendChild(isFull);
        rootElement.appendChild(mapSize);

        doc.appendChild(rootElement);
        
        return doc;
    }
}
