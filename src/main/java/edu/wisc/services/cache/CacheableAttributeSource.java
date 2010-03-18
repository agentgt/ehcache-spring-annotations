/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import java.lang.reflect.Method;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableAttributeSource {
    public CacheableAttribute getCachableAttribute(Method method, Class<?> targetClass);
}
