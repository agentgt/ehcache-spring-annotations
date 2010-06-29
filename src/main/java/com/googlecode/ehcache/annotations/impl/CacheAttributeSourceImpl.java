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

package com.googlecode.ehcache.annotations.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.CacheNotFoundException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.CacheableAttribute;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.MethodAttribute;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.SelfPopulatingCacheScope;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.config.AnnotationDrivenEhCacheBeanDefinitionParser;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ReflectionHelperAware;


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
    
    private DefaultListableBeanFactory cacheKeyBeanFactory;
    
    private CacheManager cacheManager;
    private BeanFactory beanFactory;
    private boolean createCaches = false;
    private CacheKeyGenerator<? extends Serializable> defaultCacheKeyGenerator;
    private SelfPopulatingCacheScope selfPopulatingCacheScope = SelfPopulatingCacheScope.SHARED;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.cacheKeyBeanFactory = new DefaultListableBeanFactory(this.beanFactory);
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
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	/* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheAttributeSource#getAdviceType(java.lang.reflect.Method, java.lang.Class)
     */
    public AdviceType getAdviceType(Method method, Class<?> targetClass) {
        final MethodAttribute methodAttribute = this.getMethodAttribute(method, targetClass);
        if (methodAttribute != null) {
            return methodAttribute.getAdviceType();
        }
        
        return AdviceType.NONE;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheAttributeSource#getCacheableAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass) {
        final MethodAttribute methodAttribute = this.getMethodAttribute(method, targetClass);
        if (methodAttribute != null && AdviceType.CACHE == methodAttribute.getAdviceType()) {
            return (CacheableAttribute)methodAttribute;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.CacheAttributeSource#getTriggersRemoveAttribute(java.lang.reflect.Method, java.lang.Class)
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
     * @return The injected CacheManager.
     */
    protected CacheManager getCacheManager() {
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
        
        Ehcache cache = cacheManager.getEhcache(cacheName);
        if (cache == null) {
            if (this.createCaches) {
                this.logger.warn("No cache named '{}' exists, it will be created from the defaultCache", cacheName);
                try {
                    cacheManager.addCache(cacheName);
                }
                catch (ObjectExistsException oee) {
                    this.logger.trace("Race condition creating missing cache '{}', ignoring and retrieving existing cache", cacheName);
                }
                cache = cacheManager.getEhcache(cacheName);
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
     * @param method The element to inspect
     * @return The advice attributes about the element, null if the element is not advised
     */
    private MethodAttribute findMethodAttribute(Method method) {
        Cacheable cacheableAnnotation = method.getAnnotation(Cacheable.class);
        if (cacheableAnnotation != null) {
            final Set<Integer> annotatedMethodIndices = this.parsePartialCacheKeyAnnotations(method);
            return this.parseCacheableAnnotation(cacheableAnnotation, annotatedMethodIndices);
        }
        
        TriggersRemove triggersRemove = method.getAnnotation(TriggersRemove.class);
        if (triggersRemove != null) {
            final Set<Integer> annotatedMethodIndices = this.parsePartialCacheKeyAnnotations(method);
            return this.parseTriggersRemoveAnnotation(triggersRemove, annotatedMethodIndices);
        }
        
        for (final Annotation metaAnn : method.getAnnotations()) {
            final Class<? extends Annotation> annotationType = metaAnn.annotationType();
            cacheableAnnotation = annotationType.getAnnotation(Cacheable.class);
            if (cacheableAnnotation != null) {
                final Set<Integer> annotatedMethodIndices = this.parsePartialCacheKeyAnnotations(method);
                return this.parseCacheableAnnotation(cacheableAnnotation, annotatedMethodIndices);
            }
            
            triggersRemove = annotationType.getAnnotation(TriggersRemove.class);
            if (triggersRemove != null) {
                final Set<Integer> annotatedMethodIndices = this.parsePartialCacheKeyAnnotations(method);
                return this.parseTriggersRemoveAnnotation(triggersRemove, annotatedMethodIndices);
            }
        }

        return null;
    }
    
    /**
     * Parse the parameters annotated with {@link PartialCacheKey}.
     * 
     * @param method The method whos parameters should be checked.
     * @return A set of parameter indices that are annotated. The set will be empty if no {@link PartialCacheKey} annotations are found.
     */
    protected Set<Integer> parsePartialCacheKeyAnnotations(Method method) {
        final Set<Integer> annotatedParameterIndices = new LinkedHashSet<Integer>();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            final Annotation[] annotations = parameterAnnotations[i];
            for (final Annotation annotation : annotations) {
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if (PartialCacheKey.class.equals(annotationType)) {
                    annotatedParameterIndices.add(i);
                    break;
                }
            }
        }
        
        return annotatedParameterIndices;
    }

    /**
     * Construct a {@link CacheableAttribute} from a {@link Cacheable} annotation.
     * 
     * @param ann The annotation to build the attributes from
     * @return The constructed cacheable advise attributes
     */
    protected CacheableAttribute parseCacheableAnnotation(Cacheable ann, Set<Integer> annotatedMethodIndices) {
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
        final KeyGenerator keyGenerator = ann.keyGenerator();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = this.getCacheKeyGenerator(keyGeneratorName, keyGenerator);
        
        return new CacheableAttributeImpl(cache, exceptionCache, cacheKeyGenerator, annotatedMethodIndices, entryFactory);
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
    protected TriggersRemoveAttribute parseTriggersRemoveAnnotation(TriggersRemove ann, Set<Integer> annotatedMethodIndices) {
        final Ehcache cache = this.getCache(ann.cacheName());

        final String keyGeneratorName = ann.keyGeneratorName();
        final KeyGenerator keyGenerator = ann.keyGenerator();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = this.getCacheKeyGenerator(keyGeneratorName, keyGenerator);
        
        return new TriggersRemoveAttributeImpl(cache, cacheKeyGenerator, annotatedMethodIndices, ann.removeAll(), ann.when());
    }
    
    /**
     * Get the {@link CacheKeyGenerator} by name. Returning a default generator if the name is empty or null
     * 
     * @param keyGeneratorName Name of the generator to retrieve
     * @return The named generator or the default generator if the name was empty or null
     */
    @SuppressWarnings("unchecked")
    protected final CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator(String keyGeneratorName, KeyGenerator keyGenerator) {
        String keyGeneratorClassName = keyGenerator.name();
        if (keyGeneratorClassName.length() > 0) {
            return this.getOrCreateCacheKeyGenerator(keyGenerator);
        }

        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator;
        if (StringUtils.hasLength(keyGeneratorName)) {
            cacheKeyGenerator = this.beanFactory.getBean(keyGeneratorName, CacheKeyGenerator.class);
        } else {
        	cacheKeyGenerator = this.defaultCacheKeyGenerator;
        }
        return cacheKeyGenerator;
    }
    
    @SuppressWarnings("unchecked")
    protected final CacheKeyGenerator<? extends Serializable> getOrCreateCacheKeyGenerator(KeyGenerator keyGenerator) {
        final StringBuilder beanNameBuilder = new StringBuilder();
        
        String keyGeneratorClassName = keyGenerator.name();
        if (!keyGeneratorClassName.contains(".")) {
            keyGeneratorClassName  = "com.googlecode.ehcache.annotations.key." + keyGeneratorClassName;
        }
        
        beanNameBuilder.append(keyGeneratorClassName);
        
        final MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        
        //Sort the properties array first so bean name generation is always consistent
        final Property[] properties = keyGenerator.properties();
        Arrays.sort(properties, PropertyComparator.INSTANCE);
        
        for (Property property : properties) {
            final String name = property.name();
            final String value = property.value();
            final String ref = property.ref();
            
            beanNameBuilder.append("[").append(name).append(",").append(value).append(",").append(ref).append("]");
            
            if (value.length() > 0) {
                if (ref.length() > 0) {
                    throw new IllegalArgumentException("Only one of value or ref must be specified no both on Property with name: " + name);
                }
                
                mutablePropertyValues.addPropertyValue(name, value);
            }
            else if (ref.length() > 0) {
                mutablePropertyValues.addPropertyValue(name, new RuntimeBeanReference(ref));
            }
            else {
                throw new IllegalArgumentException("Either value or ref must be specified on Property with name: " + name);
            }
        }
        
        final String beanName = beanNameBuilder.toString();
        
        //See if the generator is already registered using the compiled bean name, if so just use that instance
        if (this.cacheKeyBeanFactory.containsBean(beanName)) {
            return this.cacheKeyBeanFactory.getBean(beanName, CacheKeyGenerator.class);
        }

        //Create and register the bean if it didn't already exist
        final AbstractBeanDefinition beanDefinition;
        try {
            beanDefinition = BeanDefinitionReaderUtils.createBeanDefinition(null, keyGeneratorClassName, ClassUtils.getDefaultClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new BeanCreationException("Could not find class '" + keyGeneratorClassName + "' to create CacheKeyGenerator from", e);
        }
        
        if (ReflectionHelperAware.class.isAssignableFrom(beanDefinition.getBeanClass())) {
            final RuntimeBeanReference cacheManagerReference = new RuntimeBeanReference(AnnotationDrivenEhCacheBeanDefinitionParser.CACHING_REFLECTION_HELPER_BEAN_NAME);
            mutablePropertyValues.addPropertyValue("reflectionHelper", cacheManagerReference);
        }
        
        beanDefinition.setPropertyValues(mutablePropertyValues);
        this.cacheKeyBeanFactory.registerBeanDefinition(beanName, beanDefinition);
        
        return this.cacheKeyBeanFactory.getBean(beanName, CacheKeyGenerator.class);
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
