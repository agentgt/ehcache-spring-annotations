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
