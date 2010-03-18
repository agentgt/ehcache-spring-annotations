/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.impl;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.annotations.Cacheable;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
class CacheableAttributeImpl implements CacheableAttribute {
    private final String cacheName;
    private final boolean blocking;
    private final String keyGeneratorName;
    private final String exceptionCacheName;

    public CacheableAttributeImpl(Cacheable cacheable) {
        this.cacheName = cacheable.cacheName();
        this.blocking = cacheable.blocking();
        this.keyGeneratorName = cacheable.keyGeneratorName();
        final String exceptionCacheName2 = cacheable.exceptionCacheName().trim();
        this.exceptionCacheName = exceptionCacheName2.length() > 0 ? exceptionCacheName2 : null;
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

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.CacheableAttribute#getKeyGeneratorName()
	 */
	@Override
	public String getKeyGeneratorName() {
		return this.keyGeneratorName;
	}
	
    /* (non-Javadoc)
     * @see edu.wisc.services.cache.CacheableAttribute#getExceptionCacheName()
     */
    @Override
    public String getExceptionCacheName() {
        return exceptionCacheName;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (blocking ? 1231 : 1237);
        result = prime * result + ((cacheName == null) ? 0 : cacheName.hashCode());
        result = prime * result + ((exceptionCacheName == null) ? 0 : exceptionCacheName.hashCode());
        result = prime * result + ((keyGeneratorName == null) ? 0 : keyGeneratorName.hashCode());
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
        if (!(obj instanceof CacheableAttributeImpl)) {
            return false;
        }
        CacheableAttributeImpl other = (CacheableAttributeImpl) obj;
        if (blocking != other.blocking) {
            return false;
        }
        if (cacheName == null) {
            if (other.cacheName != null) {
                return false;
            }
        }
        else if (!cacheName.equals(other.cacheName)) {
            return false;
        }
        if (exceptionCacheName == null) {
            if (other.exceptionCacheName != null) {
                return false;
            }
        }
        else if (!exceptionCacheName.equals(other.exceptionCacheName)) {
            return false;
        }
        if (keyGeneratorName == null) {
            if (other.keyGeneratorName != null) {
                return false;
            }
        }
        else if (!keyGeneratorName.equals(other.keyGeneratorName)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "CacheableAttributeImpl [cacheName=" + cacheName + ", exceptionCacheName=" + exceptionCacheName
                + ", keyGeneratorName=" + keyGeneratorName + ", blocking=" + blocking + "]";
    }
}
