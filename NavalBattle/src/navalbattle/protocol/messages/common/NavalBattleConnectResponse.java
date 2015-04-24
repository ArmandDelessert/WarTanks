package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleConnectResponse extends NavalBattleMessage {

    public enum RESULT { AUTHENTICATED, CONNECTED, AUTHENTICATION_ERROR, CONNECTION_ERROR }
    
    private RESULT result;
    private NavalBattleErrorCode error;
    
    
    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("response");
        rootNode.setAttribute("type", "connect");
        
        Element res = doc.createElement("result");
        res.appendChild(doc.createTextNode(this.result.name().toLowerCase()));
        rootNode.appendChild(res);
        
        if (error != null)
            rootNode.appendChild(this.error.getXMLRepresentation(doc));
        
        doc.appendChild(rootNode);

        return doc;
    }

    public RESULT getResult() {
        return result;
    }

    public NavalBattleErrorCode getError() {
        return error;
    }

    public void setValues(RESULT result, NavalBattleErrorCode error)
    {
        this.result = result;
        this.error = error;
    }
    
    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage
    {
        Node rootNode = message.getFirstChild();
        
        RESULT r;
        Node resultNode = XMLHelper.getChildren(rootNode).get("result");
        String authResult = resultNode.getFirstChild().getNodeValue();
        NavalBattleErrorCode err = null;

        switch (authResult)
        {
            case "authenticated":
                r = RESULT.AUTHENTICATED;
                break;
                
            case "connected":
                r = RESULT.CONNECTED;
                break;
                
            case "authentication_error":
                r = RESULT.AUTHENTICATION_ERROR;
                break;
                
            case "connection_error":
                r = RESULT.CONNECTION_ERROR;
                break;
                
            default:
                throw new InvalidMessage();
        }
        
        if (r == RESULT.AUTHENTICATION_ERROR ||
            r == RESULT.CONNECTION_ERROR)
        {
            err = this.parseErrorCode(message.getFirstChild());
        }

        this.setValues(r, err);
        
        return this;
    }
}
