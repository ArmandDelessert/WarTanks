package navalbattle.protocol.messages.common;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public abstract class NavalBattleMessage {
    public abstract Document getXMLRepresentation();
    
    public abstract NavalBattleMessage parse(Document message) throws InvalidMessage;
    
    public NavalBattleErrorCode parseErrorCode(Node nodeContainingErrorNode)
    {
        HashMap<String, Node> errorRegion = XMLHelper.getChildren(XMLHelper.getChildren(nodeContainingErrorNode).get("error"));
        int errorCode = Integer.parseInt(errorRegion.get("code").getFirstChild().getNodeValue());
        String errorDescription = errorRegion.get("description").getFirstChild().getNodeValue();

        return new NavalBattleErrorCode(errorCode, errorDescription);
    }
}
