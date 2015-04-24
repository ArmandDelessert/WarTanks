package navalbattle.protocol.messages.common;

import java.util.HashMap;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlhelper.XMLHelper;

public class NavalBattleEndOfGame extends NavalBattleMessage {

    private NavalBattleProtocol.REAON_END_OF_GAME reason;
    private String usernameWinner = null;

    @Override
    public Document getXMLRepresentation() {
        Document doc = XMLHelper.createBaseDocument();

        Element rootNode = doc.createElement("request");
        rootNode.setAttribute("type", "endOfGame");

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode(this.reason.name().toLowerCase()));
        rootNode.appendChild(type);

        Element winner = doc.createElement("winner");

        if (usernameWinner != null) {
            winner.appendChild(doc.createTextNode(usernameWinner));
        }

        rootNode.appendChild(winner);

        doc.appendChild(rootNode);

        return doc;
    }

    public NavalBattleProtocol.REAON_END_OF_GAME getReason() {
        return this.reason;
    }

    public String getUsernameWinner() {
        return this.usernameWinner;
    }

    public void setValues(NavalBattleProtocol.REAON_END_OF_GAME reason, String usernameWinner) {
        if (reason != NavalBattleProtocol.REAON_END_OF_GAME.THERE_IS_WINNER) {
            usernameWinner = null;
        }

        this.reason = reason;
        this.usernameWinner = usernameWinner;
    }

    @Override
    public NavalBattleMessage parse(Document message) throws InvalidMessage {

        Node rootNode = message.getDocumentElement();
        HashMap<String, Node> rootChildren = XMLHelper.getChildren(rootNode);

        Node resultNode = rootChildren.get("type");
        String endOfGameReason = resultNode.getFirstChild().getNodeValue();
        NavalBattleProtocol.REAON_END_OF_GAME r;
        String winner = null;

        switch (endOfGameReason) {
            case "there_is_winner":
                r = NavalBattleProtocol.REAON_END_OF_GAME.THERE_IS_WINNER;
                break;

            case "cannot_continue_game":
                r = NavalBattleProtocol.REAON_END_OF_GAME.CANNOT_CONTINUE_GAME;
                break;

            default:
                throw new InvalidMessage();
        }

        if (r == NavalBattleProtocol.REAON_END_OF_GAME.THERE_IS_WINNER) {
            winner = rootChildren.get("winner").getFirstChild().getNodeValue();
        }

        this.setValues(r, winner);

        return this;
    }

}
