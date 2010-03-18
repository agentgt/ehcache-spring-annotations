/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.impl;

import edu.wisc.services.cache.CacheableAttribute;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
class CacheableAttributeImpl implements CacheableAttribute {
    private final String cacheName;
    private final boolean blocking;
    private final String keyGeneratorName;

    public CacheableAttributeImpl(String cacheName, boolean blocking, String keyGeneratorName) {
        this.cacheName = cacheName;
        this.blocking = blocking;
        this.keyGeneratorName = keyGeneratorName;
    }

    
    /* (non-Javadoc)
     * @see org.jasig.spring.cache.CachableAttribute#getCacheName()
     */
    @Override
    public String getCacheName() {
        return cacheName;
    }

    /* (non-Javadoc)
     * @see org.jasig.spring.cache.CachableAttribute#isBlocking()
     */
    @Override
    public boolean isBlocking() {
        return blocking;
    }


	@Override
    public String toString() {
        return "CachableAttributeImpl [cacheName=" + cacheName + ", blocking=" + blocking + ",cacheKeyGenerator=" + keyGeneratorName + "]";
    }


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (blocking ? 1231 : 1237);
		result = prime
				* result
				+ ((keyGeneratorName == null) ? 0 : keyGeneratorName
						.hashCode());
		result = prime * result
				+ ((cacheName == null) ? 0 : cacheName.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheableAttributeImpl other = (CacheableAttributeImpl) obj;
		if (blocking != other.blocking)
			return false;
		if (keyGeneratorName == null) {
			if (other.keyGeneratorName != null)
				return false;
		} else if (!keyGeneratorName.equals(other.keyGeneratorName))
			return false;
		if (cacheName == null) {
			if (other.cacheName != null)
				return false;
		} else if (!cacheName.equals(other.cacheName))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.services.cache.CacheableAttribute#getKeyGeneratorName()
	 */
	@Override
	public String getKeyGeneratorName() {
		return this.keyGeneratorName;
	}

    
}
