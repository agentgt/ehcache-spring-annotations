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

package com.googlecode.ecache.annotations.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.googlecode.ecache.annotations.AdviceType;
import com.googlecode.ecache.annotations.CacheAttributeSource;
import com.googlecode.ecache.annotations.CacheNotFoundException;
import com.googlecode.ecache.annotations.Cacheable;
import com.googlecode.ecache.annotations.CacheableAttribute;
import com.googlecode.ecache.annotations.MethodAttribute;
import com.googlecode.ecache.annotations.SelfPopulatingCacheScope;
import com.googlecode.ecache.annotations.TriggersRemove;
import com.googlecode.ecache.annotations.TriggersRemoveAttribute;
import com.googlecode.ecache.annotations.key.CacheKeyGenerator;


/**
 * Provides logic for determining if a class + method are advised and to then setup the
 * data needed for the advice.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheAttributeSourceImpl implements CacheAttributeSource, BeanFactoryAware {
    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Caches for advice data
     */
    private final Map<Object, Object> ingoredMethods = new ConcurrentHashMap<Object, Object>();
    private final ConcurrentMap<Object, MethodAttribute> attributesCache = new ConcurrentHashMap<Object, MethodAttribute>();
    private final ConcurrentMap<String, SelfPopulatingCacheTracker> selfPopulatingCaches = new ConcurrentHashMap<String, SelfPopulatingCacheTracker>(); 
    
    private CacheManager cacheManager;
    private BeanFactory beanFactory;
    private String cacheManagerBeanName;
    private boolean createCaches = false;
    private CacheKeyGenerator<? extends Serializable> defaultCacheKeyGenerator;
    private SelfPopulatingCacheScope selfPopulatingCacheScope = SelfPopulatingCacheScope.SHARED;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    public void setCacheManagerBeanName(String cacheManagerBeanName) {
        this.cacheManagerBeanName = cacheManagerBeanName;
    }
    public void setCreateCaches(boolean createCaches) {
        this.createCaches = createCaches;
    }
	public void setDefaultCacheKeyGenerator(CacheKeyGenerator<? extends Serializable> defaultCacheKeyGenerator) {
		this.defaultCacheKeyGenerator = defaultCacheKeyGenerator;
	}
	public void setSelfPopulatingCacheScope(SelfPopulatingCacheScope selfPopulatingCacheScope) {
        this.selfPopulatingCacheScope = selfPopulatingCacheScope;
    }
	
	
    /* (non-Javadoc)
     * @see com.googlecode.ecache.annotations.CacheAttributeSource#getAdviceType(java.lang.reflect.Method, java.lang.Class)
     */
    public AdviceType getAdviceType(Method method, Class<?> targetClass) {
        final MethodAttribute methodAttribute = this.getMethodAttribute(method, targetClass);
        if (methodAttribute != null) {
            return methodAttribute.getAdviceType();
        }
        
        return AdviceType.NONE;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ecache.annotations.CacheAttributeSource#getCacheableAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass) {
        final MethodAttribute methodAttribute = this.getMethodAttribute(method, targetClass);
        if (methodAttribute != null && AdviceType.CACHE == methodAttribute.getAdviceType()) {
            return (CacheableAttribute)methodAttribute;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ecache.annotations.CacheAttributeSource#getTriggersRemoveAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    public TriggersRemoveAttribute getTriggersRemoveAttribute(Method method, Class<?> targetClass) {
        final MethodAttribute methodAttribute = this.getMethodAttribute(method, targetClass);
        if (methodAttribute != null && AdviceType.REMOVE == methodAttribute.getAdviceType()) {
            return (TriggersRemoveAttribute)methodAttribute;
        }
        
        return null;
    }
    
    private MethodAttribute getMethodAttribute(final Method method, final Class<?> targetClass) {
        final Object cacheKey = this.getCacheKey(method, targetClass);
        
        //Check if the method has already been inspected and should be ignored
        if (this.ingoredMethods.containsKey(cacheKey)) {
            return null;
        }
        
        //Check the cache if the method has already had its advise attributes created
        final MethodAttribute attributes = this.attributesCache.get(cacheKey);
        if (attributes != null) {
            return attributes;
        }
        
        // We need to work it out.
        final MethodAttribute att = this.computeMethodAttribute(method, targetClass);
        
        // Put it in the cache.
        if (att == null) {
            this.ingoredMethods.put(cacheKey, cacheKey);
        }
        else  {
            this.logger.debug("Adding {} advised method '{}' under key '{}' with attribute: {}", new Object[] { att.getAdviceType(), method.getName(), cacheKey, att });
            final MethodAttribute existing = this.attributesCache.putIfAbsent(cacheKey, att);
            if (existing != null) {
                return existing;
            }
        }
        
        return att;
    }


    /**
     * Should only public methods be allowed to be advised?
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
     * @throws CacheNotFoundException if the cache does not exist and createCaches is false.
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
                throw new CacheNotFoundException(cacheName);
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

    /**
     * Determine if the specified {@link AnnotatedElement} is annotated with either {@link Cacheable} or {@link TriggersRemove}
     * 
     * @param ae The element to inspect
     * @return The advice attributes about the element, null if the element is not advised
     */
    private MethodAttribute findMethodAttribute(AnnotatedElement ae) {
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

    /**
     * Construct a {@link CacheableAttribute} from a {@link Cacheable} annotation.
     * 
     * @param ann The annotation to build the attributes from
     * @return The constructed cacheable advise attributes
     */
    protected CacheableAttribute parseCacheableAnnotation(Cacheable ann) {
        Ehcache cache = this.getCache(ann.cacheName());
        ThreadLocal<MethodInvocation> entryFactory = null;
        if (ann.selfPopulating()) {
            final SelfPopulatingCacheTracker selfPopulatingCacheTracker = this.createSelfPopulatingCacheInternal(cache);
            cache = selfPopulatingCacheTracker.selfPopulatingCache;
            entryFactory = selfPopulatingCacheTracker.cacheEntryFactory;
        }
        
        final Ehcache exceptionCache;
        if (StringUtils.hasLength(ann.exceptionCacheName())) {
            exceptionCache = this.getCache(ann.exceptionCacheName());
        }
        else {
            exceptionCache = null;
        }
        
        final String keyGeneratorName = ann.keyGeneratorName();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = getCacheKeyGenerator(keyGeneratorName);
        
        return new CacheableAttributeImpl(cache, exceptionCache, cacheKeyGenerator, entryFactory);
    }
    
    /**
     * Creates or retrieves a SelfPopulatingCacheTracker for the specified cache depending on the
     * configured {@link SelfPopulatingCacheScope}
     * 
     * @param cache The cache to create a self populating instance of
     * @return The SelfPopulatingCache and corresponding factory object to use
     */
    protected final SelfPopulatingCacheTracker createSelfPopulatingCacheInternal(Ehcache cache) {
        //If method scoped just create a new instance 
        if (SelfPopulatingCacheScope.METHOD == this.selfPopulatingCacheScope) {
            return this.createSelfPopulatingCache(cache);
        }

        //Shared scope, try loading the instance from local Map
        
        //See if there is a cached SelfPopulatingCache for the name
        final String cacheName = cache.getName();
        SelfPopulatingCacheTracker selfPopulatingCacheTracker = this.selfPopulatingCaches.get(cacheName);
        if (selfPopulatingCacheTracker == null) {
            selfPopulatingCacheTracker = this.createSelfPopulatingCache(cache);
            
            //do putIfAbsent to handle concurrent creation. If a value is returned it was already put and that
            //value should be used. If no value was returned the newly created selfPopulatingCache should be used
            final SelfPopulatingCacheTracker existing = this.selfPopulatingCaches.putIfAbsent(cacheName, selfPopulatingCacheTracker);
            if (existing != null) {
                selfPopulatingCacheTracker = existing;
            }
        }
        
        return selfPopulatingCacheTracker;
    }

    /**
     * Create a new {@link SelfPopulatingCache} and corresponding {@link CacheEntryFactory}
     */
    protected SelfPopulatingCacheTracker createSelfPopulatingCache(Ehcache cache) {
        final ThreadLocalCacheEntryFactory cacheEntryFactory = new ThreadLocalCacheEntryFactory();
        final SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(cache, cacheEntryFactory);
        return new SelfPopulatingCacheTracker(selfPopulatingCache, cacheEntryFactory.entryFactory);
    }

    /**
     * Construct a {@link TriggersRemoveAttribute} from a {@link TriggersRemove} annotation.
     * 
     * @param ann The annotation to build the attributes from
     * @return The constructed triggers remove advise attributes
     */
    protected TriggersRemoveAttribute parseTriggersRemoveAnnotation(TriggersRemove ann) {
        final Ehcache cache = this.getCache(ann.cacheName());

        final String keyGeneratorName = ann.keyGeneratorName();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = getCacheKeyGenerator(keyGeneratorName);
        
        return new TriggersRemoveAttributeImpl(cache, cacheKeyGenerator, ann.removeAll());
    }
    
    /**
     * Get the {@link CacheKeyGenerator} by name. Returning a default generator if the name is empty or null
     * 
     * @param keyGeneratorName Name of the generator to retrieve
     * @return The named generator or the default generator if the name was empty or null
     */
    @SuppressWarnings("unchecked")
    protected CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator(final String keyGeneratorName) {
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
        if (StringUtils.hasLength(keyGeneratorName)) {
            cacheKeyGenerator = this.beanFactory.getBean(keyGeneratorName, CacheKeyGenerator.class);
        } else {
        	cacheKeyGenerator = this.defaultCacheKeyGenerator;
        }
        return cacheKeyGenerator;
    }
    
    static class SelfPopulatingCacheTracker {
        public final SelfPopulatingCache selfPopulatingCache;
        public final ThreadLocal<MethodInvocation> cacheEntryFactory;
        
        public SelfPopulatingCacheTracker(SelfPopulatingCache selfPopulatingCache, ThreadLocal<MethodInvocation> cacheEntryFactory) {
            this.selfPopulatingCache = selfPopulatingCache;
            this.cacheEntryFactory = cacheEntryFactory;
        }
    }

    /**
     * EhCache entry factory that uses a ThreadLocal to pass a MethodInvocation into the factory
     * for object creation.
     */
    private static class ThreadLocalCacheEntryFactory implements CacheEntryFactory {
        public final ThreadLocal<MethodInvocation> entryFactory = new ThreadLocal<MethodInvocation>();

        public Object createEntry(Object arg0) throws Exception {
            final MethodInvocation methodInvocation = this.entryFactory.get();
            if (methodInvocation == null) {
                throw new RuntimeException("No MethodInvocation specified in the ThreadLocal");
            }
            
            try {
                return methodInvocation.proceed();
            }
            catch (Throwable t) {
                if (t instanceof Exception) {
                    throw (Exception)t;
                }
                else if (t instanceof Error) {
                    throw (Error)t;
                }
                
                throw new Exception(t);
            }
        }
        
    }
}
