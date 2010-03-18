/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.interceptor.caching;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.CacheableAttributeSource;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * TODO object versus serializable caching
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CachingInterceptor implements MethodInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private CacheableAttributeSource cacheableAttributeSource;
    

    public void setCacheableAttributeSource(CacheableAttributeSource cacheableAttributeSource) {
        this.cacheableAttributeSource = cacheableAttributeSource;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final CacheableAttribute cacheableAttribute = this.cacheableAttributeSource.getCacheableAttribute(methodInvocation.getMethod(), methodInvocation.getClass());
        if (cacheableAttribute == null) {
            this.logger.trace("Don't need to cache [{}]: This method isn't cacheable.", methodInvocation);
            return methodInvocation.proceed();
        }
        
        //Generate the cache key
        final CacheKeyGenerator cacheKeyGenerator = cacheableAttribute.getCacheKeyGenerator();
        final Serializable key = cacheKeyGenerator.generateKey(methodInvocation);

        //See if there is a cached result
        final Ehcache cache = cacheableAttribute.getCache();
        final Element element = cache.get(key);
        if (element != null) {
            return element.getObjectValue();
        }

        //Determine if exception caching is enabled
        final Ehcache exceptionCache = cacheableAttribute.getExceptionCache();
        if (exceptionCache != null) {
            //See if there is a cached exception
            final Element execptionElement = exceptionCache.get(key);
            if (execptionElement != null) {
                throw (Throwable)execptionElement.getObjectValue();
            }
        }

        //No cached value or exception, proceed
        final Object value;
        try {
            value = methodInvocation.proceed();
        }
        catch (Throwable t) {
            //If exception caching, cache the exception
            if (exceptionCache != null) {
                exceptionCache.put(new Element(key, t));
            }
            
            throw t;
        }
        
        //Cache and return the value
        cache.put(new Element(key, value));
        return value;
    }
}
