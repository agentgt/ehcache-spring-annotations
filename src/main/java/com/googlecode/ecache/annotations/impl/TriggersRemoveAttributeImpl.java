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
package com.googlecode.ecache.annotations.impl;

import com.googlecode.ecache.annotations.AdviceType;
import com.googlecode.ecache.annotations.TriggersRemoveAttribute;
import com.googlecode.ecache.annotations.key.CacheKeyGenerator;

import net.sf.ehcache.Ehcache;

/**
 * Basic pojo style impl of {@link TriggersRemoveAttribute}
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version $Revision$
 */
class TriggersRemoveAttributeImpl implements TriggersRemoveAttribute {
	private final Ehcache cache;
	private final CacheKeyGenerator cacheKeyGenerator;
	private final boolean removeAll;
	
	TriggersRemoveAttributeImpl(Ehcache cache, CacheKeyGenerator cacheKeyGenerator, boolean removeAll) {
		this.cache = cache;
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.removeAll = removeAll;
	}
    
    public AdviceType getAdviceType() {
        return AdviceType.REMOVE;
    }

	/* (non-Javadoc)
	 * @see com.googlecode.ecache.annotations.FlushableAttribute#getCache()
	 */
	public Ehcache getCache() {
		return this.cache;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.ecache.annotations.FlushableAttribute#isRemoveAll()
	 */
	public boolean isRemoveAll() {
		return this.removeAll;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.ecache.annotations.FlushableAttribute#getCacheKeyGenerator()
	 */
	public CacheKeyGenerator getCacheKeyGenerator() {
		return this.cacheKeyGenerator;
	}

    @Override
    public String toString() {
        return "TriggersRemoveAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", removeAll=" + this.removeAll + "]";
    }
}
