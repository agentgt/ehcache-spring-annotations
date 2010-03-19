/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.services.cache.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.ObjectExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.CacheableAttributeSource;
import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.config.AnnotationDrivenEhCacheBeanDefinitionParser;
import edu.wisc.services.cache.key.CacheKeyGenerator;
import edu.wisc.services.cache.provider.CacheNotFoundException;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheableAttributeSourceImpl implements CacheableAttributeSource, BeanFactoryAware {
    private static final CacheableAttribute NULL_CACHEABLE_ATTRIBUTE = new CacheableAttribute() {
        @Override
        public Ehcache getCache() {
            return null;
        }
        @Override
        public CacheKeyGenerator getCacheKeyGenerator() {
            return null;
        }
        @Override
        public Ehcache getExceptionCache() {
            return null;
        }
    };
    
    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Cache of CacheableAttributes, keyed by DefaultCacheKey (Method + target Class).
     */
    private final Map<Object, CacheableAttribute> attributeCache = new ConcurrentHashMap<Object, CacheableAttribute>();
    
    private CacheManager cacheManager;
    private BeanFactory beanFactory;
    private String cacheManagerBeanName;
    private boolean createCaches = false;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    public void setCacheManagerBeanName(String cacheManagerBeanName) {
        this.cacheManagerBeanName = cacheManagerBeanName;
    }
    public void setCreateCaches(boolean createCaches) {
        this.createCaches = createCaches;
    }

    /* (non-Javadoc)
     * @see org.jasig.spring.cache.CacheableAttributeSource#getCacheableAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass) {
        // First, see if we have a cached value.
        Object cacheKey = getCacheKey(method, targetClass);
        final CacheableAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            // Value will either be canonical value indicating there is no transaction attribute,
            // or an actual cacheable attribute.
            if (cached == NULL_CACHEABLE_ATTRIBUTE) {
                return null;
            }

            return cached;
        }
        
        // We need to work it out.
        final CacheableAttribute att = computeCacheableAttribute(method, targetClass);
        // Put it in the cache.
        if (att == null) {
            this.attributeCache.put(cacheKey, NULL_CACHEABLE_ATTRIBUTE);
        }
        else {
            this.logger.debug("Adding Cacheable method '{}' under key '{}' with attribute: {}", new Object[] { method.getName(), cacheKey, att });
            this.attributeCache.put(cacheKey, att);
        }
        return att;
    }

    /**
     * Determine a cache key for the given method and target class.
     * <p>Must not produce same key for overloaded methods.
     * Must produce same key for different instances of the same method.
     * @param method the method (never <code>null</code>)
     * @param targetClass the target class (may be <code>null</code>)
     * @return the cache key (never <code>null</code>)
     */
    protected Object getCacheKey(Method method, Class<?> targetClass) {
        return new DefaultCacheKey(method, targetClass);
    }

    /**
     * Same signature as {@link #getCacheableAttribute}, but doesn't cache the result.
     * {@link #getCacheableAttribute} is effectively a caching decorator for this method.
     * @see #getCacheableAttribute
     */
    private CacheableAttribute computeCacheableAttribute(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        CacheableAttribute att = findCacheableAttribute(specificMethod);
        if (att != null) {
            return att;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            att = findCacheableAttribute(method);
            if (att != null) {
                return att;
            }
            // Last fallback is the class of the original method.
            return findCacheableAttribute(method.getDeclaringClass());
        }
        
        return null;
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
                this.logger.warn("No cache named '{}' exists, it will be created from the defaultCache", cacheName);
                try {
                    cacheManager.addCache(cacheName);
                }
                catch (ObjectExistsException oee) {
                    this.logger.trace("Race condition creating missing cache '{}', ignoring and retrieving existing cache", cacheName);
                }
                cache = cacheManager.getCache(cacheName);
            }
            else {
                //TODO better exception type
                throw new CacheNotFoundException("Cache '" + cacheName + "' does not exist");
            }
        }
        return cache;
    }


    /**
     * Subclasses need to implement this to return the transaction attribute
     * for the given method, if any.
     * @param method the method to retrieve the attribute for
     * @return all transaction attribute associated with this method
     * (or <code>null</code> if none)
     */
    protected CacheableAttribute findCacheableAttribute(AnnotatedElement ae) {
        Cacheable ann = ae.getAnnotation(Cacheable.class);
        if (ann == null) {
            for (Annotation metaAnn : ae.getAnnotations()) {
                ann = metaAnn.annotationType().getAnnotation(Cacheable.class);
                if (ann != null) {
                    break;
                }
            }
        }
        if (ann != null) {
            return parseCacheableAnnotation(ann);
        }

        return null;
    }


    protected CacheableAttribute parseCacheableAnnotation(Cacheable ann) {
        final Ehcache cache = this.getCache(ann.cacheName());
        
        final Ehcache exceptionCache;
        if (StringUtils.hasLength(ann.exceptionCacheName())) {
            exceptionCache = this.getCache(ann.exceptionCacheName());
        }
        else {
            exceptionCache = null;
        }
        
        final CacheKeyGenerator cacheKeyGenerator;
        if (StringUtils.hasLength(ann.keyGeneratorName())) {
            cacheKeyGenerator = this.beanFactory.getBean(ann.keyGeneratorName(), CacheKeyGenerator.class);
        }
        else {
            cacheKeyGenerator = this.beanFactory.getBean(AnnotationDrivenEhCacheBeanDefinitionParser.DEFAULT_CACHE_KEY_GENERATOR, CacheKeyGenerator.class);
        }
        
        return new CacheableAttributeImpl(cache, exceptionCache, cacheKeyGenerator);
    }


    /**
     * Should only public methods be allowed to have Cacheable semantics?
     * <p>The default implementation returns <code>false</code>.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}
