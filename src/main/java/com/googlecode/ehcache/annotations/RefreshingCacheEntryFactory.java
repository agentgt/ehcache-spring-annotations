package com.googlecode.ehcache.annotations;

import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;

import org.aopalliance.intercept.MethodInvocation;

import com.googlecode.ehcache.annotations.resolver.ThreadLocalCacheEntryFactory;

/**
 * Entry factory used by {@link RefreshableCacheEntry} that can update {@link RefreshableCacheEntry}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class RefreshingCacheEntryFactory extends ThreadLocalCacheEntryFactory implements UpdatingCacheEntryFactory {

    public void updateEntryValue(Object key, Object value) throws Exception {
        if (!(value instanceof RefreshableCacheEntry)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires cache entries of type " + RefreshableCacheEntry.class.getSimpleName());
        }
        
        final RefreshableCacheEntry refreshableCacheEntry = (RefreshableCacheEntry)value;
        
        final MethodInvocation methodInvocation = refreshableCacheEntry.getMethodInvocation();
        final MethodInvocation existingMethodInvocation = entryFactory.get();
        try {
            entryFactory.set(methodInvocation);
            
            final Object newEntry = this.createEntry(key);
            refreshableCacheEntry.setValue(newEntry);
        }
        finally {
            if (existingMethodInvocation == null) {
                entryFactory.remove();
            }
            else {
                entryFactory.set(existingMethodInvocation);
            }
        }
    }

    @Override
    public Object createEntry(Object key) throws Exception {
        final MethodInvocation methodInvocation = this.entryFactory.get();
        final Object value = super.createEntry(key);
        return new RefreshableCacheEntry(methodInvocation, value);
    }
}
