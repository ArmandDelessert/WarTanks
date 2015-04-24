package navalbattle.server;

import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;

public class GameParameters {
    
    private final String name;
    private final String password;

    private final MapSizeEnum size;
    
    private final NavalBattleProtocol.BONUS_STATE minesActive;
    private final NavalBattleProtocol.BONUS_STATE satelliteActive;
    
    private final NavalBattleProtocol.OPPONENT_TYPE opponentType;

    public GameParameters(String name, String password, MapSizeEnum size, NavalBattleProtocol.OPPONENT_TYPE opponentType, NavalBattleProtocol.BONUS_STATE minesActive, NavalBattleProtocol.BONUS_STATE satelliteActive) {
        this.name = name;
        this.password = password;
        this.size = size;
        this.opponentType = opponentType;
        this.minesActive = minesActive;
        this.satelliteActive = satelliteActive;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
    
    public boolean isPasswordProtected()
    {
        return (password != null);
    }

    public MapSizeEnum getMapSize() {
        return size;
    }
    
    public NavalBattleProtocol.OPPONENT_TYPE getOpponentType() {
        return opponentType;
    }

    public NavalBattleProtocol.BONUS_STATE isBonusMinesEnabled() {
        return minesActive;
    }

    public NavalBattleProtocol.BONUS_STATE isBonusSatelliteEnabled() {
        return satelliteActive;
    }
}
