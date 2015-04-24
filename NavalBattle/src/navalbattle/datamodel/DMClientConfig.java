package navalbattle.datamodel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class DMClientConfig extends DataModelEntity {
    private String username;

    public String getUsername() {
        return this.username;
    }

    @Override
    public Node getXMLRepresentation(Document doc) {
        Node rootNode = doc.createElement("client_config");
        
        Node usernameNode = doc.createElement("username");
        usernameNode.appendChild(doc.createTextNode(username));
        
        rootNode.appendChild(usernameNode);
        
        return rootNode;
    }

    @Override
    public DMClientConfig parse(Node rootNode) throws InvalidModelException {
        Node userNode = XMLHelper.getChildren(rootNode).get("username");
        String usernameRead = ((userNode.getChildNodes().getLength() == 0) ? "" : userNode.getFirstChild().getNodeValue());
        
        this.setValues(usernameRead);
        
        return this;
    }

    public void setValues(String username) {
        this.username = username;
    }
}
