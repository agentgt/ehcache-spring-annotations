/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import java.lang.reflect.Method;

/**
 * Provides {@link edu.wisc.services.cache.annotations.Cacheable} information about a
 * method. 
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableAttributeSource {
    /**
     * Gets the runtime caching information for a method. If the method is not eligible for
     * caching null is returned. 
     * 
     * @param method The method to inspect
     * @param targetClass The class the method exists on
     * @return Runtime caching configuration for the method, null if the method is not cachable.
     */
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass);
}
