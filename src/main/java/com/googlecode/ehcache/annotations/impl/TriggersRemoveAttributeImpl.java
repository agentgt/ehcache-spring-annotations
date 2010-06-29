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
import java.util.Set;

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
	private final Ehcache cache;
	private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
	private final Set<Integer> partialCacheKeyParameterIndicies;
    private final boolean removeAll;
	private final When when;
	
	TriggersRemoveAttributeImpl(Ehcache cache, 
	        CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, Set<Integer> partialCacheKeyParameterIndicies,
	        boolean removeAll, When when) {
		this.cache = cache;
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.partialCacheKeyParameterIndicies = Collections.unmodifiableSet(partialCacheKeyParameterIndicies);
        this.removeAll = removeAll;
		this.when = when;
	}
    
    public AdviceType getAdviceType() {
        return AdviceType.REMOVE;
    }

	/* (non-Javadoc)
	 * @see com.googlecode.ehcache.annotations.FlushableAttribute#getCache()
	 */
	public Ehcache getCache() {
		return this.cache;
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
    public Set<Integer> getPartialCacheKeyParameterIndicies() {
        return this.partialCacheKeyParameterIndicies;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.cache == null) ? 0 : this.cache.hashCode());
        result = prime * result + ((this.cacheKeyGenerator == null) ? 0 : this.cacheKeyGenerator.hashCode());
        result = prime
                * result
                + ((this.partialCacheKeyParameterIndicies == null) ? 0 : this.partialCacheKeyParameterIndicies
                        .hashCode());
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
        if (this.partialCacheKeyParameterIndicies == null) {
            if (other.partialCacheKeyParameterIndicies != null) {
                return false;
            }
        }
        else if (!this.partialCacheKeyParameterIndicies.equals(other.partialCacheKeyParameterIndicies)) {
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
        return "TriggersRemoveAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", partialCacheKeyParameterIndicies=" + this.partialCacheKeyParameterIndicies + ", removeAll="
                + this.removeAll + ", when=" + this.when + "]";
    }
}
