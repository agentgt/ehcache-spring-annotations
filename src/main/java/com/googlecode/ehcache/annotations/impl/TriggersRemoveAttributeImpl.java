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

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

import net.sf.ehcache.Ehcache;

/**
 * Basic pojo style impl of {@link TriggersRemoveAttribute}
 * 
 * @author Nicholas Blair
 * @version $Revision$
 */
class TriggersRemoveAttributeImpl implements TriggersRemoveAttribute {
	private final Ehcache cache;
	private final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
	private final boolean removeAll;
	private final When when;
	
	TriggersRemoveAttributeImpl(Ehcache cache, CacheKeyGenerator<? extends Serializable> cacheKeyGenerator, boolean removeAll, When when) {
		this.cache = cache;
		this.cacheKeyGenerator = cacheKeyGenerator;
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
	public When when() {
		return this.when;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.ehcache.annotations.FlushableAttribute#getCacheKeyGenerator()
	 */
	public CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator() {
		return this.cacheKeyGenerator;
	}

    @Override
    public String toString() {
        return "TriggersRemoveAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", removeAll=" + this.removeAll + "]";
    }
}
