package navalbattle.datamodel;

import java.util.Date;
import java.util.HashMap;
import navalbattle.datahelper.DateTimeHelper;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmlhelper.XMLHelper;

public class DMServerConfig extends DataModelEntity {

    private String serverName;
    private NavalBattleProtocol.OPPONENT_TYPE mode;
    private boolean requiresPassword;
    private String password;
    private MapSizeEnum mapSize;
    private NavalBattleProtocol.BONUS_STATE areMinesEnabled;
    private NavalBattleProtocol.BONUS_STATE areSatEnabled;

    public void setValues(String serverName, MapSizeEnum mapSize, NavalBattleProtocol.OPPONENT_TYPE mode, boolean requiresPassword, String password, NavalBattleProtocol.BONUS_STATE areMinesEnabled, NavalBattleProtocol.BONUS_STATE areSatEnabled) {
        this.serverName = serverName;
        this.mapSize = mapSize;
        this.mode = mode;
        this.requiresPassword = requiresPassword;
        this.password = password;
        this.areMinesEnabled = areMinesEnabled;
        this.areSatEnabled = areSatEnabled;
    }

    public String getServerName() {
        return serverName;
    }

    public NavalBattleProtocol.OPPONENT_TYPE getMode() {
        return mode;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRequiresPassword() {
        return requiresPassword;
    }

    public NavalBattleProtocol.BONUS_STATE getAreMinesEnabled() {
        return areMinesEnabled;
    }

    public NavalBattleProtocol.BONUS_STATE getAreSatEnabled() {
        return areSatEnabled;
    }
    
    public MapSizeEnum getMapSize() {
        return this.mapSize;
    }

    @Override
    public Node getXMLRepresentation(Document doc) {

        Node rootNode = doc.createElement("client_config");
        
        Node lastParametersNode = doc.createElement("last_parameters");

        {
            Node serverNameMode = doc.createElement("server_name");
            serverNameMode.appendChild(doc.createTextNode(this.serverName));
            lastParametersNode.appendChild(serverNameMode);
            
            Node mapSizeNode = doc.createElement("map_size");
            mapSizeNode.appendChild(doc.createTextNode(Character.toString(this.mapSize.name().toUpperCase().charAt(0))));
            lastParametersNode.appendChild(mapSizeNode);

            Node modeNode = doc.createElement("mode");
            modeNode.appendChild(doc.createTextNode(this.mode == NavalBattleProtocol.OPPONENT_TYPE.HumanVSAI ? "1 versus AI" : "2 versus 2"));
            lastParametersNode.appendChild(modeNode);

            Element authMode = doc.createElement("authorization");
            authMode.setAttribute("type", "shared_secret");
            authMode.setAttribute("state", this.requiresPassword ? "enabled" : "disabled");

            {
                Element passwordNode = doc.createElement("password");
                passwordNode.appendChild(doc.createTextNode(this.requiresPassword ? this.password : ""));

                authMode.appendChild(passwordNode);
            }

            lastParametersNode.appendChild(authMode);

            Element specialsNode = doc.createElement("specials");

            {
                Element specialMinesNode = doc.createElement("special");
                specialMinesNode.setAttribute("name", "mines");
                specialMinesNode.setAttribute("state", this.areMinesEnabled == NavalBattleProtocol.BONUS_STATE.ACTIVATED ? "enabled" : "disabled");
                specialsNode.appendChild(specialMinesNode);

                Element specialSatNode = doc.createElement("special");
                specialSatNode.setAttribute("name", "satellite");
                specialSatNode.setAttribute("state", this.areMinesEnabled == NavalBattleProtocol.BONUS_STATE.ACTIVATED ? "enabled" : "disabled");
                specialsNode.appendChild(specialSatNode);
            }

            lastParametersNode.appendChild(specialsNode);
        }

        rootNode.appendChild(lastParametersNode);
        
        return rootNode;
    }

    @Override
    public DMServerConfig parse(Node rootNode) throws InvalidModelException {

        String serverNameRead;
        MapSizeEnum mapSizeRead;
        NavalBattleProtocol.OPPONENT_TYPE modeRead;
        boolean requiresPasswordRead;
        String passwordRead = null;
        NavalBattleProtocol.BONUS_STATE areMinesEnabledRead = null;
        NavalBattleProtocol.BONUS_STATE areSatEnabledRead = null;

        Node lastParametersNode = XMLHelper.getChildren(rootNode).get("last_parameters");
        HashMap<String, Node> childrenLastParameters = XMLHelper.getChildren(lastParametersNode);

        Node serverNameNode = childrenLastParameters.get("server_name");
        serverNameRead = serverNameNode.getFirstChild().getNodeValue();
        
        Node mapSizeNode = childrenLastParameters.get("map_size");
        
        switch (mapSizeNode.getTextContent())
        {
            case "S":
                mapSizeRead = MapSizeEnum.SMALL;
                break;
                
            case "M":
                mapSizeRead = MapSizeEnum.MEDIUM;
                break;
                
            case "L":
                mapSizeRead = MapSizeEnum.LARGE;
                break;
                
            default:
                throw new InvalidModelException();
        }

        Node modeNode = childrenLastParameters.get("mode");

        switch (modeNode.getFirstChild().getNodeValue()) {
            case "1 versus AI":
                modeRead = NavalBattleProtocol.OPPONENT_TYPE.HumanVSAI;
                break;

            case "2 versus 2":
                modeRead = NavalBattleProtocol.OPPONENT_TYPE.HumanVSHuman;
                break;

            default:
                throw new InvalidModelException();
        }

        Node authNode = childrenLastParameters.get("authorization");
        NamedNodeMap attributes = authNode.getAttributes();

        requiresPasswordRead = attributes.getNamedItem("state").getNodeValue().equals("enabled");

        if (requiresPasswordRead) {
            Node passwordNode = XMLHelper.getChildren(authNode).get("password");
            passwordRead = passwordNode.getFirstChild().getNodeValue();
        }

        Node specialsNode = childrenLastParameters.get("specials");

        NodeList specials = specialsNode.getChildNodes();

        int specialsCount = specials.getLength();
        for (int i = 0; i < specialsCount; ++i) {
            Node currentSpecial = specials.item(i);

            if (currentSpecial.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String name = currentSpecial.getAttributes().getNamedItem("name").getNodeValue();
            NavalBattleProtocol.BONUS_STATE state = (currentSpecial.getAttributes().getNamedItem("state").getNodeValue().equals("enabled") ? NavalBattleProtocol.BONUS_STATE.ACTIVATED : NavalBattleProtocol.BONUS_STATE.DEACTIVATED);

            switch (name) {
                case "mines":

                    if (areMinesEnabledRead != null) {
                        throw new InvalidModelException();
                    }

                    areMinesEnabledRead = state;

                    break;

                case "satellite":

                    if (areSatEnabledRead != null) {
                        throw new InvalidModelException();
                    }

                    areSatEnabledRead = state;

                    break;

                default:
                    throw new InvalidModelException();
            }
        }

        if (areMinesEnabledRead == null || areSatEnabledRead == null) {
            throw new InvalidModelException();
        }

        this.setValues(serverNameRead, mapSizeRead, modeRead, requiresPasswordRead, passwordRead, areMinesEnabledRead, areSatEnabledRead);

        return this;
    }

    
}
