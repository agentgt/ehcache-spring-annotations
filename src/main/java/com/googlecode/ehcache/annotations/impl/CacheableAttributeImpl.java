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

package com.googlecode.ehcache.annotations.impl;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.CacheableAttribute;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;


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
    private final ParameterMask parameterMask;
    private final ThreadLocal<MethodInvocation> entryFactory;
    
    public CacheableAttributeImpl(Ehcache cache, Ehcache exceptionCache, 
            CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, ParameterMask parameterMask, 
            ThreadLocal<MethodInvocation> entryFactory) {
        this.cache = cache;
        this.exceptionCache = exceptionCache;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.parameterMask = parameterMask;
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
    
    public ParameterMask getCacheKeyParameterMask() {
        return this.parameterMask;
    }

    public Ehcache getExceptionCache() {
        return this.exceptionCache;
    }
    
    public ThreadLocal<MethodInvocation> getEntryFactory() {
        return this.entryFactory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.cache == null) ? 0 : this.cache.hashCode());
        result = prime * result + ((this.cacheKeyGenerator == null) ? 0 : this.cacheKeyGenerator.hashCode());
        result = prime * result + ((this.entryFactory == null) ? 0 : this.entryFactory.hashCode());
        result = prime * result + ((this.exceptionCache == null) ? 0 : this.exceptionCache.hashCode());
        result = prime * result + ((this.parameterMask == null) ? 0 : this.parameterMask.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CacheableAttributeImpl other = (CacheableAttributeImpl) obj;
        if (this.cache == null) {
            if (other.cache != null) {
                return false;
            }
        }
        else if (!this.cache.equals(other.cache)) {
            return false;
        }
        if (this.cacheKeyGenerator == null) {
            if (other.cacheKeyGenerator != null) {
                return false;
            }
        }
        else if (!this.cacheKeyGenerator.equals(other.cacheKeyGenerator)) {
            return false;
        }
        if (this.entryFactory == null) {
            if (other.entryFactory != null) {
                return false;
            }
        }
        else if (!this.entryFactory.equals(other.entryFactory)) {
            return false;
        }
        if (this.exceptionCache == null) {
            if (other.exceptionCache != null) {
                return false;
            }
        }
        else if (!this.exceptionCache.equals(other.exceptionCache)) {
            return false;
        }
        if (this.parameterMask == null) {
            if (other.parameterMask != null) {
                return false;
            }
        }
        else if (!this.parameterMask.equals(other.parameterMask)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CacheableAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", entryFactory=" + this.entryFactory + ", exceptionCache=" + this.exceptionCache
                + ", parameterMask=" + this.parameterMask + "]";
    }
}
