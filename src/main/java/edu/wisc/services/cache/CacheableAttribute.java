/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import edu.wisc.services.cache.key.CacheKeyGenerator;
import net.sf.ehcache.Ehcache;

/**
 * Represents the objects needed to intercept calls to methods annontated
 * with {@link edu.wisc.services.cache.annotations.Cacheable}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableAttribute {
    /**
     * @return The cache to use, will never be null.
     */
    public Ehcache getCache();
    
    /**
     * @return The key generator to use, will never be null.
     */
    public CacheKeyGenerator getCacheKeyGenerator();
    
    /**
     * @return The cache to use in case of an exception, may be null if no exception caching should be done.
     */
    public Ehcache getExceptionCache();
}
