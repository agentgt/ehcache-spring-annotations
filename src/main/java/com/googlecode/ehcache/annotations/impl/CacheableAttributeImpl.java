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
import com.googlecode.ehcache.annotations.CacheableInterceptor;
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
    private final boolean cacheNull;
    private final CacheableInterceptor cacheInterceptor;
    
    public CacheableAttributeImpl(
            CacheableCacheResolver cacheInstanceResolver,CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, 
            ParameterMask parameterMask, boolean cacheNull, CacheableInterceptor cacheInterceptor) {
        this.cacheInstanceResolver = cacheInstanceResolver;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.parameterMask = parameterMask;
        this.cacheNull = cacheNull;
        this.cacheInterceptor = cacheInterceptor;
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
    
    public boolean isCacheNull() {
        return this.cacheNull;
    }
    
    public ParameterMask getCacheKeyParameterMask() {
        return this.parameterMask;
    }

    public CacheableInterceptor getCacheInterceptor() {
		return this.cacheInterceptor;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cacheInstanceResolver == null) ? 0 : cacheInstanceResolver.hashCode());
        result = prime * result + ((cacheInterceptor == null) ? 0 : cacheInterceptor.hashCode());
        result = prime * result + ((cacheKeyGenerator == null) ? 0 : cacheKeyGenerator.hashCode());
        result = prime * result + (cacheNull ? 1231 : 1237);
        result = prime * result + ((parameterMask == null) ? 0 : parameterMask.hashCode());
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
        if (cacheInstanceResolver == null) {
            if (other.cacheInstanceResolver != null)
                return false;
        }
        else if (!cacheInstanceResolver.equals(other.cacheInstanceResolver))
            return false;
        if (cacheInterceptor == null) {
            if (other.cacheInterceptor != null)
                return false;
        }
        else if (!cacheInterceptor.equals(other.cacheInterceptor))
            return false;
        if (cacheKeyGenerator == null) {
            if (other.cacheKeyGenerator != null)
                return false;
        }
        else if (!cacheKeyGenerator.equals(other.cacheKeyGenerator))
            return false;
        if (cacheNull != other.cacheNull)
            return false;
        if (parameterMask == null) {
            if (other.parameterMask != null)
                return false;
        }
        else if (!parameterMask.equals(other.parameterMask))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CacheableAttributeImpl [cacheInstanceResolver=" + cacheInstanceResolver + ", cacheKeyGenerator="
                + cacheKeyGenerator + ", parameterMask=" + parameterMask + ", cacheNull=" + cacheNull
                + ", cacheInterceptor=" + cacheInterceptor + "]";
    }
}
