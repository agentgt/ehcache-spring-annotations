/**
 * Copyright 2010 Nicholas Blair, Eric Dalquist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.ehcache.annotations.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.CacheableAttribute;
import com.googlecode.ehcache.annotations.MethodAttribute;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;


/**
 * Intercepter that handles invocations on methods annotated with {@link Cacheable} or {@link TriggersRemove}.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class EhCacheInterceptor implements MethodInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private CacheAttributeSource cacheAttributeSource;

    public void setCacheAttributeSource(CacheAttributeSource cacheableAttributeSource) {
        this.cacheAttributeSource = cacheableAttributeSource;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public final Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Method method = methodInvocation.getMethod();
        final Class<?> targetClass = (methodInvocation.getThis() != null ? methodInvocation.getThis().getClass() : null);
        final AdviceType adviceType = this.cacheAttributeSource.getAdviceType(method, targetClass);
        
        switch (adviceType) {
            case CACHE: {
                final CacheableAttribute cacheableAttribute = this.cacheAttributeSource.getCacheableAttribute(method, targetClass);
                if (cacheableAttribute == null) {
                    this.logger.warn("CacheAttributeSource.getAdviceType(Method, Class) returned CACHE but CacheAttributeSource.getCacheableAttribute(Method, Class) returned null. Method will be invoked directly with no Cachable advice. Method: {}, Class: {}", method, targetClass);
                    return methodInvocation.proceed();
                }
                
                return this.invokeCacheable(methodInvocation, cacheableAttribute);

            }

            case REMOVE: {
                final TriggersRemoveAttribute triggersRemoveAttribute = this.cacheAttributeSource.getTriggersRemoveAttribute(method, targetClass);
                if (triggersRemoveAttribute == null) {
                    this.logger.warn("CacheAttributeSource.getAdviceType(Method, Class) returned REMOVE but CacheAttributeSource.getTriggersRemoveAttribute(Method, Class) returned null. Method will be invoked directly with no TriggersFlush advice. Method: {}, Class: {}", method, targetClass);
                    return methodInvocation.proceed();
                }
                
                return this.invokeTriggersRemove(methodInvocation, triggersRemoveAttribute);
            }
            
            default: {
                this.logger.trace("The method {} is not advised {}.", methodInvocation, adviceType);
                return methodInvocation.proceed();
            }
        }
    }
    
    /**
     * Called if the {@link MethodInvocation} is annotated with {@link Cacheable}.
     * 
     * @param methodInvocation Original method invocation
     * @param cacheableAttribute Information about the {@link Cacheable} annotation
     * @return The result of the invocation or the cached result
     * @throws Throwable exception thrown by the invocation or cached exception
     */
    private Object invokeCacheable(final MethodInvocation methodInvocation, final CacheableAttribute cacheableAttribute) throws Throwable {
        //Generate the cache key
        final Serializable cacheKey = this.generateCacheKey(methodInvocation, cacheableAttribute);
        
        this.checkForCachedException(cacheableAttribute, cacheKey);
        
        //See if this is self-populating
        if (cacheableAttribute.getEntryFactory() != null) {
            return this.invokeSelfPopulatingCacheable(methodInvocation, cacheableAttribute, cacheKey);
        }

        //See if there is a cached result
        final Ehcache cache = cacheableAttribute.getCache();
        final Element element = cache.get(cacheKey);
        if (element != null) {
            return element.getObjectValue();
        }

        //No cached value or exception, proceed
        final Object value;
        try {
            value = methodInvocation.proceed();
        }
        catch (Throwable t) {
            this.cacheException(cacheableAttribute, cacheKey, t);
            throw t;
        }
        
        //Cache and return the value
        cache.put(new Element(cacheKey, value));
        return value;
    }

    /**
     * Handles invoking the advised method via a {@link SelfPopulatingCache}. The {@link MethodInvocation} is set into
     * a {@link ThreadLocal} which is used by the {@link CacheEntryFactory} to create the object if needed.
     * 
     * @param methodInvocation The advised invocation
     * @param cacheableAttribute Configuration for the method invocation
     * @param key The cache key for the invocation
     * @return The result of the invocation or the cached result.
     * @throws Throwable Exception from the invocation
     */
    private Object invokeSelfPopulatingCacheable(final MethodInvocation methodInvocation, final CacheableAttribute cacheableAttribute, final Serializable key) throws Throwable {
        final Ehcache cache = cacheableAttribute.getCache();

        final ThreadLocal<MethodInvocation> entryFactory = cacheableAttribute.getEntryFactory();
        if (entryFactory == null) {
            throw new IllegalArgumentException("CacheableAttribute.getEntryFactory() returned null");
        }
        
        entryFactory.set(methodInvocation);
        final Element element;
        try {
            element = cache.get(key);
        }
        catch (Throwable t) {
        	Throwable cause = t.getCause();
            this.cacheException(cacheableAttribute, key, cause);
            throw cause;
        }
        finally {
            entryFactory.remove();
        }
        
        //This should not occur, it would have to be from a coding error in this library or EhCache's APIs have changed
        if (element == null) {
           throw new IllegalStateException("the supposed SelfPopulatingCache returned null, which violates the contract it should always return an Element; perhaps the cache is not truly a SelfPopulatingCache?");
        }
        
        return element.getObjectValue();
    }
    
    /**
     * Called if the {@link MethodInvocation} is annotated with {@link TriggersRemove}.
     * 
     * @param methodInvocation Original method invocation
     * @param triggersRemoveAttribute Information about the {@link TriggersRemove} annotation
     * @return The result of the invocation
     * @throws Throwable exception thrown by the invocation
     */
    private Object invokeTriggersRemove(final MethodInvocation methodInvocation, final TriggersRemoveAttribute triggersRemoveAttribute) throws Throwable {
        final Ehcache cache = triggersRemoveAttribute.getCache();
        
        if(When.BEFORE_METHOD_INVOCATION.equals(triggersRemoveAttribute.when())) {
        	invokeCacheRemove(methodInvocation, triggersRemoveAttribute, cache);
        	return methodInvocation.proceed();
        }
        
    	final Object methodInvocationResult =  methodInvocation.proceed();
    	invokeCacheRemove(methodInvocation, triggersRemoveAttribute, cache);
    	return methodInvocationResult;
    }

	/**
	 * Call the ehcache remove function as prescribed by the {@link TriggersRemoveAttribute}.
	 * 
	 * @param methodInvocation
	 * @param triggersRemoveAttribute
	 * @param cache
	 */
	private void invokeCacheRemove(final MethodInvocation methodInvocation,
			final TriggersRemoveAttribute triggersRemoveAttribute,
			final Ehcache cache) {
		if (triggersRemoveAttribute.isRemoveAll()) {
			cache.removeAll();
		}
		else {
			final Serializable cacheKey = generateCacheKey(methodInvocation, triggersRemoveAttribute);
			cache.remove(cacheKey);
		}
	}

    /**
     * Check if there is a cached exception for the key. If there is throw it.
     * 
     * @param cacheableAttribute Configuration for the method invocation
     * @param key The cache key for the method invocation
     * @throws Throwable The cached throwable
     */
    protected void checkForCachedException(final CacheableAttribute cacheableAttribute, final Serializable key) throws Throwable {
        //Determine if exception caching is enabled
        final Ehcache exceptionCache = cacheableAttribute.getExceptionCache();
        if (exceptionCache != null) {
            //See if there is a cached exception
            final Element execptionElement = exceptionCache.get(key);
            if (execptionElement != null) {
                throw (Throwable)execptionElement.getObjectValue();
            }
        }
    }

    /**
     * Checks {@link CacheableAttribute#getExceptionCache()}, if not null the exception will be cached.
     * 
     * @param cacheableAttribute Configuration for the method invocation
     * @param key Cache key for the invocation
     * @param t The exception to cache
     */
    protected void cacheException(final CacheableAttribute cacheableAttribute, final Serializable key, final Throwable t) {
        final Ehcache exceptionCache = cacheableAttribute.getExceptionCache();
        if (exceptionCache != null) {
            exceptionCache.put(new Element(key, t));
        }
    }

    /**
     * Creates a {@link Serializable} cache key from the {@link MethodInvocation} and configuration attributes.
     * 
     * @param methodInvocation Invocation to build the key for
     * @param methodAttribute Configuration for the invoked method
     * @return Generated cache key, must not return null.
     */
    protected Serializable generateCacheKey(final MethodInvocation methodInvocation, final MethodAttribute methodAttribute) {
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = methodAttribute.getCacheKeyGenerator();
        final Serializable cacheKey = cacheKeyGenerator.generateKey(methodInvocation);
        this.logger.debug("Generated key '{}' for invocation: {}", cacheKey, methodInvocation);
        return cacheKey;
    }
}
