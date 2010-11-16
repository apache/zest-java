package org.qi4j.library.locking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import org.qi4j.api.common.AppliesTo;
import org.qi4j.api.concern.ConcernOf;
import org.qi4j.api.injection.scope.This;

/**
 * Applies write-lock to Composite
 */
@AppliesTo( WriteLock.class )
public class WriteLockConcern
    extends ConcernOf<InvocationHandler>
    implements InvocationHandler
{
    private
    @This
    ReadWriteLock lock;

    public Object invoke( Object o, Method method, Object[] objects )
        throws Throwable
    {
        lock();
        try
        {
            return next.invoke( o, method, objects );
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    /**
     * Fix for this bug:
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6822370
     */
    protected void lock()
    {
       while (true)
       {
          try
          {
             lock.writeLock().tryLock( 1000, TimeUnit.MILLISECONDS );
             break;
          } catch (InterruptedException e)
          {
             // Try again
          }
       }
    }
}