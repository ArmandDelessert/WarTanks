package navalbattle.protocol.messages.common;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleChatReceive extends NavalBattleMessage {

    private String username;
    private String text;
    
    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "chatReceive");
        
        Element user = doc.createElement("username");
        user.appendChild(doc.createTextNode(this.username));
        rootNode.appendChild(user);
        
        Element txt = doc.createElement("text");
        txt.appendChild(doc.createTextNode(this.text));
        rootNode.appendChild(txt);

        doc.appendChild(rootNode);
        
        return doc;
    }

    public String getUsername() {
        return this.username;
    }

    public String getText() {
        return this.text;
    }
    
    public void setValues(String username, String text)
    {
        this.username = username;
        this.text = text;
    }
    
    @Override
    public NavalBattleChatReceive parse(Document message) throws InvalidMessage {
        
        Element rootNode = message.getDocumentElement();
        
        HashMap<String, Node> children = XMLHelper.getChildren(rootNode);
        
        String whoSpeaks = children.get("username").getFirstChild().getNodeValue();
        String whatIsBeingSaid = children.get("text").getFirstChild().getNodeValue();
        
        this.setValues(whoSpeaks, whatIsBeingSaid);
        
        return this;
    }
    
}
