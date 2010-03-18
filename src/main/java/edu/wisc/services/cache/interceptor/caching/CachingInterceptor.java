/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.interceptor.caching;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
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
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CachingInterceptor implements MethodInterceptor, BeanFactoryAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private CacheableAttributeSource cacheableAttributeSource;
    private CacheManager cacheManager;
    private BeanFactory beanFactory;
    private boolean createCaches = false;
    
    public void setCacheableAttributeSource(CacheableAttributeSource cacheableAttributeSource) {
        this.cacheableAttributeSource = cacheableAttributeSource;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
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
        
        final String keyGeneratorName = cachableAttribute.getKeyGeneratorName();
        final CacheKeyGenerator cacheKeyGenerator = this.beanFactory.getBean(keyGeneratorName, CacheKeyGenerator.class);
        
        final String cacheName = cachableAttribute.getCacheName();
        final Cache cache = this.getCache(cacheName);

        final Serializable key = cacheKeyGenerator.generateKey(methodInvocation);

        final Element element = cache.get(key);
        if (element != null) {
            return element.getObjectValue();
        }
        
        //TODO handle caching of exceptions?
        final Object value = methodInvocation.proceed();
        
        cache.put(new Element(key, value));
        
        return value;
    }

    /**
     * @param cacheName
     * @return
     */
    protected Cache getCache(final String cacheName) {
        Cache cache = this.cacheManager.getCache(cacheName);
        if (cache == null) {
            if (this.createCaches) {
                try {
                    this.cacheManager.addCache(cacheName);
                }
                catch (ObjectExistsException oee) {
                    this.logger.warn("Race condition creating non-existant cache '" + cacheName + "', ignoring and retrieving existing cache");
                }
                cache = this.cacheManager.getCache(cacheName);
            }
            else {
                throw new RuntimeException("Cache '" + cacheName + "' does not exist");
            }
        }
        return cache;
    }

}
