package com.googlecode.ehcache.annotations.resolver;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import org.aopalliance.intercept.MethodInvocation;

/**
 * EhCache entry factory that uses a ThreadLocal to pass a MethodInvocation into the factory
 * for object creation.
 */
public class ThreadLocalCacheEntryFactory implements CacheEntryFactory {
    protected final ThreadLocal<MethodInvocation> entryFactory = new ThreadLocal<MethodInvocation>();

    public Object createEntry(Object key) throws Exception {
        final MethodInvocation methodInvocation = this.entryFactory.get();
        if (methodInvocation == null) {
            throw new RuntimeException("No MethodInvocation specified in the ThreadLocal");
        }
        
        try {
            return methodInvocation.proceed();
        }
        catch (Throwable t) {
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            else if (t instanceof Error) {
                throw (Error)t;
            }
            
            throw new Exception(t);
        }
    }
}