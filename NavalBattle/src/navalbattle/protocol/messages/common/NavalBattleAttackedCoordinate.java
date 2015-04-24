package navalbattle.protocol.messages.common;

import java.util.ArrayList;
import java.util.HashMap;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class NavalBattleAttackedCoordinate extends NavalBattleMessage {

    private String usernameOpponent = null;
    private ArrayList<CoordinateWithType> allCoordinatesHit = new ArrayList<>();

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "attackedCoordinate");
        
        Element username = doc.createElement("username");
        username.appendChild(doc.createTextNode(this.usernameOpponent));
        rootNode.appendChild(username);

        Element coordinates = doc.createElement("coordinates");

        for (CoordinateWithType coordinateWithType : this.allCoordinatesHit)
        {
            NavalBattleProtocol.COORDINATE_TYPE t = coordinateWithType.getType();
            Element coordinate = doc.createElement("coordinate");
            coordinate.setAttribute("x", Integer.toString(coordinateWithType.getX()));
            coordinate.setAttribute("y", Integer.toString(coordinateWithType.getY()));

            if (t != NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_NO_SHIP &&
                t != NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP)
            {
                coordinate.setAttribute("type", t.name().toLowerCase());
            }
            else
            {
                coordinate.setAttribute("type", "revealed");
                coordinate.setAttribute("has_ship", (t == NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP) ? "true" : "false");
            }

            coordinates.appendChild(coordinate);
        }

        rootNode.appendChild(coordinates);

        doc.appendChild(rootNode);
        
        return doc;
    }
    
    public String getUsernameOpponent() {
        return usernameOpponent;
    }

    public ArrayList<CoordinateWithType> getAllCoordinatesHit() {
        return (ArrayList<CoordinateWithType>)this.allCoordinatesHit.clone();
    }
    
    public void setValues(String usernameOpponent, ArrayList<CoordinateWithType> allCoordinatesHit)
    {
        this.usernameOpponent = usernameOpponent;
        this.allCoordinatesHit = allCoordinatesHit;
    }
    
    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {
        
        Node rootElement = message.getFirstChild();
        
        HashMap<String, Node> rootChildren = XMLHelper.getChildren(rootElement);
        
        String usernamePlayed = rootChildren.get("username").getFirstChild().getNodeValue();
        ArrayList<CoordinateWithType> allCoordinatesWithType = new ArrayList<>();
        NavalBattleErrorCode err = null;

        Node coordinatesNode = rootChildren.get("coordinates");
        NodeList allCoordinates = coordinatesNode.getChildNodes();
        for (int i = 0; i < allCoordinates.getLength() ; ++i)
        {
            Node currentCoordinate = allCoordinates.item(i);
            
            if (currentCoordinate.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            NamedNodeMap attributes = currentCoordinate.getAttributes();
            
            int posX = Integer.parseInt(attributes.getNamedItem("x").getNodeValue());
            int posY = Integer.parseInt(attributes.getNamedItem("y").getNodeValue());
            String typeStr = attributes.getNamedItem("type").getNodeValue();
            NavalBattleProtocol.COORDINATE_TYPE t;

            if (posX < 0 || posY < 0)
                throw new InvalidMessage();

            switch (typeStr)
            {
                case "satellite":

                    t = NavalBattleProtocol.COORDINATE_TYPE.SATELLITE;
                    
                    break;

                case "mine":

                    t = NavalBattleProtocol.COORDINATE_TYPE.MINE;

                    break;

                case "damaged":
                    t = NavalBattleProtocol.COORDINATE_TYPE.DAMAGED;
                    break;

                case "sinked":
                    t = NavalBattleProtocol.COORDINATE_TYPE.SINKED;
                    break;

                case "nothing":

                    t = NavalBattleProtocol.COORDINATE_TYPE.NOTHING;

                    break;

                case "revealed":

                    boolean hasShipOnThatPosition = attributes.getNamedItem("has_ship").getNodeValue().equals("true");

                    if (hasShipOnThatPosition)
                        t = NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP;
                    else
                        t = NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_NO_SHIP;

                    break;

                default:
                    throw new InvalidMessage();
            }

            allCoordinatesWithType.add(new CoordinateWithType(posX, posY, t));
        }

        this.setValues(usernamePlayed, allCoordinatesWithType);

        return this;
    }
    
}
