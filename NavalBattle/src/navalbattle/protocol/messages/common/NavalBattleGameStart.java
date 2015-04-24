package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleGameStart extends NavalBattleMessage {

    private String usernameFirstToPlay;

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "gameStart");
        
        Element firstToPlay = doc.createElement("first_to_play");
        firstToPlay.appendChild(doc.createTextNode(usernameFirstToPlay));
        rootNode.appendChild(firstToPlay);

        doc.appendChild(rootNode);

        return doc;
    }
    
    public String getUsernameFirstToPlay() {
        return usernameFirstToPlay;
    }
    
    public void setValues(String usernameFirstToPlay)
    {
        this.usernameFirstToPlay = usernameFirstToPlay;
    }
    
    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {
        Node rootNode = message.getFirstChild();
        
        String firstToPlay = XMLHelper.getChildren(rootNode).get("first_to_play").getFirstChild().getNodeValue();
        
        this.setValues(firstToPlay);
        
        return this;
    }
    
}
