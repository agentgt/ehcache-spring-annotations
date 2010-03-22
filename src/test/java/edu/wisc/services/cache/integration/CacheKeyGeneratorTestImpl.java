/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.integration;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheKeyGeneratorTestImpl implements CacheKeyGeneratorTestInterface {
    private int singleCachedCall = 0;
    
    /* (non-Javadoc)
     * @see edu.wisc.services.cache.integration.CacheConfigurationTestInterface#singleCachedCall(java.lang.String)
     */
    @Override
    public String singleCachedCall(String argument) {
        this.singleCachedCall++;
        return "singleCachedCall(" + argument + ")";
    }

    /* (non-Javadoc)
     * @see edu.wisc.services.cache.integration.CacheConfigurationTestInterface#singleCachedCallCount()
     */
    @Override
    public int singleCachedCallCount() {
        return this.singleCachedCall;
    }

}
