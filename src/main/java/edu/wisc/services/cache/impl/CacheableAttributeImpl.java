/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.impl;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
class CacheableAttributeImpl implements CacheableAttribute {
    private final Ehcache cache;
    private final Ehcache exceptionCache;
    private final CacheKeyGenerator cacheKeyGenerator;
    
    public CacheableAttributeImpl(Ehcache cache, Ehcache exceptionCache, CacheKeyGenerator cacheKeyGenerator) {
        this.cache = cache;
        this.exceptionCache = exceptionCache;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Override
    public Ehcache getCache() {
        return this.cache;
    }

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    @Override
    public Ehcache getExceptionCache() {
        return this.exceptionCache;
    }

    @Override
    public String toString() {
        return "CacheableAttributeImpl [cache=" + cache + ", cacheKeyGenerator=" + cacheKeyGenerator + ", exceptionCache=" + exceptionCache + "]";
    }
}
