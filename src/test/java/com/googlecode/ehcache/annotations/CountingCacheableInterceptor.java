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
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CountingCacheableInterceptor extends DefaultCacheableInterceptor {
    private int preInvCache = 0;
    private int preInvCacheEx = 0;
    private int postInvCache = 0;
    private int postInvCacheEx = 0;

    @Override
    public boolean preInvokeCachable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value) {
        preInvCache++;
        return super.preInvokeCachable(cache, methodInvocation, key, value);
    }

    @Override
    public boolean postInvokeCacheable(Ehcache cache, MethodInvocation methodInvocation, Serializable key, Object value) {
        postInvCache++;
        return super.postInvokeCacheable(cache, methodInvocation, key, value);
    }

    @Override
    public boolean preInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation,
            Serializable key, Throwable t) {
        preInvCacheEx++;
        return super.preInvokeCacheableException(exceptionCache, methodInvocation, key, t);
    }

    @Override
    public boolean postInvokeCacheableException(Ehcache exceptionCache, MethodInvocation methodInvocation,
            Serializable key, Throwable t) {
        postInvCacheEx++;
        return super.postInvokeCacheableException(exceptionCache, methodInvocation, key, t);
    }
    
    public void resetCounters() {
        this.preInvCache = 0;
        this.preInvCacheEx = 0;
        this.postInvCache = 0;
        this.postInvCacheEx = 0;
    }

    public int getPreInvCache() {
        return this.preInvCache;
    }

    public int getPreInvCacheEx() {
        return this.preInvCacheEx;
    }

    public int getPostInvCache() {
        return this.postInvCache;
    }

    public int getPostInvCacheEx() {
        return this.postInvCacheEx;
    }

    @Override
    public String toString() {
        return "CountingCacheableInterceptor [preInvCache=" + this.preInvCache + ", preInvCacheEx="
                + this.preInvCacheEx + ", postInvCache=" + this.postInvCache + ", postInvCacheEx="
                + this.postInvCacheEx + "]";
    }
}
