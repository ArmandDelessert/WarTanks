package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlhelper.XMLHelper;

public class NavalBattleChatSendResponse extends NavalBattleMessage {

    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setValues(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();

        Element rootNode = doc.createElement("response");
        rootNode.setAttribute("type", "chatSend");

        Element random = doc.createElement("nonce");
        random.appendChild(doc.createTextNode(this.nonce));
        rootNode.appendChild(random);

        doc.appendChild(rootNode);

        return doc;
    }

    @Override
    public NavalBattleChatSendResponse parse(Document message) throws InvalidMessage {

        Element rootNode = message.getDocumentElement();
        
        String nonceRead = XMLHelper.getChildren(rootNode).get("nonce").getFirstChild().getNodeValue();

        this.setValues(nonceRead);

        return this;
    }

}
