package navalbattle.protocol.common;

public class CoordinateWithType extends Coordinate {

    private final NavalBattleProtocol.COORDINATE_TYPE t;

    public CoordinateWithType(int x, int y, NavalBattleProtocol.COORDINATE_TYPE t)
    {
        super(x, y);
        this.t = t;
    }
    
    public NavalBattleProtocol.COORDINATE_TYPE getType()
    {
        return this.t;
    }
    
    @Override
    public String toString()
    {
        return super.toString() + " type : " + this.t.name();
    }
}
