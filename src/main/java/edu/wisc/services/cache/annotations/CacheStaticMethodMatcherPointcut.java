/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.annotations;

import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import edu.wisc.services.cache.CacheableAttributeSource;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheStaticMethodMatcherPointcut extends StaticMethodMatcherPointcut {
    private CacheableAttributeSource cacheableAttributeSource;
    
    public void setCacheableAttributeSource(CacheableAttributeSource cacheableAttributeSource) {
        this.cacheableAttributeSource = cacheableAttributeSource;
    }

    /* (non-Javadoc)
     * @see org.springframework.aop.MethodMatcher#matches(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return this.cacheableAttributeSource.getCacheableAttribute(method, targetClass) != null;
    }
}
