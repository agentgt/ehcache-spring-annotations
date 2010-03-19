/**
 * 
 */
package edu.wisc.services.cache.impl;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.FlushableAttribute;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
class FlushableAttributeImpl implements FlushableAttribute {

	private final Ehcache cache;
	private final boolean removeAll;
	
	FlushableAttributeImpl(Ehcache cache, boolean removeAll) {
		this.cache = cache;
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

}
