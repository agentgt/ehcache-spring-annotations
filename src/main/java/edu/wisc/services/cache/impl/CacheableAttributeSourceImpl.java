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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.CacheableAttributeSource;
import edu.wisc.services.cache.annotations.Cacheable;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheableAttributeSourceImpl implements CacheableAttributeSource {
    private static final CacheableAttribute NULL_CACHABLE_ATTRIBUTE = new CacheableAttribute() {
        @Override
        public boolean isBlocking() {
            return false;
        }
        @Override
        public String getCacheName() {
            return null;
        }
    };
    
    /**
     * Logger available to subclasses.
     * <p>As this base class is not marked Serializable, the logger will be recreated
     * after serialization - provided that the concrete subclass is Serializable.
     */
    protected final Log logger = LogFactory.getLog(getClass());
    
    /**
     * Cache of CachableAttributes, keyed by DefaultCacheKey (Method + target Class).
     * <p>As this base class is not marked Serializable, the cache will be recreated
     * after serialization - provided that the concrete subclass is Serializable.
     */
    private final Map<Object, CacheableAttribute> attributeCache = new ConcurrentHashMap<Object, CacheableAttribute>();


    /* (non-Javadoc)
     * @see org.jasig.spring.cache.CacheableAttributeSource#getCachableAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public CacheableAttribute getCachableAttribute(Method method, Class<?> targetClass) {
        // First, see if we have a cached value.
        Object cacheKey = getCacheKey(method, targetClass);
        final CacheableAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            // Value will either be canonical value indicating there is no transaction attribute,
            // or an actual transaction attribute.
            if (cached == NULL_CACHABLE_ATTRIBUTE) {
                return null;
            }

            return cached;
        }
        
        // We need to work it out.
        final CacheableAttribute att = computeCachableAttribute(method, targetClass);
        // Put it in the cache.
        if (att == null) {
            this.attributeCache.put(cacheKey, NULL_CACHABLE_ATTRIBUTE);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Adding Cachable method '" + method.getName() + "' with attribute: " + att);
            }
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
     * Same signature as {@link #getCachableAttribute}, but doesn't cache the result.
     * {@link #getCachableAttribute} is effectively a caching decorator for this method.
     * @see #getCachableAttribute
     */
    private CacheableAttribute computeCachableAttribute(Method method, Class<?> targetClass) {
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
        CacheableAttribute att = findCachableAttribute(specificMethod);
        if (att != null) {
            return att;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            att = findCachableAttribute(method);
            if (att != null) {
                return att;
            }
            // Last fallback is the class of the original method.
            return findCachableAttribute(method.getDeclaringClass());
        }
        
        return null;
    }


    /**
     * Subclasses need to implement this to return the transaction attribute
     * for the given method, if any.
     * @param method the method to retrieve the attribute for
     * @return all transaction attribute associated with this method
     * (or <code>null</code> if none)
     */
    protected CacheableAttribute findCachableAttribute(AnnotatedElement ae) {
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
            return parseTransactionAnnotation(ann);
        }

        return null;
    }


    protected CacheableAttribute parseTransactionAnnotation(Cacheable ann) {
        return new CacheableAttributeImpl(ann.value(), ann.blocking());
    }


    /**
     * Should only public methods be allowed to have Cachable semantics?
     * <p>The default implementation returns <code>false</code>.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
    }


    /**
     * Default cache key for the CachableAttribute cache.
     */
    private static class DefaultCacheKey {

        private final Method method;

        private final Class<?> targetClass;

        public DefaultCacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DefaultCacheKey)) {
                return false;
            }
            DefaultCacheKey otherKey = (DefaultCacheKey) other;
            return (this.method.equals(otherKey.method) &&
                    ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() * 29 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }
    }
}
