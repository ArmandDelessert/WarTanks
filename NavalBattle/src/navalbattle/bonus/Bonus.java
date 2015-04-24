package navalbattle.bonus;

import navalbattle.server.PlayerGrid;

public abstract class Bonus {
    private boolean isActive = true;

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public abstract Object fire(int x, int y, PlayerGrid playerWhoWillSufferDamages, PlayerGrid playerWhoFired);
}
