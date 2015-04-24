package navalbattle.protocol.beaconing.client;

import java.util.HashMap;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.messages.common.InvalidMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class ClientBeaconingHelper {

    public static BeaconingParameters parseBeaconingMessage(Document document) throws InvalidMessage {
        Node responseNode = document.getDocumentElement();
        String messageType = null;

        try {
            messageType = responseNode.getAttributes().getNamedItem("type").getTextContent();
        } catch (Exception ex) {
        }

        if (messageType != null) {
            HashMap<String, Node> children = XMLHelper.getChildren(responseNode);

            String serverName = children.get("name").getTextContent();
            boolean requiresAuthentication = children.get("auth").getTextContent().equals("true");
            int playersCount = (int) Integer.parseInt(children.get("players").getTextContent());
            boolean isServerFull = children.get("is_full").getTextContent().equals("true");
            MapSizeEnum mapSize;

            switch (children.get("size").getTextContent()) {
                case "S":
                    mapSize = MapSizeEnum.SMALL;
                    break;

                case "M":
                    mapSize = MapSizeEnum.MEDIUM;
                    break;

                case "L":
                    mapSize = MapSizeEnum.LARGE;
                    break;
                    
                default:
                    throw new InvalidMessage();
            }

            return new BeaconingParameters(serverName, requiresAuthentication, playersCount, isServerFull, mapSize);
        }
        else
            throw new InvalidMessage();
    }
}
