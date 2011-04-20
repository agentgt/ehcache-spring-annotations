/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.googlecode.ehcache.annotations;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Used by {@link Cacheable} to allow logic to be injected into the caching API.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableInterceptor {
    /**
     * Called after a cache hit but before the method invocation.
     * 
     * <b>Not used when {@link Cacheable#selfPopulating()} is set to true.</b>
     * 
     * @param cache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @param key The generated cache key
     * @param value The found cache value if any, may be null
     * @return true if the intercepted method invocation should be invoked, false if it should not be invoked and the value should be returned.
     */
    public boolean preInvokeCachable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value);
    
    /**
     * Called after a method invocation but before the returned value is cached
     * 
     * <b>Not used when {@link Cacheable#selfPopulating()} is set to true.</b>
     * 
     * @param cache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @param key The generated cache key
     * @param value The value returned by the invocation
     * @return true if the value should be cached, false if not
     */
    public boolean postInvokeCacheable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value);
    
    /**
     * Called after the cache lookup but before the method invocation if a cached exception was found
     * 
     * @param exceptionCache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @param key The generated cache key
     * @param t The exception thrown by the invocation
     * @return true if the intercepted method invocation should be invoked, false if it should not be invoked and the cached exception thrown.
     */
    public boolean preInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation, Serializable key, Throwable t);
    
    
    /**
     * Called after a method invocation throws an exception AND an exception cache is configured for the
     * interceptor but before the exception is cached
     * 
     * @param exceptionCache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @param key The generated cache key
     * @param t The exception thrown by the invocation
     * @return true if the value should be cached, false if not
     */
    public boolean postInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation, Serializable key, Throwable t);
}
