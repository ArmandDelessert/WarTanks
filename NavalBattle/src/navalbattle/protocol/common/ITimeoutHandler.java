package navalbattle.protocol.common;

import java.util.Date;

public interface ITimeoutHandler {
    
    /**
     * Returns the date of the last activity
     * @return Date
     */
    public Date lastCommunicationWas();
    
    /**
     * Method called when the timeout is reached
     */
    public void hasReachedTimeout();
}
