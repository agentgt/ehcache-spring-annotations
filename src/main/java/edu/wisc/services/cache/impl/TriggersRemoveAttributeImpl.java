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
package edu.wisc.services.cache.impl;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.AdviceType;
import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.key.CacheKeyGenerator;

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
    
    @Override
    public AdviceType getAdviceType() {
        return AdviceType.REMOVE;
    }

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableAttribute#getCache()
	 */
	@Override
	public Ehcache getCache() {
		return this.cache;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableAttribute#isRemoveAll()
	 */
	@Override
	public boolean isRemoveAll() {
		return this.removeAll;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableAttribute#getCacheKeyGenerator()
	 */
	@Override
	public CacheKeyGenerator getCacheKeyGenerator() {
		return this.cacheKeyGenerator;
	}

    @Override
    public String toString() {
        return "TriggersRemoveAttributeImpl [cache=" + this.cache + ", cacheKeyGenerator=" + this.cacheKeyGenerator
                + ", removeAll=" + this.removeAll + "]";
    }
}
