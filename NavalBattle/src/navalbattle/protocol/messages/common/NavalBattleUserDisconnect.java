package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleUserDisconnect extends NavalBattleMessage {

    private String usernameDisconnect;

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "userDisconnect");
        
        Element username = doc.createElement("username");
        username.appendChild(doc.createTextNode(this.usernameDisconnect));
        rootNode.appendChild(username);

        doc.appendChild(rootNode);

        return doc;
    }
    
    public String getUsernameDisconnected() {
        return this.usernameDisconnect;
    }
    
    public void setValues(String usernameDisconnect)
    {
        this.usernameDisconnect = usernameDisconnect;
    }
    
    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {
        Node rootNode = message.getFirstChild();
        String username = XMLHelper.getChildren(rootNode).get("username").getFirstChild().getNodeValue();
        
        this.setValues(username);

        return this;
    }
    
}
