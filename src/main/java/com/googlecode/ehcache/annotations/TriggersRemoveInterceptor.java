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
 * Used by {@link TriggersRemove} to allow logic to be injected into the triggers remove API.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface TriggersRemoveInterceptor {

    /**
     * Called before the element is removed from the cache
     * 
     * @param cache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @param key The generated cache key
     * @return true if the element should be removed, false if the remove should be skipped.
     */
    public boolean preInvokeTriggersRemove(Ehcache cache, MethodInvocation methodInvocation, Serializable key);
    
    /**
     * Called before the elements are removed from the cache
     * 
     * @param cache The cache used for this invocation
     * @param methodInvocation The method invocation that has been intercepted
     * @return true if the elements should be removed, false if the remove should be skipped.
     */
    public boolean preInvokeTriggersRemoveAll(Ehcache cache, MethodInvocation methodInvocation);
}
