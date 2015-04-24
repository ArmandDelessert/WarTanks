package navalbattle.protocol.common;

import java.security.InvalidParameterException;


public class Coordinate {
    private int x, y;
    
    public Coordinate() {}
    
    public Coordinate(int x, int y)
    {
        if (x < 0 || y < 0)
            throw new InvalidParameterException();
        
        this.x = x;
        this.y = y;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    @Override
    public String toString()
    {
        return "(" + this.x + "," + this.y + ")";
    }
}
