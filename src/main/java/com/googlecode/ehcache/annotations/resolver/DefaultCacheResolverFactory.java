/**
 * Copyright 2010 Nicholas Blair, Eric Dalquist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.ehcache.annotations.resolver;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.CacheNotFoundException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.SelfPopulatingCacheScope;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * Cache resolver that simply looks up the specified caches by name and returns {@link SingletonCacheableCacheResolver} or
 * {@link SingletonTriggersRemoveCacheResolver} instances.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class DefaultCacheResolverFactory implements CacheResolverFactory {
    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ConcurrentMap<String, SelfPopulatingCacheTracker> selfPopulatingCaches = new ConcurrentHashMap<String, SelfPopulatingCacheTracker>(); 
    private final CacheManager cacheManager;
    private boolean createCaches = false;
    private SelfPopulatingCacheScope selfPopulatingCacheScope = SelfPopulatingCacheScope.SHARED;
    
    public DefaultCacheResolverFactory(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public boolean isCreateCaches() {
        return this.createCaches;
    }

    public void setCreateCaches(boolean createCaches) {
        this.createCaches = createCaches;
    }
    
    public void setSelfPopulatingCacheScope(SelfPopulatingCacheScope selfPopulatingCacheScope) {
        this.selfPopulatingCacheScope = selfPopulatingCacheScope;
    }

    protected CacheManager getCacheManager() {
        return this.cacheManager;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.resolver.CacheResolverFactory#getCacheResolver(com.googlecode.ehcache.annotations.Cacheable, java.lang.reflect.Method)
     */
    public CacheableCacheResolver getCacheResolver(Cacheable cacheable, Method method) {
        final String cacheName = cacheable.cacheName();
        Ehcache cache = this.getCache(cacheName);
        
        ThreadLocal<MethodInvocation> entryFactory = null; 
        if (cacheable.selfPopulating()) {
            final SelfPopulatingCacheTracker selfPopulatingCacheTracker = this.createSelfPopulatingCacheInternal(cache);
            cache = selfPopulatingCacheTracker.selfPopulatingCache;
            entryFactory = selfPopulatingCacheTracker.cacheEntryFactory;
        }
        
        final String exceptionCacheName = cacheable.exceptionCacheName();
        if (StringUtils.hasLength(exceptionCacheName)) {
            final Ehcache exceptionCache = this.getCache(exceptionCacheName);
            return new SingletonCacheableCacheResolver(cache, entryFactory, exceptionCache);
        }

        return new SingletonCacheableCacheResolver(cache, entryFactory);
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.resolver.CacheResolverFactory#getCacheResolver(com.googlecode.ehcache.annotations.TriggersRemove, java.lang.reflect.Method)
     */
    public TriggersRemoveCacheResolver getCacheResolver(TriggersRemove triggersRemove, Method method) {
        final String[] cacheNames = triggersRemove.cacheName();
        final Set<Ehcache> caches = new LinkedHashSet<Ehcache>(cacheNames.length);
        for (final String cacheName : cacheNames) {
            final Ehcache cache = this.getCache(cacheName);
            caches.add(cache);
        }
        
        return new SingletonTriggersRemoveCacheResolver(caches);
    }

    protected Ehcache getCache(String cacheName) {
        final CacheManager cacheManager = this.getCacheManager();
        
        Ehcache cache = cacheManager.getEhcache(cacheName);
        if (cache == null) {
            if (this.createCaches) {
                this.logger.warn("No cache named '{}' exists, it will be created from the defaultCache", cacheName);
                try {
                    cacheManager.addCache(cacheName);
                }
                catch (ObjectExistsException oee) {
                    this.logger.trace("Race condition creating missing cache '{}', ignoring and retrieving existing cache", cacheName);
                }
                cache = cacheManager.getEhcache(cacheName);
            }
            else {
                throw new CacheNotFoundException(cacheName);
            }
        }
        return cache;
    }
    
    /**
     * Creates or retrieves a SelfPopulatingCacheTracker for the specified cache depending on the
     * configured {@link SelfPopulatingCacheScope}
     * 
     * @param cache The cache to create a self populating instance of
     * @return The SelfPopulatingCache and corresponding factory object to use
     */
    protected final SelfPopulatingCacheTracker createSelfPopulatingCacheInternal(Ehcache cache) {
        //If method scoped just create a new instance 
        if (SelfPopulatingCacheScope.METHOD == this.selfPopulatingCacheScope) {
            return this.createSelfPopulatingCache(cache);
        }

        //Shared scope, try loading the instance from local Map
        
        //See if there is a cached SelfPopulatingCache for the name
        final String cacheName = cache.getName();
        SelfPopulatingCacheTracker selfPopulatingCacheTracker = this.selfPopulatingCaches.get(cacheName);
        if (selfPopulatingCacheTracker == null) {
            selfPopulatingCacheTracker = this.createSelfPopulatingCache(cache);
            
            //do putIfAbsent to handle concurrent creation. If a value is returned it was already put and that
            //value should be used. If no value was returned the newly created selfPopulatingCache should be used
            final SelfPopulatingCacheTracker existing = this.selfPopulatingCaches.putIfAbsent(cacheName, selfPopulatingCacheTracker);
            if (existing != null) {
                selfPopulatingCacheTracker = existing;
            }
        }
        
        return selfPopulatingCacheTracker;
    }

    /**
     * Create a new {@link SelfPopulatingCache} and corresponding {@link CacheEntryFactory}
     */
    protected SelfPopulatingCacheTracker createSelfPopulatingCache(Ehcache cache) {
        final ThreadLocalCacheEntryFactory cacheEntryFactory = new ThreadLocalCacheEntryFactory();
        final SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(cache, cacheEntryFactory);
        return new SelfPopulatingCacheTracker(selfPopulatingCache, cacheEntryFactory.entryFactory);
    }
    
    protected static class SelfPopulatingCacheTracker {
        public final SelfPopulatingCache selfPopulatingCache;
        public final ThreadLocal<MethodInvocation> cacheEntryFactory;
        
        public SelfPopulatingCacheTracker(SelfPopulatingCache selfPopulatingCache, ThreadLocal<MethodInvocation> cacheEntryFactory) {
            this.selfPopulatingCache = selfPopulatingCache;
            this.cacheEntryFactory = cacheEntryFactory;
        }
    }

    /**
     * EhCache entry factory that uses a ThreadLocal to pass a MethodInvocation into the factory
     * for object creation.
     */
    protected static class ThreadLocalCacheEntryFactory implements CacheEntryFactory {
        public final ThreadLocal<MethodInvocation> entryFactory = new ThreadLocal<MethodInvocation>();

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
}
