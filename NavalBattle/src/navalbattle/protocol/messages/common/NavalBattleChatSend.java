package navalbattle.protocol.messages.common;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleChatSend extends NavalBattleMessage {

    private String text;
    private String nonce;

    public String getText() {
        return text;
    }

    public String getNonce() {
        return nonce;
    }
    public void setValues(String nonce, String text) {
        this.nonce = nonce;
        this.text = text;
    }

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();

        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "chatSend");

        Element random = doc.createElement("nonce");
        random.appendChild(doc.createTextNode(this.nonce));
        rootNode.appendChild(random);

        Element txt = doc.createElement("text");
        txt.appendChild(doc.createTextNode(this.text));
        rootNode.appendChild(txt);

        doc.appendChild(rootNode);

        return doc;
    }

    @Override
    public NavalBattleChatSend parse(Document message) throws InvalidMessage {

        Element rootNode = message.getDocumentElement();
        HashMap<String, Node> children = XMLHelper.getChildren(rootNode);
        
        String nonceRead = children.get("nonce").getFirstChild().getNodeValue();
        String textRead = children.get("text").getFirstChild().getNodeValue();

        this.setValues(nonceRead, textRead);

        return this;
    }

}