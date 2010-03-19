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

package edu.wisc.services.cache;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Represents the objects needed to intercept calls to methods annotated
 * with {@link edu.wisc.services.cache.annotations.Cacheable}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableAttribute extends MethodAttribute {
    /**
     * @return The cache to use in case of an exception, may be null if no exception caching should be done.
     */
    public Ehcache getExceptionCache();
    
    /**
     * @return The ThreadLocal to use with a self populating cache. If not null the intercepter must provide a {@link Callable} before calling {@link Ehcache#get(Object)}
     */
    public ThreadLocal<MethodInvocation> getEntryFactory();
}
