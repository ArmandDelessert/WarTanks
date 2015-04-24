package navalbattle.protocol.messages.common;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleConnect extends NavalBattleMessage {
    
    public enum DIGEST_TYPE { SHARED_SECRET, NONE }
    
    private String username;
    private String password;
    private DIGEST_TYPE digest;
    
    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "connect");
        
        Element res = doc.createElement("username");
        res.appendChild(doc.createTextNode(this.username));
        rootNode.appendChild(res);
        
        Element credentials = doc.createElement("digest");
        credentials.setAttribute("type", this.digest.name().toLowerCase());
        
        if (this.digest == DIGEST_TYPE.SHARED_SECRET)
        {
            Element password = doc.createElement("password");
            password.appendChild(doc.createTextNode(this.password));
            credentials.appendChild(password);
        }
        
        rootNode.appendChild(credentials);

        doc.appendChild(rootNode);

        return doc;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public DIGEST_TYPE getDigest() {
        return this.digest;
    }

    public void setValues(String username, String password, DIGEST_TYPE digest)
    {
        this.username = username;
        this.password = password;
        this.digest = digest;
    }
    
    @Override
    public NavalBattleConnect parse(Document message) throws InvalidMessage
    {
        DIGEST_TYPE d;
        Node rootNode = message.getFirstChild();
        HashMap<String, Node> rootChildren = XMLHelper.getChildren(rootNode);
        String username = rootChildren.get("username").getFirstChild().getNodeValue();
        Node dig = rootChildren.get("digest");
        String authTypeStr = dig.getAttributes().getNamedItem("type").getNodeValue();
        String sharedSecret = null;
                
        switch (authTypeStr)
        {
            case "none":
                    d = DIGEST_TYPE.NONE;
                break;
            
            case "shared_secret":
                    d = DIGEST_TYPE.SHARED_SECRET;
                    sharedSecret = XMLHelper.getChildren(dig).get("password").getFirstChild().getNodeValue();
                break;
                
            default:
                throw new InvalidMessage();
            
        }

        this.setValues(username, sharedSecret, d);
        
        return this;
    }
}
