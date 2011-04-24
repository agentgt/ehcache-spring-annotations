/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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

import java.io.Serializable;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Resolver that returns the {@link Ehcache}s provided to the constructor, no resolution logic is performed.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SingletonCacheableCacheResolver implements CacheableCacheResolver {
    private final Ehcache cache;
    private final ThreadLocal<MethodInvocation> selfPopulatingEntryFactory;
    private final Ehcache exceptionCache;
    
    public SingletonCacheableCacheResolver(Ehcache cache, ThreadLocal<MethodInvocation> selfPopulatingEntryFactory) {
        this.cache = cache;
        this.selfPopulatingEntryFactory = selfPopulatingEntryFactory;
        this.exceptionCache = null;
    }
    
    public SingletonCacheableCacheResolver(Ehcache cache, ThreadLocal<MethodInvocation> selfPopulatingEntryFactory, Ehcache exceptionCache) {
        this.cache = cache;
        this.selfPopulatingEntryFactory = selfPopulatingEntryFactory;
        this.exceptionCache = exceptionCache;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver#resolveCache(java.io.Serializable, org.aopalliance.intercept.MethodInvocation)
     */
    public Ehcache resolveCache(Serializable cacheKey, MethodInvocation invocation) {
        return this.cache;
    }
    
    public ThreadLocal<MethodInvocation> resolveSelfPopulatingCacheEntryFactory(Ehcache cache, Serializable cacheKey,
            MethodInvocation invocation) {
        return this.selfPopulatingEntryFactory;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver#resolveExceptionCache(java.io.Serializable, org.aopalliance.intercept.MethodInvocation, java.lang.Throwable)
     */
    public Ehcache resolveExceptionCache(Serializable cacheKey, MethodInvocation invocation, Throwable t) {
        return this.exceptionCache;
    }
}
