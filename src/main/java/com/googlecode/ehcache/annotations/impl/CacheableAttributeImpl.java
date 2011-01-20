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

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.CacheableAttribute;
import com.googlecode.ehcache.annotations.ParameterMask;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver;


/**
 * Basic pojo style impl of {@link CacheableAttribute}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
class CacheableAttributeImpl implements CacheableAttribute {
    private final CacheableCacheResolver cacheInstanceResolver;
    private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
    private final ParameterMask parameterMask;
    
    public CacheableAttributeImpl(CacheableCacheResolver cacheInstanceResolver, 
            CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, ParameterMask parameterMask) {
        this.cacheInstanceResolver = cacheInstanceResolver;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.parameterMask = parameterMask;
    }
    
    public AdviceType getAdviceType() {
        return AdviceType.CACHE;
    }
    
    public CacheableCacheResolver getCacheResolver() {
        return this.cacheInstanceResolver;
    }

    public CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }
    
    public ParameterMask getCacheKeyParameterMask() {
        return this.parameterMask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.cacheInstanceResolver == null) ? 0 : this.cacheInstanceResolver.hashCode());
        result = prime * result + ((this.cacheKeyGenerator == null) ? 0 : this.cacheKeyGenerator.hashCode());
        result = prime * result + ((this.parameterMask == null) ? 0 : this.parameterMask.hashCode());
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
        CacheableAttributeImpl other = (CacheableAttributeImpl) obj;
        if (this.cacheInstanceResolver == null) {
            if (other.cacheInstanceResolver != null)
                return false;
        }
        else if (!this.cacheInstanceResolver.equals(other.cacheInstanceResolver))
            return false;
        if (this.cacheKeyGenerator == null) {
            if (other.cacheKeyGenerator != null)
                return false;
        }
        else if (!this.cacheKeyGenerator.equals(other.cacheKeyGenerator))
            return false;
        if (this.parameterMask == null) {
            if (other.parameterMask != null)
                return false;
        }
        else if (!this.parameterMask.equals(other.parameterMask))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CacheableAttributeImpl [cacheInstanceResolver=" + this.cacheInstanceResolver + ", cacheKeyGenerator="
                + this.cacheKeyGenerator + ", parameterMask=" + this.parameterMask + "]";
    }
}
