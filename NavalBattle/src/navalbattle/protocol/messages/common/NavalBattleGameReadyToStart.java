package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlhelper.XMLHelper;

public class NavalBattleGameReadyToStart extends NavalBattleMessage {

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();

        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "gameReadyToStart");

        doc.appendChild(rootNode);

        return doc;
    }

    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {
        // There's nothing to do
        return this;
    }
}
