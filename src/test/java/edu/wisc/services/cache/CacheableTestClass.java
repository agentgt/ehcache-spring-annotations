/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheableTestClass implements CacheableTestInterface {
    private int callCount = 0;
    
    public int getCallCount() {
        return callCount;
    }
    
    public String getCachedResource(String argument) {
        callCount++;
        return "resource-" + argument;
    }
}
