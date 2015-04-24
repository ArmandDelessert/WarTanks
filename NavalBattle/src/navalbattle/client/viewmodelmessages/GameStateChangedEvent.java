package navalbattle.client.viewmodelmessages;

import navalbattle.client.models.ModelGameMain;

public class GameStateChangedEvent {

    private final ModelGameMain.WHO who;
    
    public GameStateChangedEvent(ModelGameMain.WHO who) {
        this.who = who;
    }

    public ModelGameMain.WHO getWho() {
        return this.who;
    }
}
