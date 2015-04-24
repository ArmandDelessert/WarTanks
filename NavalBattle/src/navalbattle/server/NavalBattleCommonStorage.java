package navalbattle.server;

import java.util.HashSet;

public class NavalBattleCommonStorage {
    
    HashSet<NavalBattleClientHandler> allClientHandlers;
    
    public NavalBattleCommonStorage()
    {
        this.allClientHandlers = new HashSet<>();      
    }

    public void addClientHandler(NavalBattleClientHandler handler)
    {
        this.allClientHandlers.add(handler);
    }
    
    public int getClientsCount()
    {
        return this.allClientHandlers.size();
    }

    public HashSet<NavalBattleClientHandler> getAllClientHandlers() {
        return allClientHandlers;
    }
}
