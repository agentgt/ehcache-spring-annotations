/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.annotations.TriggersRemove;

/**
 * Possible types of cache related advice
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public enum AdviceType {
    /**
     * No advice
     */
    NONE,
    /**
     * {@link Cacheable} advice
     */
    CACHE,
    /**
     * {@link TriggersRemove} advice
     */
    REMOVE;
}
