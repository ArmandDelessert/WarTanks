package navalbattle.storage;

import navalbattle.datamodel.*;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class StorageUtils {

    public static final String xmlFiles = "storage/";
    
    public static final String clientConfigFile = xmlFiles + "client_config.xml";
    public static final String gamesHistoryFile = xmlFiles + "games_history.xml";
    public static final String serverConfigFile = xmlFiles + "server_config.xml";

    public static final String[] storageFiles = {clientConfigFile, gamesHistoryFile,
        serverConfigFile };

    public static Document readServerHistoryConfigFile() throws Exception {
        return XMLHelper.parseXMLFromFile(gamesHistoryFile);
    }
    
    public static Document readClientConfigFile() throws Exception {
        return XMLHelper.parseXMLFromFile(clientConfigFile);
    }
    
    public static Document readServerConfigFile() throws Exception {
        return XMLHelper.parseXMLFromFile(serverConfigFile);
    }
    
    public static DMClientConfig getClientConfig() throws Exception {
        Document clientConfigContents = StorageUtils.readClientConfigFile();
        
        DMClientConfig dataEntity = new DMClientConfig();
        return dataEntity.parse(clientConfigContents.getDocumentElement());
    }
    
    public static DMServerConfig getServerConfig() throws Exception {
        Document serverConfigContents = StorageUtils.readServerConfigFile();
        
        DMServerConfig dataEntity = new DMServerConfig();
        return dataEntity.parse(serverConfigContents.getDocumentElement());
    }
    
    public static ArrayList<DMGameHistory> getServerHistory() throws Exception {
    
        Document serverHistoryContents = StorageUtils.readServerHistoryConfigFile();
        
        ArrayList<DMGameHistory> ret = new ArrayList<>();

        Node rootElement = serverHistoryContents.getDocumentElement();
        NodeList games = rootElement.getChildNodes();
        
        final int gamesCount = games.getLength();
        for (int i = 0; i < gamesCount; ++i) {
            Node game = games.item(i);

            if (game.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            DMGameHistory gameEntity = new DMGameHistory();
            gameEntity.parse(game);
            
            ret.add(gameEntity);
        }

        return ret;
    }
    
    public static void writeClientConfigFile(DMClientConfig entity) throws Exception
    {
        Document doc = XMLHelper.createBaseDocument();
        Node xmlRepresentation = entity.getXMLRepresentation(doc);
        
        doc.appendChild(xmlRepresentation);
        
        XMLHelper.writeXMLToFile(doc, "UTF-8", clientConfigFile);
    }
    
    public static void writeServerConfigFile(DMServerConfig entity) throws Exception
    {
        Document doc = XMLHelper.createBaseDocument();
        Node xmlRepresentation = entity.getXMLRepresentation(doc);
        
        doc.appendChild(xmlRepresentation);
        
        XMLHelper.writeXMLToFile(doc, "UTF-8", serverConfigFile);
    }
    
    public static void addGameHistory(DMGameHistory entity) throws Exception
    {
        Document doc = StorageUtils.readServerHistoryConfigFile();
        
        doc.getDocumentElement().appendChild(entity.getXMLRepresentation(doc));
        
        XMLHelper.writeXMLToFile(doc, "UTF-8", gamesHistoryFile);
    }
}
