package navalbattle.server;

public class WinnerAndLoser {

    private final NavalBattleClientHandler winner;
    private final NavalBattleClientHandler loser;
    
    public WinnerAndLoser(NavalBattleClientHandler winner, NavalBattleClientHandler loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public NavalBattleClientHandler getWinner() {
        return winner;
    }

    public NavalBattleClientHandler getLoser() {
        return loser;
    }
}
