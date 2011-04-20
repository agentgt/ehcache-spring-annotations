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
 * Default interceptor that always returns cached values, throws cached exceptions and always caches values and
 * exceptions.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class DefaultCacheableInterceptor implements CacheableInterceptor {
    public static final DefaultCacheableInterceptor INSTANCE = new DefaultCacheableInterceptor();

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheInterceptor#preInvokeCachable(net.sf.ehcache.Ehcache, org.aopalliance.intercept.MethodInvocation, java.io.Serializable, java.lang.Object)
     */
    public boolean preInvokeCachable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheInterceptor#postInvokeCacheable(net.sf.ehcache.Ehcache, org.aopalliance.intercept.MethodInvocation, java.io.Serializable, java.lang.Object)
     */
    public boolean postInvokeCacheable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheInterceptor#preInvokeCacheableException(net.sf.ehcache.Ehcache, org.aopalliance.intercept.MethodInvocation, java.io.Serializable, java.lang.Throwable)
     */
    public boolean preInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation, Serializable key, Throwable t) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheInterceptor#postInvokeCacheableException(net.sf.ehcache.Ehcache, org.aopalliance.intercept.MethodInvocation, java.io.Serializable, java.lang.Throwable)
     */
    public boolean postInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation, Serializable key, Throwable t) {
        return true;
    }
}
