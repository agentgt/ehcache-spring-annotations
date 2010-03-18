/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.interceptor.caching;

import java.io.Serializable;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.ObjectExistsException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.CacheableAttributeSource;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * TODO object versus serializable caching
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CachingInterceptor implements MethodInterceptor, BeanFactoryAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private CacheManager cacheManager;
    
    private BeanFactory beanFactory;
    private String cacheManagerBeanName;
    private CacheableAttributeSource cacheableAttributeSource;
    private boolean createCaches = false;
    

    public void setCacheManagerBeanName(String cacheManagerBeanName) {
        this.cacheManagerBeanName = cacheManagerBeanName;
    }

    public void setCacheableAttributeSource(CacheableAttributeSource cacheableAttributeSource) {
        this.cacheableAttributeSource = cacheableAttributeSource;
    }

    public void setCreateCaches(boolean createCaches) {
        this.createCaches = createCaches;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final CacheableAttribute cachableAttribute = this.cacheableAttributeSource.getCachableAttribute(methodInvocation.getMethod(), methodInvocation.getClass());
        if (cachableAttribute == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Don't need to create transaction for [" + methodInvocation + "]: This method isn't transactional.");
            }
            
            return methodInvocation.proceed();
        }
        
        //Get the cache
        final String cacheName = cachableAttribute.getCacheName();
        final Ehcache cache = this.getCache(cacheName);

        //Generate the cache key
        final String keyGeneratorName = cachableAttribute.getKeyGeneratorName();
        final CacheKeyGenerator cacheKeyGenerator = this.beanFactory.getBean(keyGeneratorName, CacheKeyGenerator.class);
        final Serializable key = cacheKeyGenerator.generateKey(methodInvocation);

        //See if there is a cached result
        final Element element = cache.get(key);
        if (element != null) {
            return element.getObjectValue();
        }

        //Determine if exception caching is enabled
        final String exceptionCacheName = cachableAttribute.getExceptionCacheName();
        final Ehcache exceptionCache;
        if (exceptionCacheName != null) {
            
            //See if there is a cached exception
            exceptionCache = this.getCache(exceptionCacheName);
            final Element execptionElement = exceptionCache.get(key);
            if (execptionElement != null) {
                throw (Throwable)execptionElement.getObjectValue();
            }
        }
        else {
            exceptionCache = null;
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
    
    /**
     * Looks up the CacheManager by the configured cacheManagerBeanName if set. If not set calls
     * {@link BeanFactory#getBean(Class)} to locate a CacheManager.
     * 
     * @return The lazy-loaded CacheManager.
     */
    protected CacheManager getCacheManager() {
        if (this.cacheManager == null) {
            if (this.cacheManagerBeanName != null) {
                this.cacheManager = this.beanFactory.getBean(this.cacheManagerBeanName, CacheManager.class);
            }
            else {
                this.cacheManager = this.beanFactory.getBean(CacheManager.class);
            }
        }
        
        return this.cacheManager;
    }

    /**
     * Get or create the specified cache if it does not exist and createCaches is set to true. 
     * 
     * @param cacheName The name of the cache to retrieve
     * @return The cache
     * @throws RuntimeException if the cache does not exist and createCaches is false.
     */
    protected Ehcache getCache(final String cacheName) {
        final CacheManager cacheManager = this.getCacheManager();
        
        Ehcache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (this.createCaches) {
                this.logger.warn("No cache named '" + cacheName + "' exists, it will be created from the defaultCache");
                try {
                    cacheManager.addCache(cacheName);
                }
                catch (ObjectExistsException oee) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Race condition creating non-existant cache '" + cacheName + "', ignoring and retrieving existing cache");
                    }
                }
                cache = cacheManager.getCache(cacheName);
            }
            else {
                //TODO better exception type
                throw new RuntimeException("Cache '" + cacheName + "' does not exist");
            }
        }
        return cache;
    }

}
