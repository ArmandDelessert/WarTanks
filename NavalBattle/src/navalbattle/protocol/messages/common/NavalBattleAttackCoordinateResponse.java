package navalbattle.protocol.messages.common;

import java.util.ArrayList;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class NavalBattleAttackCoordinateResponse extends NavalBattleMessage {

    public enum RESULT { SUCCESS, ERROR }
    
    private RESULT result;
    private ArrayList<CoordinateWithType> allCoordinatesHit = new ArrayList<>();
    private NavalBattleErrorCode error;
    
    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("response");
        rootNode.setAttribute("type", "attackCoordinate");
        
        Element result = doc.createElement("result");
        result.setTextContent(this.result == RESULT.SUCCESS ? "success" : "failure");
        
        rootNode.appendChild(result);
        
        if (this.result == RESULT.SUCCESS)
        {
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
        }
        else
        {
            rootNode.appendChild(this.error.getXMLRepresentation(doc));
        }
        
        doc.appendChild(rootNode);
        
        return doc;
    }

    public RESULT getResult() {
        return this.result;
    }

    public ArrayList<CoordinateWithType> getAllCoordinatesHit() {
        return (ArrayList<CoordinateWithType>)this.allCoordinatesHit.clone();
    }

    public NavalBattleErrorCode getError() {
        return this.error;
    }
    
    public void setValues(RESULT result, ArrayList<CoordinateWithType> allCoordinatesHit, NavalBattleErrorCode error)
    {
        this.result = result;
        this.allCoordinatesHit = ((allCoordinatesHit == null) ? new ArrayList<CoordinateWithType>() : (ArrayList<CoordinateWithType>)allCoordinatesHit.clone());
        this.error = error;
    }
    
    @Override
    public NavalBattleAttackCoordinateResponse parse(Document message) throws InvalidMessage {
        
        Node rootNode = message.getDocumentElement();
        
        Node resultNode = XMLHelper.getChildren(rootNode).get("result");
        String attackResult = resultNode.getTextContent();
        ArrayList<CoordinateWithType> allCoordinatesWithType = new ArrayList<>();
        NavalBattleErrorCode err = null;
        RESULT res;
        
        switch(attackResult)
        {
            case "success":
                    res = RESULT.SUCCESS;
                break;
                
            case "failure":
                 res = RESULT.ERROR;
                break;
                
            default:
                throw new InvalidMessage();
        }
        
        if (res == RESULT.SUCCESS)
        {
            NodeList allCoordinates = XMLHelper.getChildren(rootNode).get("coordinates").getChildNodes();
            
            int coordCount = allCoordinates.getLength();
            for (int i = 0; i < coordCount ; ++i)
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

                        boolean hasShipOnThatPosition = attributes.getNamedItem("has_ship").getTextContent().equals("true");

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
            
            if (allCoordinatesWithType.size() < 1)
                throw new InvalidMessage();
        }
        else
        {
            err = this.parseErrorCode(rootNode);
        }
        
        this.setValues(res, allCoordinatesWithType, err);

        return this;
    }
    
}
