package navalbattle.protocol.messages.common;

import navalbattle.protocol.beaconing.client.ClientBeaconingHelper;
import navalbattle.protocol.beaconing.server.ServerBeaconingHelper;
import navalbattle.protocol.common.BeaconingParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NavalBattleGameParametersResponse extends NavalBattleMessage {

    private BeaconingParameters parameters;

    public BeaconingParameters getParameters() {
        return parameters;
    }

    public void setValues(BeaconingParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public Document getXMLRepresentation() {

        Document doc = ServerBeaconingHelper.getBeaconingMessage(this.parameters);
        Element root = doc.getDocumentElement();
        root.setAttribute("type", "getGameParameters"); // replacing endpointsDiscovery
        return doc;
    }

    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {

        Node root = message.getDocumentElement();
        
        if (!root.getAttributes().getNamedItem("type").getTextContent().equals("getGameParameters")) {
            throw new InvalidMessage();
        }

        this.parameters = ClientBeaconingHelper.parseBeaconingMessage(message);
        return this;
    }

}
