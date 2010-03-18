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
public interface CacheableAttribute {
    public String getCacheName();
    
    public boolean isBlocking();
    
    public String getKeyGeneratorName();
}
