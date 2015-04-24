package navalbattle.protocol.messages.common;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattlePositionMyBoatsResponse extends NavalBattleMessage {

    public enum RESULT { SUCCESS, ERROR }
    
    private RESULT result;
    private NavalBattleErrorCode error;

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();
        
        Element rootNode = doc.createElement("response");
        rootNode.setAttribute("type", "positionMyBoats");
        
        Element res = doc.createElement("result");
        res.appendChild(doc.createTextNode(this.result.name().toLowerCase()));
        rootNode.appendChild(res);
        
        if (error != null)
            rootNode.appendChild(this.error.getXMLRepresentation(doc));

        doc.appendChild(rootNode);

        return doc;
    }
    
    public RESULT getResult() {
        return this.result;
    }

    public NavalBattleErrorCode getError() {
        return this.error;
    }
    
    public void setValues(RESULT res, NavalBattleErrorCode err)
    {
        this.result = res;
        this.error = err;
    }
    
    @Override
    public NavalBattlePositionMyBoatsResponse parse(Document message) throws InvalidMessage {
        Node rootNode = message.getFirstChild();
        HashMap<String, Node> rootChildren = XMLHelper.getChildren(rootNode);
        
        RESULT r;
        Node resultNode = rootChildren.get("result");
        String positionResult = resultNode.getFirstChild().getNodeValue();
        NavalBattleErrorCode err = null;
                
        switch (positionResult)
        {
            case "success":
                r = RESULT.SUCCESS;
                break;
                
            case "error":
                r = RESULT.ERROR;
                break;

            default:
                throw new InvalidMessage();
        }
        
        if (r == RESULT.ERROR)
        {
            err = this.parseErrorCode(message);
        }

        this.setValues(r, err);
        
        return this;
    }
    
}
