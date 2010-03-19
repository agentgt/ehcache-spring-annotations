/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import edu.wisc.services.cache.AdviceType;
import edu.wisc.services.cache.CacheAttributeSource;
import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.MethodAttribute;
import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.annotations.TriggersRemove;
import edu.wisc.services.cache.config.AnnotationDrivenEhCacheBeanDefinitionParser;
import edu.wisc.services.cache.key.CacheKeyGenerator;
import edu.wisc.services.cache.provider.CacheNotFoundException;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheAttributeSourceImpl implements CacheAttributeSource, BeanFactoryAware {
    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Cache of CacheableAttributes, keyed by DefaultCacheKey (Method + target Class).
     */
    private final Map<Object, Object> ingoredMethods = new ConcurrentHashMap<Object, Object>();
    private final Map<Object, CacheableAttribute> cacheableAttributes = new ConcurrentHashMap<Object, CacheableAttribute>();
    private final Map<Object, TriggersRemoveAttribute> triggersRemoveAttributes = new ConcurrentHashMap<Object, TriggersRemoveAttribute>();
    
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
     * @see edu.wisc.services.cache.CacheAttributeSource#getAdviceType(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public AdviceType getAdviceType(Method method, Class<?> targetClass) {
        final Object cacheKey = this.getCacheKey(method, targetClass);
        if (this.getCacheableAttribute(cacheKey, method, targetClass) != null) {
            return AdviceType.CACHE;
        }
        
        if (this.getTriggersRemoveAttribute(cacheKey, method, targetClass) != null) {
            return AdviceType.REMOVE;
        }
        
        return AdviceType.NONE;
    }

    /* (non-Javadoc)
     * @see edu.wisc.services.cache.CacheAttributeSource#getCacheableAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass) {
        final Object cacheKey = this.getCacheKey(method, targetClass);
        return this.getCacheableAttribute(cacheKey, method, targetClass);
    }

    /* (non-Javadoc)
     * @see edu.wisc.services.cache.CacheAttributeSource#getTriggersRemoveAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public TriggersRemoveAttribute getTriggersRemoveAttribute(Method method, Class<?> targetClass) {
        final Object cacheKey = this.getCacheKey(method, targetClass);
        return this.getTriggersRemoveAttribute(cacheKey, method, targetClass);
    }
    
    private CacheableAttribute getCacheableAttribute(final Object cacheKey, Method method, Class<?> targetClass) {
        if (this.ingoredMethods.containsKey(cacheKey)) {
            return null;
        }
        
        final CacheableAttribute attributes = this.cacheableAttributes.get(cacheKey);
        if (attributes != null) {
            return attributes;
        }

        final MethodAttribute methodAttribute = this.getMethodAttribute(cacheKey, method, targetClass);
        if (methodAttribute != null && AdviceType.CACHE ==  methodAttribute.getAdviceType()) {
            return (CacheableAttribute) methodAttribute;
        }
        
        return null;
    }

    private TriggersRemoveAttribute getTriggersRemoveAttribute(final Object cacheKey, Method method, Class<?> targetClass) {
        if (this.ingoredMethods.containsKey(cacheKey)) {
            return null;
        }
        
        final TriggersRemoveAttribute attributes = this.triggersRemoveAttributes.get(cacheKey);
        if (attributes != null) {
            return attributes;
        }

        final MethodAttribute methodAttribute = this.getMethodAttribute(cacheKey, method, targetClass);
        if (methodAttribute != null && AdviceType.REMOVE ==  methodAttribute.getAdviceType()) {
            return (TriggersRemoveAttribute) methodAttribute;
        }
        
        return null;
    }
    
    
    private MethodAttribute getMethodAttribute(Object cacheKey, Method method, Class<?> targetClass) {
        // We need to work it out.
        final MethodAttribute att = this.computeMethodAttribute(method, targetClass);
        // Put it in the cache.
        if (att == null) {
            this.ingoredMethods.put(cacheKey, cacheKey);
        }
        else if (AdviceType.CACHE == att.getAdviceType() ){
            this.logger.debug("Adding Cacheable method '{}' under key '{}' with attribute: {}", new Object[] { method.getName(), cacheKey, att });
            this.cacheableAttributes.put(cacheKey, (CacheableAttribute)att);
        }

        else if (AdviceType.REMOVE == att.getAdviceType() ){
            this.logger.debug("Adding TriggersRemove method '{}' under key '{}' with attribute: {}", new Object[] { method.getName(), cacheKey, att });
            this.triggersRemoveAttributes.put(cacheKey, (TriggersRemoveAttribute)att);
        }
        return att;
    }


    /**
     * Should only public methods be allowed to have Flushable semantics?
     * <p>The default implementation returns <code>false</code>.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
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
    protected Ehcache getCache(String cacheName) {
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
    
    private MethodAttribute computeMethodAttribute(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        MethodAttribute att = this.findMethodAttribute(specificMethod);
        if (att != null) {
            return att;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            att = this.findMethodAttribute(method);
            if (att != null) {
                return att;
            }
        }
        
        return null;
    }

    protected MethodAttribute findMethodAttribute(AnnotatedElement ae) {
        Cacheable cacheableAnnotation = ae.getAnnotation(Cacheable.class);
        if (cacheableAnnotation != null) {
            return this.parseCacheableAnnotation(cacheableAnnotation);
        }
        
        TriggersRemove triggersRemove = ae.getAnnotation(TriggersRemove.class);
        if (triggersRemove != null) {
            return this.parseTriggersRemoveAnnotation(triggersRemove);
        }
        
        for (final Annotation metaAnn : ae.getAnnotations()) {
            final Class<? extends Annotation> annotationType = metaAnn.annotationType();
            cacheableAnnotation = annotationType.getAnnotation(Cacheable.class);
            if (cacheableAnnotation != null) {
                return this.parseCacheableAnnotation(cacheableAnnotation);
            }
            
            triggersRemove = annotationType.getAnnotation(TriggersRemove.class);
            if (triggersRemove != null) {
                return this.parseTriggersRemoveAnnotation(triggersRemove);
            }
        }

        return null;
    }

    protected CacheableAttribute parseCacheableAnnotation(Cacheable ann) {
        Ehcache cache = this.getCache(ann.cacheName());
        ThreadLocal<Callable<?>> entryFactory = null;
        if (ann.selfPopulating()) {
            final ThreadLocalCacheEntryFactory cacheEntryFactory = new ThreadLocalCacheEntryFactory();
            entryFactory = cacheEntryFactory.entryFactory;
            cache = new SelfPopulatingCache(cache, cacheEntryFactory);
        }
        
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
        
        return new CacheableAttributeImpl(cache, exceptionCache, cacheKeyGenerator, entryFactory);
    }

    protected TriggersRemoveAttribute parseTriggersRemoveAnnotation(TriggersRemove ann) {
        final Ehcache cache = this.getCache(ann.cacheName());

        final CacheKeyGenerator cacheKeyGenerator;
        if (StringUtils.hasLength(ann.keyGeneratorName())) {
            cacheKeyGenerator = this.beanFactory.getBean(ann.keyGeneratorName(), CacheKeyGenerator.class);
        }
        else {
            cacheKeyGenerator = this.beanFactory.getBean(AnnotationDrivenEhCacheBeanDefinitionParser.DEFAULT_CACHE_KEY_GENERATOR, CacheKeyGenerator.class);
        }
        
        return new TriggersRemoveAttributeImpl(cache, cacheKeyGenerator, ann.removeAll());
    }

    private static class ThreadLocalCacheEntryFactory implements CacheEntryFactory {
        public final ThreadLocal<Callable<?>> entryFactory = new ThreadLocal<Callable<?>>();

        @Override
        public Object createEntry(Object arg0) throws Exception {
            final Callable<?> callable = entryFactory.get();
            if (callable == null) {
                throw new RuntimeException("No Callable<?> specified in the ThreadLocal");
            }
            return callable.call();
        }
        
    }
}
