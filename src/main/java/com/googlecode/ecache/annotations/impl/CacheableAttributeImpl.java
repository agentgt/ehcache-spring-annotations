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

package com.googlecode.ecache.annotations.impl;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

import com.googlecode.ecache.annotations.AdviceType;
import com.googlecode.ecache.annotations.CacheableAttribute;
import com.googlecode.ecache.annotations.key.CacheKeyGenerator;


/**
 * Basic pojo style impl of {@link CacheableAttribute}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
class CacheableAttributeImpl implements CacheableAttribute {
    private final Ehcache cache;
    private final Ehcache exceptionCache;
    private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
    private final ThreadLocal<MethodInvocation> entryFactory;
    
    public CacheableAttributeImpl(Ehcache cache, Ehcache exceptionCache, CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, ThreadLocal<MethodInvocation> entryFactory) {
        this.cache = cache;
        this.exceptionCache = exceptionCache;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.entryFactory = entryFactory;
    }
    
    public AdviceType getAdviceType() {
        return AdviceType.CACHE;
    }

    public Ehcache getCache() {
        return this.cache;
    }

    public CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    public Ehcache getExceptionCache() {
        return this.exceptionCache;
    }
    
    public ThreadLocal<MethodInvocation> getEntryFactory() {
        return this.entryFactory;
    }

    @Override
    public String toString() {
        return "CacheableAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", entryFactory=" + this.entryFactory + ", exceptionCache=" + this.exceptionCache + "]";
    }
}
