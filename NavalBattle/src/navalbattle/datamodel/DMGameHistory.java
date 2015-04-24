package navalbattle.datamodel;

import java.util.Date;
import java.util.HashMap;
import navalbattle.datahelper.DateTimeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class DMGameHistory extends DataModelEntity {

    private Date date;
    private String usernameWinner;
    private String usernameNotWinner;

    public void setValues(Date date, String usernameWinner, String usernameNotWinner) {
        this.date = date;
        this.usernameWinner = usernameWinner;
        this.usernameNotWinner = usernameNotWinner;
    }

    public Date getDate() {
        return this.date;
    }

    public String getUsernameWinner() {
        return this.usernameWinner;
    }

    public String getUsernameNotWinner() {
        return this.usernameNotWinner;
    }

    @Override
    public Node getXMLRepresentation(Document doc) {
        
        Node gameNode = doc.createElement("game");

        {
            {
                Node dateNode = doc.createElement("date");
                dateNode.appendChild(doc.createTextNode(DateTimeHelper.dateToUniversalDateString(this.date)));
                
                gameNode.appendChild(dateNode);
            }
            
            {
                Node playersNode = doc.createElement("players");

                {
                    Element winnerNode = doc.createElement("player");
                    winnerNode.setAttribute("winner", "yes");
                    winnerNode.appendChild(doc.createTextNode(usernameWinner));

                    Element notWinnerNode = doc.createElement("player");
                    notWinnerNode.setAttribute("winner", "no");
                    notWinnerNode.appendChild(doc.createTextNode(usernameNotWinner));
                    
                    playersNode.appendChild(winnerNode);
                    playersNode.appendChild(notWinnerNode);
                }
                
                gameNode.appendChild(playersNode);
            }
        }

        return gameNode;
    }

    @Override
    public DataModelEntity parse(Node gameNode) throws InvalidModelException {
        
        Date dateRead = null;
        String usernameWinnerRead = null;
        String usernameNotWinnerRead = null;
        
        HashMap<String, Node> gameChildren = XMLHelper.getChildren(gameNode);
        
        Node dateNode = gameChildren.get("date");
        dateRead = DateTimeHelper.dateFromUniversalDateString(dateNode.getTextContent());
        
        Node playersNode = gameChildren.get("players");
        
        NodeList allPlayers = playersNode.getChildNodes();
            
        int playersCount = allPlayers.getLength();
        for (int i = 0; i < playersCount ; ++i)
        {
            Node currentPlayer = allPlayers.item(i);

            if (currentPlayer.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            boolean isWinner = currentPlayer.getAttributes().getNamedItem("winner").getTextContent().equals("yes");
            String usernameRead = currentPlayer.getFirstChild().getNodeValue();
            
            if (isWinner)
            {
                if (usernameWinnerRead != null)
                    throw new InvalidModelException();
                
                usernameWinnerRead = usernameRead;
            }
            else
            {
                if (usernameNotWinnerRead != null)
                    throw new InvalidModelException();
                
                usernameNotWinnerRead = usernameRead;
            }
        }
        
        if (usernameWinnerRead == null || usernameNotWinnerRead == null)
            throw new InvalidModelException();


        this.setValues(dateRead, usernameWinnerRead, usernameNotWinnerRead);

        return this;
    }
}
