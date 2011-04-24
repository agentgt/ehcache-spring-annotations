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
/**
 * 
 */
package com.googlecode.ehcache.annotations.impl;

import java.io.Serializable;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.ParameterMask;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.TriggersRemoveInterceptor;
import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.resolver.TriggersRemoveCacheResolver;

/**
 * Basic pojo style impl of {@link TriggersRemoveAttribute}
 * 
 * @author Nicholas Blair
 * @version $Revision$
 */
class TriggersRemoveAttributeImpl implements TriggersRemoveAttribute {
    private final TriggersRemoveCacheResolver cacheResolver;
    private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
    private final TriggersRemoveInterceptor triggersRemoveInterceptor;
    private final ParameterMask parameterMask;
    private final boolean removeAll;
    private final When when;
    
    TriggersRemoveAttributeImpl(
            TriggersRemoveCacheResolver cacheResolver, CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, 
            ParameterMask parameterMask, TriggersRemoveInterceptor triggersRemoveInterceptor,
            boolean removeAll, When when) {
        this.cacheResolver = cacheResolver;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.triggersRemoveInterceptor = triggersRemoveInterceptor;
        this.parameterMask = parameterMask;
        this.removeAll = removeAll;
        this.when = when;
    }
    
    public AdviceType getAdviceType() {
        return AdviceType.REMOVE;
    }
    
    public TriggersRemoveCacheResolver getCacheResolver() {
        return this.cacheResolver;
    }
    
    public TriggersRemoveInterceptor getTriggersRemoveInterceptor() {
        return this.triggersRemoveInterceptor;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.FlushableAttribute#isRemoveAll()
     */
    public boolean isRemoveAll() {
        return this.removeAll;
    }
    
    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.TriggersRemoveAttribute#when()
     */
    public When getWhen() {
        return this.when;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.FlushableAttribute#getCacheKeyGenerator()
     */
    public CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.MethodAttribute#getPartialCacheKeyParameterIndicies()
     */
    public ParameterMask getCacheKeyParameterMask() {
        return this.parameterMask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.cacheKeyGenerator == null) ? 0 : this.cacheKeyGenerator.hashCode());
        result = prime * result + ((this.cacheResolver == null) ? 0 : this.cacheResolver.hashCode());
        result = prime * result + ((this.parameterMask == null) ? 0 : this.parameterMask.hashCode());
        result = prime * result + (this.removeAll ? 1231 : 1237);
        result = prime * result
                + ((this.triggersRemoveInterceptor == null) ? 0 : this.triggersRemoveInterceptor.hashCode());
        result = prime * result + ((this.when == null) ? 0 : this.when.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TriggersRemoveAttributeImpl other = (TriggersRemoveAttributeImpl) obj;
        if (this.cacheKeyGenerator == null) {
            if (other.cacheKeyGenerator != null)
                return false;
        }
        else if (!this.cacheKeyGenerator.equals(other.cacheKeyGenerator))
            return false;
        if (this.cacheResolver == null) {
            if (other.cacheResolver != null)
                return false;
        }
        else if (!this.cacheResolver.equals(other.cacheResolver))
            return false;
        if (this.parameterMask == null) {
            if (other.parameterMask != null)
                return false;
        }
        else if (!this.parameterMask.equals(other.parameterMask))
            return false;
        if (this.removeAll != other.removeAll)
            return false;
        if (this.triggersRemoveInterceptor == null) {
            if (other.triggersRemoveInterceptor != null)
                return false;
        }
        else if (!this.triggersRemoveInterceptor.equals(other.triggersRemoveInterceptor))
            return false;
        if (this.when != other.when)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TriggersRemoveAttributeImpl [cacheResolver=" + this.cacheResolver + ", cacheKeyGenerator="
                + this.cacheKeyGenerator + ", triggersRemoveInterceptor=" + this.triggersRemoveInterceptor
                + ", parameterMask=" + this.parameterMask + ", removeAll=" + this.removeAll + ", when=" + this.when
                + "]";
    }
}
