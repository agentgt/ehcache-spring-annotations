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

/**
 * 
 */
package com.googlecode.ehcache.annotations.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import net.sf.ehcache.Ehcache;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

/**
 * Basic pojo style impl of {@link TriggersRemoveAttribute}
 * 
 * @author Nicholas Blair
 * @version $Revision$
 */
class TriggersRemoveAttributeImpl implements TriggersRemoveAttribute {
    private final List<Ehcache> caches;
    private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
    private final ParameterMask parameterMask;
    private final boolean removeAll;
    private final When when;
    
    TriggersRemoveAttributeImpl(List<Ehcache> caches, 
            CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, ParameterMask parameterMask,
            boolean removeAll, When when) {
        this.caches = Collections.unmodifiableList(caches);
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.parameterMask = parameterMask;
        this.removeAll = removeAll;
        this.when = when;
    }
    
    public AdviceType getAdviceType() {
        return AdviceType.REMOVE;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.TriggersRemoveAttribute#getCache()
     */
    public Iterable<Ehcache> getCaches() {
        return this.caches;
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
        result = prime * result + ((this.caches == null) ? 0 : this.caches.hashCode());
        result = prime * result + ((this.parameterMask == null) ? 0 : this.parameterMask.hashCode());
        result = prime * result + (this.removeAll ? 1231 : 1237);
        result = prime * result + ((this.when == null) ? 0 : this.when.hashCode());
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
        TriggersRemoveAttributeImpl other = (TriggersRemoveAttributeImpl) obj;
        if (this.cacheKeyGenerator == null) {
            if (other.cacheKeyGenerator != null) {
                return false;
            }
        }
        else if (!this.cacheKeyGenerator.equals(other.cacheKeyGenerator)) {
            return false;
        }
        if (this.caches == null) {
            if (other.caches != null) {
                return false;
            }
        }
        else if (!this.caches.equals(other.caches)) {
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
        if (this.removeAll != other.removeAll) {
            return false;
        }
        if (this.when == null) {
            if (other.when != null) {
                return false;
            }
        }
        else if (!this.when.equals(other.when)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TriggersRemoveAttributeImpl [cacheKeyGenerator=" + this.cacheKeyGenerator + ", caches=" + this.caches
                + ", parameterMask=" + this.parameterMask + ", removeAll=" + this.removeAll + ", when=" + this.when
                + "]";
    }
}
