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