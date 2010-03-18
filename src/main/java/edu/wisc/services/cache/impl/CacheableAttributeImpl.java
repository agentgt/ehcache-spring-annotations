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

    public CacheableAttributeImpl(String cacheName, boolean blocking) {
        this.cacheName = cacheName;
        this.blocking = blocking;
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
        return "CachableAttributeImpl [cacheName=" + cacheName + ", blocking=" + blocking + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (blocking ? 1231 : 1237);
        result = prime * result + ((cacheName == null) ? 0 : cacheName.hashCode());
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
        return true;
    }
}
