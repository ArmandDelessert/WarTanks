package navalbattle.server.tests;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.*;
import navalbattle.client.viewmodelmessages.ConnectParameters;
import navalbattle.client.models.ModelConnectServer;
import navalbattle.protocol.client.NavalBattleClientMessagesHelper;
import navalbattle.protocol.common.CoordinateWithType;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.InvalidMessage;
import navalbattle.protocol.messages.common.NavalBattleAttackedCoordinate;
import navalbattle.protocol.messages.common.NavalBattleConnect;
import navalbattle.protocol.messages.common.NavalBattleEndOfGame;
import navalbattle.protocol.messages.common.NavalBattleGameStart;
import navalbattle.protocol.messages.common.NavalBattleMessage;
import navalbattle.protocol.messages.common.NavalBattlePositionMyBoatsResponse;
import navalbattle.protocol.messages.common.NavalBattleUserDisconnect;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 *
 * @author JAMA
 */
public class Test_NavalBattleClientMessagesHelper {

    File clientMessagesRoot = new File(new File("tests/", "res/"), "client_messages/");
    File test1 = new File(clientMessagesRoot, "attackedCoordinate_1.txt");
    File test2 = new File(clientMessagesRoot, "endOfGame_1.txt");
    File test3 = new File(clientMessagesRoot, "userDisconnect_1.txt");
    File test4 = new File(clientMessagesRoot, "positionMyBoats_1.txt");
    File test5 = new File(clientMessagesRoot, "gameStart_1.txt");

    public Test_NavalBattleClientMessagesHelper() throws IOException, ParserConfigurationException, SAXException {
        String test1Contents = this.readFile(test1.getPath(), Charset.forName("UTF-8"));
        String test2Contents = this.readFile(test2.getPath(), Charset.forName("UTF-8"));
        String test3Contents = this.readFile(test3.getPath(), Charset.forName("UTF-8"));
        String test4Contents = this.readFile(test4.getPath(), Charset.forName("UTF-8"));
        String test5Contents = this.readFile(test5.getPath(), Charset.forName("UTF-8"));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc1 = builder.parse(new InputSource(new StringReader(test1Contents)));
        Document doc2 = builder.parse(new InputSource(new StringReader(test2Contents)));
        Document doc3 = builder.parse(new InputSource(new StringReader(test3Contents)));
        Document doc4 = builder.parse(new InputSource(new StringReader(test4Contents)));
        Document doc5 = builder.parse(new InputSource(new StringReader(test5Contents)));

        System.out.println("Test1 : " + (this.test1(doc1) ? "Success" : "Failed"));
        System.out.println("Test2 : " + (this.test2(doc2) ? "Success" : "Failed"));
        System.out.println("Test3 : " + (this.test3(doc3) ? "Success" : "Failed"));
        System.out.println("Test4 : " + (this.test4(doc4) ? "Success" : "Failed"));
        System.out.println("Test5 : " + (this.test5(doc5) ? "Success" : "Failed"));
        System.out.println("Test6 : " + (this.test6() ? "Success" : "Failed"));
    }


    private boolean test1(Document xml) {
        NavalBattleMessage response;

        try {
            response = NavalBattleClientMessagesHelper.parseMessageToRepresentation(xml, null);
        } catch (InvalidMessage ex) {
            return false;
        }

        if (response.getClass() != NavalBattleAttackedCoordinate.class) {
            return false;
        }

        NavalBattleAttackedCoordinate message = (NavalBattleAttackedCoordinate) response;
        ArrayList<CoordinateWithType> allCoordinatesHit = message.getAllCoordinatesHit();

        if (!message.getUsernameOpponent().equals("Luc")) {
            return false;
        }

        if (allCoordinatesHit.size() != 3) {
            return false;
        }

        CoordinateWithType coord0 = allCoordinatesHit.get(0);
        CoordinateWithType coord1 = allCoordinatesHit.get(1);
        CoordinateWithType coord2 = allCoordinatesHit.get(2);

        if (coord0.getType() != NavalBattleProtocol.COORDINATE_TYPE.MINE
                || coord0.getX() != 5 || coord0.getY() != 5) {
            return false;
        }

        if (coord1.getType() != NavalBattleProtocol.COORDINATE_TYPE.DAMAGED
                || coord1.getX() != 5 || coord1.getY() != 6) {
            return false;
        }

        if (coord2.getType() != NavalBattleProtocol.COORDINATE_TYPE.SINKED
                || coord2.getX() != 6 || coord2.getY() != 6) {
            return false;
        }

        return true;
    }

    private boolean test2(Document xml) {
        NavalBattleMessage response;

        try {
            response = NavalBattleClientMessagesHelper.parseMessageToRepresentation(xml, null);
        } catch (InvalidMessage ex) {
            return false;
        }

        if (response.getClass() != NavalBattleEndOfGame.class) {
            return false;
        }

        NavalBattleEndOfGame message = (NavalBattleEndOfGame) response;

        if (message.getReason() != NavalBattleProtocol.REAON_END_OF_GAME.THERE_IS_WINNER) {
            return false;
        }

        if (!message.getUsernameWinner().equals("Paul")) {
            return false;
        }

        return true;
    }

    private boolean test3(Document xml) {
        NavalBattleMessage response;

        try {
            response = NavalBattleClientMessagesHelper.parseMessageToRepresentation(xml, null);
        } catch (InvalidMessage ex) {
            return false;
        }

        if (response.getClass() != NavalBattleUserDisconnect.class) {
            return false;
        }

        NavalBattleUserDisconnect message = (NavalBattleUserDisconnect) response;

        if (!message.getUsernameDisconnected().equals("Paul")) {
            return false;
        }

        return true;
    }

    private boolean test4(Document xml) {
        NavalBattleMessage response;

        try {
            response = NavalBattleClientMessagesHelper.parseMessageToRepresentation(xml, null);
        } catch (InvalidMessage ex) {
            return false;
        }

        if (response.getClass() != NavalBattlePositionMyBoatsResponse.class) {
            return false;
        }

        NavalBattlePositionMyBoatsResponse message = (NavalBattlePositionMyBoatsResponse) response;

        if (message.getResult() != NavalBattlePositionMyBoatsResponse.RESULT.SUCCESS) {
            return false;
        }

        return true;
    }

    private boolean test5(Document xml) {
        NavalBattleMessage response;

        try {
            response = NavalBattleClientMessagesHelper.parseMessageToRepresentation(xml, null);
        } catch (InvalidMessage ex) {
            return false;
        }

        if (response.getClass() != NavalBattleGameStart.class) {
            return false;
        }

        NavalBattleGameStart message = (NavalBattleGameStart) response;

        if (!message.getUsernameFirstToPlay().equals("Luc")) {
            return false;
        }

        return true;
    }

    private boolean test6() {
        ConnectParameters param = new ConnectParameters("127.0.0.1", "myusername", null, NavalBattleConnect.DIGEST_TYPE.NONE);

        ModelConnectServer controllerConnectServer = new ModelConnectServer(null, param, "127.0.0.1");

        return true;
    }

    private String readFile(String p, Charset e) throws IOException {
        return new String(Files.readAllBytes(Paths.get(p)), e);
    }
}
