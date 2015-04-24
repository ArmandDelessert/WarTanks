package navalbattle.protocol.messages.common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NavalBattleErrorCode {
    private final int errorCode;
    private final String errorDescription;
        
    public Node getXMLRepresentation(Document doc)
    {
        Node error = doc.createElement("error");
        Node code = doc.createElement("code");
        Node description = doc.createElement("description");
        
        code.setTextContent(Integer.toString(this.errorCode));
        description.appendChild(doc.createTextNode(errorDescription));
        
        error.appendChild(code);
        error.appendChild(description);
        
        return error;
    }
    
    public NavalBattleErrorCode(int errorCode, String errorDescription)
    {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

}
