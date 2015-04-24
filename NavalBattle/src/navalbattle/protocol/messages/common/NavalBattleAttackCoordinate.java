package navalbattle.protocol.messages.common;


import java.security.InvalidParameterException;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleAttackCoordinate extends NavalBattleMessage {

    int x, y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setValues(int x, int y)
    {
        if (x < 0 || y < 0)
            throw new InvalidParameterException();
        
        this.x = x;
        this.y = y;
    }
    
    @Override
    public NavalBattleAttackCoordinate parse(Document message) throws InvalidMessage {
        Node rootNode = message.getDocumentElement();
        Node coordinateNode = XMLHelper.getChildren(rootNode).get("coordinates");
        HashMap<String, Node> coordinateChildren = XMLHelper.getChildren(coordinateNode);
        
        int posX = Integer.valueOf(coordinateChildren.get("x").getFirstChild().getNodeValue());
        int posY = Integer.valueOf(coordinateChildren.get("y").getFirstChild().getNodeValue());
        
        this.setValues(posX, posY);

        return this;
    }

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "attackCoordinate");
        
        Element coordinates = doc.createElement("coordinates");
        Element coordinateX = doc.createElement("x");
        Element coordinateY = doc.createElement("y");
        
        coordinateX.setTextContent(Integer.toString(this.x));
        coordinateY.setTextContent(Integer.toString(this.y));
        
        coordinates.appendChild(coordinateX);
        coordinates.appendChild(coordinateY);
        
        rootNode.appendChild(coordinates);
        
        doc.appendChild(rootNode);
        
        return doc;
    }
    
}

