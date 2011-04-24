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
package com.googlecode.ehcache.annotations.integration.resolver;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.ObjectExistsException;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.resolver.CacheResolverFactory;
import com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver;
import com.googlecode.ehcache.annotations.resolver.SingletonCacheableCacheResolver;
import com.googlecode.ehcache.annotations.resolver.TriggersRemoveCacheResolver;

/**
 * Used in testing
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
class LoggingCacheResolverFactory implements CacheResolverFactory {
    private final Set<Serializable> resolvedKeys = new LinkedHashSet<Serializable>();
    
    private CacheManager cacheManager;

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public Set<Serializable> getResolvedKeys() {
        return this.resolvedKeys;
    }

    public CacheableCacheResolver getCacheResolver(Cacheable cacheable, Method method) {
        final Ehcache cache = this.getCache(cacheable.cacheName());
        final String exceptionCacheName = cacheable.exceptionCacheName();
        if (StringUtils.hasLength(exceptionCacheName)) {
            final Ehcache exceptionCache = this.getCache(exceptionCacheName);
            return new TestCacheableCacheResolver(cache, exceptionCache);
        }
        
        return new TestCacheableCacheResolver(cache);
    }

    public TriggersRemoveCacheResolver getCacheResolver(TriggersRemove triggersRemove, Method method) {
        throw new UnsupportedOperationException();
    }

    protected Ehcache getCache(String cacheName) {
        Ehcache cache = this.cacheManager.getEhcache(cacheName);
        if (cache == null) {
            try {
                cacheManager.addCache(cacheName);
            }
            catch (ObjectExistsException oee) {
                //ignore
            }
            cache = cacheManager.getEhcache(cacheName);
        }
        return cache;
    }
    
    private final class TestCacheableCacheResolver extends SingletonCacheableCacheResolver {
        public TestCacheableCacheResolver(Ehcache cache, Ehcache exceptionCache) {
            super(cache, null, exceptionCache);
        }

        public TestCacheableCacheResolver(Ehcache cache) {
            super(cache, null);
        }

        @Override
        public Ehcache resolveCache(Serializable cacheKey, MethodInvocation invocation) {
            resolvedKeys.add(cacheKey);
            return super.resolveCache(cacheKey, invocation);
        }

        @Override
        public Ehcache resolveExceptionCache(Serializable cacheKey, MethodInvocation invocation, Throwable t) {
            resolvedKeys.add(cacheKey);
            return super.resolveExceptionCache(cacheKey, invocation, t);
        }
    }
}
