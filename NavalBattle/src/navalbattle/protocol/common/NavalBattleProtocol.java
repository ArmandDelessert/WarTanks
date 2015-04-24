package navalbattle.protocol.common;

public class NavalBattleProtocol {
    public static final int ENDPOINT_PORT = 36817;
    public static int BEACONING_PORT = 36818;
    public static int TIMEOUT = 15; // in seconds
    public static int BEACONING_INTERVAL = 10; // in seconds
    public static String ENCODING = "UTF-8";
    
    
    public enum COORDINATE_TYPE { BOAT,
                                  DAMAGED,
                                  SINKED,
                                  SATELLITE,
                                  MINE,
                                  NOTHING,
                                  FIRED_BUT_DID_NOT_DAMAGE_ANYTHING,
                                  REAVEALED_HAS_SHIP,
                                  REAVEALED_HAS_NO_SHIP }
    
    
    public enum REAON_END_OF_GAME { THERE_IS_WINNER, CANNOT_CONTINUE_GAME }
    
    public enum BONUS_STATE { ACTIVATED, DEACTIVATED }
    public enum OPPONENT_TYPE { HumanVSHuman, HumanVSAI }
    public enum BOAT_TYPE { AIRCRAFT_CARRIER, CRUISER, SUBMARINE, DESTROYER }
    public enum BOAT_ORIENTATION { HORIZONTAL, VERTICAL }
    public enum HIT_ON_BOAT { MISSED, HIT, SINKED, ALREADY_FIRED_ON_THAT_POSITION }
}
