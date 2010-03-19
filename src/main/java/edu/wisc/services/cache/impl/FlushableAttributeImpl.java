/**
 * 
 */
package edu.wisc.services.cache.impl;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.FlushableAttribute;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
class FlushableAttributeImpl implements FlushableAttribute {

	private final Ehcache cache;
	private final CacheKeyGenerator cacheKeyGenerator;
	private final boolean removeAll;
	
	FlushableAttributeImpl(Ehcache cache, CacheKeyGenerator cacheKeyGenerator, boolean removeAll) {
		this.cache = cache;
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.removeAll = removeAll;
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

}
