package navalbattle.protocol.common;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class NavalBattleTimeoutGuard {
    private final int timeout; // in seconds
    private final int period; // in seconds
    private final HashSet<ITimeoutHandler> handlers = new HashSet<>();
    private boolean end = false;
    private Thread check = null;
    private final Object checkSignal = new Object();
    
    public NavalBattleTimeoutGuard(final int timeout, final int period)
    {
        if (timeout < 0 || period < 1)
            throw new InvalidParameterException();
        
        this.timeout = timeout;
        this.period = period;
    }
    
    public boolean isPeriodicallyChecking()
    {
        return (this.check != null && this.check.isAlive());
    }
    
    public void stopPeriodicCheck()
    {
        if (!this.isPeriodicallyChecking())
            return;
        
        end = true;
        
        synchronized (checkSignal)
        {
            checkSignal.notify();
        }
    }
    
    public void startPeriodicCheck()
    {
        if (this.isPeriodicallyChecking())
            return;
        
        end = false;
        
        // This thread will periodically check for timeout
        this.check = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!end)
                {
                    try {
                        checkTimeout();
                        
                        synchronized(checkSignal)
                        {
                            checkSignal.wait(period * 1000);
                        }
                        
                    } catch (InterruptedException ex) {
                    }
                }
                
            }
        });
        
        this.check.start();
    }
    
    public void addHandler(ITimeoutHandler handler)
    {
        synchronized(this.handlers)
        {
            this.handlers.add(handler);
        }
    }
    
    public void removeHandler(ITimeoutHandler handler)
    {
        synchronized(this.handlers)
        {
            if (!this.handlers.contains(handler))
                throw new InvalidParameterException();
            
            this.handlers.remove(handler);
        }
    }
    
    private void checkTimeout()
    {
        final HashSet<ITimeoutHandler> reachedTimeout = new HashSet<>();
        
        synchronized(this.handlers)
        {
            Iterator<ITimeoutHandler> it = this.handlers.iterator();
            
            while (it.hasNext())
            {
                ITimeoutHandler handler = it.next();
                
                Date limit = new Date();
                limit.setTime(limit.getTime() - this.timeout * 1000);
                boolean hasReachedTimeout = handler.lastCommunicationWas().compareTo(limit) < 0;
                
                if (hasReachedTimeout)
                {
                    // Timeout detected
                    
                    reachedTimeout.add(handler);
                    it.remove();
                }
            }
        }
        
        if (!reachedTimeout.isEmpty())
        {
            // Sending the notifications
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (ITimeoutHandler handler : reachedTimeout)
                    {
                        handler.hasReachedTimeout();
                    }

                    reachedTimeout.clear();
                }
            }).start();
        }
    }
}
