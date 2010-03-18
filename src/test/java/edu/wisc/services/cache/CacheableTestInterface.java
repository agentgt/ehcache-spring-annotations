/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.Cacheable;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableTestInterface {

    public int getCallCount();
    
    @Cacheable(cacheName="cachedResource")
    public String getCachedResource(String argument);
    
    @Cacheable(cacheName="cachedResource")
    public String getCachedSingleResource();
    
    @Cacheable(cacheName="cachedResourceTwo")
    public String getCachedResourceTwo(String argument);
}