package navalbattle.protocol.messages.common;

import java.util.ArrayList;
import navalbattle.boats.BoatPosition;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class NavalBattlePositionMyBoats extends NavalBattleMessage {

    private ArrayList<BoatPosition> boats = null;

    public ArrayList<BoatPosition> getBoats() {
        return boats;
    }

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();

        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "positionMyBoats");

        Element boatsNode = doc.createElement("boats");

        for (BoatPosition boat : this.boats) {
            Element boatNode = doc.createElement("boat");

            boatNode.setAttribute("x", Integer.toString(boat.getX()));
            boatNode.setAttribute("y", Integer.toString(boat.getY()));
            boatNode.setAttribute("size", Integer.toString(boat.getLength()));
            boatNode.setAttribute("orientation", ((boat.getOrientation() == NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL) ? "horizontal" : "vertical"));

            boatsNode.appendChild(boatNode);
        }

        rootNode.appendChild(boatsNode);
        doc.appendChild(rootNode);

        return doc;
    }

    @Override
    public NavalBattlePositionMyBoats parse(Document message) throws InvalidMessage {
        ArrayList<BoatPosition> boatsRead = new ArrayList<>();

        Node rootNode = message.getDocumentElement();
        Node boatsNode = XMLHelper.getChildren(rootNode).get("boats");
        
        NodeList allBoats = boatsNode.getChildNodes();
        
        final int boatsCount = allBoats.getLength();
        for (int i = 0; i < boatsCount; ++i) {
            int x;
            int y;
            int length;
            NavalBattleProtocol.BOAT_ORIENTATION orientation;

            Node boatNode = allBoats.item(i);
            
            if (boatNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            NamedNodeMap nodeAttributes = boatNode.getAttributes();

            x = Integer.parseInt(nodeAttributes.getNamedItem("x").getNodeValue());
            y = Integer.parseInt(nodeAttributes.getNamedItem("y").getNodeValue());
            length = Integer.parseInt(nodeAttributes.getNamedItem("size").getNodeValue());

            if (x < 0 || y < 0 || length <= 0) {
                throw new InvalidMessage();
            }

            switch (nodeAttributes.getNamedItem("orientation").getTextContent()) {
                case "horizontal":
                    orientation = NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL;
                    break;

                case "vertical":
                    orientation = NavalBattleProtocol.BOAT_ORIENTATION.VERTICAL;
                    break;

                default:
                    throw new InvalidMessage();
            }

            boatsRead.add(new BoatPosition(x, y, orientation, length));
        }

        this.setValues(boatsRead);

        return this;
    }

    public void setValues(ArrayList<BoatPosition> boats) {
        this.boats = boats;
    }

}
