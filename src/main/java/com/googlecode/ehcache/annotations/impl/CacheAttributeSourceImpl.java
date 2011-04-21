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

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.AdviceType;
import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.CacheableAttribute;
import com.googlecode.ehcache.annotations.CacheableInterceptor;
import com.googlecode.ehcache.annotations.DecoratedCacheType;
import com.googlecode.ehcache.annotations.DefaultCacheableInterceptor;
import com.googlecode.ehcache.annotations.DefaultTriggersRemoveInterceptor;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.MethodAttribute;
import com.googlecode.ehcache.annotations.ParameterMask;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.ResolverFactory;
import com.googlecode.ehcache.annotations.SelfPopulatingCacheScope;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.TriggersRemoveAttribute;
import com.googlecode.ehcache.annotations.TriggersRemoveInterceptor;
import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ReflectionHelper;
import com.googlecode.ehcache.annotations.key.ReflectionHelperAware;
import com.googlecode.ehcache.annotations.resolver.CacheResolverFactory;
import com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver;
import com.googlecode.ehcache.annotations.resolver.DefaultCacheResolverFactory;
import com.googlecode.ehcache.annotations.resolver.TriggersRemoveCacheResolver;
    

/**
 * Provides logic for determining if a class + method are advised and to then setup the
 * data needed for the advice.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheAttributeSourceImpl implements CacheAttributeSource, BeanFactoryAware, InitializingBean {
    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Caches for advice data
     */
    private final MultiKeyConcurrentMap<Class<?>, Method, Boolean> ingoredMethods = new MultiKeyConcurrentMap<Class<?>, Method, Boolean>();
    private final MultiKeyConcurrentMap<Class<?>, Method, MethodAttribute> attributesCache = new MultiKeyConcurrentMap<Class<?>, Method, MethodAttribute>();
    
    private DefaultListableBeanFactory childBeanFactory;
    
    private CacheManager cacheManager;
    private BeanFactory beanFactory;
    private SelfPopulatingCacheScope selfPopulatingCacheScope;
    private Boolean createCaches;
    private CacheKeyGenerator<? extends Serializable> defaultCacheKeyGenerator;
    private CacheableInterceptor defaultCacheableInterceptor = DefaultCacheableInterceptor.INSTANCE;
    private TriggersRemoveInterceptor defaultTriggersRemoveInterceptor = DefaultTriggersRemoveInterceptor.INSTANCE;
    private ReflectionHelper reflectionHelper;
    private CacheResolverFactory cacheResolverFactory;

    private TaskScheduler scheduler;
    private SchedulingTaskExecutor executor;
    	
    public void setScheduler(TaskScheduler scheduler) {
    	this.scheduler = scheduler;
    }
    
    public void setExecutor(SchedulingTaskExecutor executor) {
    	this.executor = executor;
    }
    
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.childBeanFactory = new DefaultListableBeanFactory(this.beanFactory);
    }
    public void setCreateCaches(boolean createCaches) {
        this.createCaches = createCaches;
    }
    public void setDefaultCacheKeyGenerator(CacheKeyGenerator<? extends Serializable> defaultCacheKeyGenerator) {
        this.defaultCacheKeyGenerator = defaultCacheKeyGenerator;
    }
    public void setDefaultCacheableInterceptor(CacheableInterceptor defaultCacheableInterceptor) {
        this.defaultCacheableInterceptor = defaultCacheableInterceptor;
    }
    public void setDefaultTriggersRemoveInterceptor(TriggersRemoveInterceptor defaultTriggersRemoveInterceptor) {
        this.defaultTriggersRemoveInterceptor = defaultTriggersRemoveInterceptor;
    }
    public void setSelfPopulatingCacheScope(SelfPopulatingCacheScope selfPopulatingCacheScope) {
        this.selfPopulatingCacheScope = selfPopulatingCacheScope;
    }
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    public void setReflectionHelper(ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }
    public void setCacheResolverFactory(CacheResolverFactory cacheResolverFactory) {
        this.cacheResolverFactory = cacheResolverFactory;
    }
    
    public void afterPropertiesSet() throws Exception {
        if (this.cacheResolverFactory == null) {
            final DefaultCacheResolverFactory defaultCacheResolverFactory = new DefaultCacheResolverFactory(this.getCacheManager());
            if (this.createCaches != null) {
                defaultCacheResolverFactory.setCreateCaches(this.createCaches);
            }
            if (this.selfPopulatingCacheScope != null) {
                defaultCacheResolverFactory.setSelfPopulatingCacheScope(this.selfPopulatingCacheScope);
            }
            defaultCacheResolverFactory.setExecutor(executor);
            defaultCacheResolverFactory.setScheduler(scheduler);            
            this.cacheResolverFactory = defaultCacheResolverFactory;
        }
        else {
            if (this.createCaches != null) {
                this.logger.warn("createCaches was specified but a custom CacheResolverFactory was also configured. The createCaches value will be ignored.");
            }
            if (this.selfPopulatingCacheScope != null) {
                this.logger.warn("selfPopulatingCacheScope was specified but a custom CacheResolverFactory was also configured. The selfPopulatingCacheScope value will be ignored.");
            }
            if (this.executor != null) {
                this.logger.warn("executor was specified but a custom CacheResolverFactory was also configured. The executor value will be ignored.");
            }
            if (this.scheduler != null) {
                this.logger.warn("scheduler was specified but a custom CacheResolverFactory was also configured. The scheduler value will be ignored.");
            }
        }
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
        //Check if the method has already been inspected and should be ignored
        if (this.ingoredMethods.containsKey(targetClass, method)) {
            return null;
        }
        
        //Check the cache if the method has already had its advise attributes created
        final MethodAttribute attributes = this.attributesCache.get(targetClass, method);
        if (attributes != null) {
            return attributes;
        }
        
        // We need to work it out.
        final MethodAttribute att = this.computeMethodAttribute(method, targetClass);
        
        // Put it in the cache.
        if (att == null) {
            this.ingoredMethods.put(targetClass, method, Boolean.TRUE);
        }
        else  {
            this.logger.debug("Adding {} advised method '{}' with attribute: {}", new Object[] { att.getAdviceType(), method.getName(), att });
            final MethodAttribute existing = this.attributesCache.putIfAbsent(targetClass, method, att);
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
     * @return The injected CacheManager.
     */
    protected CacheManager getCacheManager() {
        return this.cacheManager;
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
            final ParameterMask parameterMask = this.parsePartialCacheKeyAnnotations(method);
            return this.parseCacheableAnnotation(cacheableAnnotation, method, parameterMask);
        }
        
        TriggersRemove triggersRemove = method.getAnnotation(TriggersRemove.class);
        if (triggersRemove != null) {
            final ParameterMask parameterMask = this.parsePartialCacheKeyAnnotations(method);
            return this.parseTriggersRemoveAnnotation(triggersRemove, method, parameterMask);
        }
        
        for (final Annotation metaAnn : method.getAnnotations()) {
            final Class<? extends Annotation> annotationType = metaAnn.annotationType();
            cacheableAnnotation = annotationType.getAnnotation(Cacheable.class);
            if (cacheableAnnotation != null) {
                final ParameterMask parameterMask = this.parsePartialCacheKeyAnnotations(method);
                return this.parseCacheableAnnotation(cacheableAnnotation, method, parameterMask);
            }
            
            triggersRemove = annotationType.getAnnotation(TriggersRemove.class);
            if (triggersRemove != null) {
                final ParameterMask parameterMask = this.parsePartialCacheKeyAnnotations(method);
                return this.parseTriggersRemoveAnnotation(triggersRemove, method, parameterMask);
            }
        }

        return null;
    }
    
    /**
     * Parse the parameters annotated with {@link PartialCacheKey}.
     * 
     * @param method The method who's parameters should be checked.
     * @return The parameter mask for the annotated parameters.
     */
    protected ParameterMask parsePartialCacheKeyAnnotations(Method method) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final boolean[] mask = new boolean[parameterAnnotations.length];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            final Annotation[] annotations = parameterAnnotations[i];
            for (final Annotation annotation : annotations) {
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if (PartialCacheKey.class.equals(annotationType)) {
                    mask[i] = true;
                    break;
                }
            }
        }
        
        return ParameterMask.create(mask);
    }

    /**
     * Construct a {@link CacheableAttribute} from a {@link Cacheable} annotation.
     * 
     * @param ann The annotation to build the attributes from
     * @return The constructed cacheable advise attributes
     */
    protected CacheableAttribute parseCacheableAnnotation(Cacheable ann, Method method, ParameterMask parameterMask) {
        final String resolverFactoryName = ann.resolverFactoryName();
        final ResolverFactory resolverFactory = ann.resolverFactory();
        final CacheResolverFactory cacheResolverFactory = this.getCacheResolverFactory(resolverFactoryName, resolverFactory);
        
        final CacheableCacheResolver cacheResolver = cacheResolverFactory.getCacheResolver(ann, method);
        
        final String keyGeneratorName = ann.keyGeneratorName();
        final KeyGenerator keyGenerator = ann.keyGenerator();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = this.getCacheKeyGenerator(keyGeneratorName, keyGenerator);
        
        final String cacheableInteceptorName = ann.cacheableInteceptorName();
        final CacheableInterceptor cacheInterceptor = this.getCacheInterceptor(cacheableInteceptorName);
        
        final boolean cacheNull = ann.cacheNull();
        final DecoratedCacheType decoratedCacheType = DecoratedCacheType.getDecoratedCacheType(ann, method);
        if (!cacheNull && (decoratedCacheType == DecoratedCacheType.REFRESHING_SELF_POPULATING_CACHE || decoratedCacheType == DecoratedCacheType.SELF_POPULATING_CACHE)) {
            this.logger.warn("cacheNull is set to false and decoratedCacheType is " + decoratedCacheType + ", cacheNull will be ignored on: " + method);
        }

        return new CacheableAttributeImpl(cacheResolver, cacheKeyGenerator, parameterMask, cacheNull, cacheInterceptor);
    }

    /**
     * Construct a {@link TriggersRemoveAttribute} from a {@link TriggersRemove} annotation.
     * 
     * @param ann The annotation to build the attributes from
     * @return The constructed triggers remove advise attributes
     */
    protected TriggersRemoveAttribute parseTriggersRemoveAnnotation(TriggersRemove ann, Method method, ParameterMask parameterMask) {
        final String resolverFactoryName = ann.resolverFactoryName();
        final ResolverFactory resolverFactory = ann.resolverFactory();
        final CacheResolverFactory cacheResolverFactory = this.getCacheResolverFactory(resolverFactoryName, resolverFactory);
        
        final TriggersRemoveCacheResolver cacheResolver = cacheResolverFactory.getCacheResolver(ann, method);

        final String keyGeneratorName = ann.keyGeneratorName();
        final KeyGenerator keyGenerator = ann.keyGenerator();
        final CacheKeyGenerator<? extends Serializable> cacheKeyGenerator = this.getCacheKeyGenerator(keyGeneratorName, keyGenerator);
        
        final String triggersRemoveInteceptorName = ann.triggersRemoveInteceptorName();
        final TriggersRemoveInterceptor triggersRemoveInterceptor = this.getTriggersRemoveInterceptor(triggersRemoveInteceptorName);
        
        final boolean removeAll = ann.removeAll();
        final When when = ann.when();
        
        return new TriggersRemoveAttributeImpl(cacheResolver, cacheKeyGenerator, parameterMask, triggersRemoveInterceptor, removeAll, when);
    }
    
    /**
     * Get the {@link CacheableInterceptor} by name. Returning a default resolver factory if the name is empty or null
     * 
     * @param resolverFactoryName Name of the resolver factory to retrieve
     * @return The named generator or the default generator if the name was empty or null
     */
    protected final CacheableInterceptor getCacheInterceptor(String cacheableInteceptorName) {
        if (StringUtils.hasLength(cacheableInteceptorName)) {
            return this.beanFactory.getBean(cacheableInteceptorName, CacheableInterceptor.class);
        }
        
        return this.defaultCacheableInterceptor;
    }
    
    /**
     * Get the {@link TriggersRemoveInterceptor} by name. Returning a default resolver factory if the name is empty or null
     * 
     * @param resolverFactoryName Name of the resolver factory to retrieve
     * @return The named generator or the default generator if the name was empty or null
     */
    protected final TriggersRemoveInterceptor getTriggersRemoveInterceptor(String triggersRemoveInterceptorName) {
        if (StringUtils.hasLength(triggersRemoveInterceptorName)) {
            return this.beanFactory.getBean(triggersRemoveInterceptorName, TriggersRemoveInterceptor.class);
        }
        
        return this.defaultTriggersRemoveInterceptor;
    }
    
    /**
     * Get the {@link CacheResolverFactory} by name. Returning a default resolver factory if the name is empty or null
     * 
     * @param resolverFactoryName Name of the resolver factory to retrieve
     * @return The named generator or the default generator if the name was empty or null
     */
    protected final CacheResolverFactory getCacheResolverFactory(String resolverFactoryName, ResolverFactory resolverFactory) {
        String cacheResolverClassName = resolverFactory.name();
        if (cacheResolverClassName.length() > 0) {
            return this.getOrCreateCacheResolverFactory(resolverFactory);
        }

        if (StringUtils.hasLength(resolverFactoryName)) {
            return this.beanFactory.getBean(resolverFactoryName, CacheResolverFactory.class);
        }
        
        return this.cacheResolverFactory;
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
        String keyGeneratorClassName = keyGenerator.name();
        if (!keyGeneratorClassName.contains(".")) {
            keyGeneratorClassName  = "com.googlecode.ehcache.annotations.key." + keyGeneratorClassName;
        }
        
        return this.getOrCreateChildBean(CacheKeyGenerator.class, keyGeneratorClassName, keyGenerator.properties());
    }
    
    protected final CacheResolverFactory getOrCreateCacheResolverFactory(ResolverFactory resolverFactory) {
        String resolverFactoryClassName = resolverFactory.name();
        if (!resolverFactoryClassName.contains(".")) {
            resolverFactoryClassName  = "com.googlecode.ehcache.annotations.resolver." + resolverFactoryClassName;
        }
        
        return this.getOrCreateChildBean(CacheResolverFactory.class, resolverFactoryClassName, resolverFactory.properties());
    }
    
    protected final <T> T getOrCreateChildBean(Class<T> beanType, String beanClass, Property[] properties) {
        final StringBuilder beanNameBuilder = new StringBuilder();
        
        beanNameBuilder.append(beanClass);
        
        final MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        
        //Sort the properties array first so bean name generation is always consistent
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
        
        //See if the bean is already registered using the compiled bean name, if so just use that instance
        if (this.childBeanFactory.containsBean(beanName)) {
            return this.childBeanFactory.getBean(beanName, beanType);
        }

        //Create and register the bean if it didn't already exist
        final AbstractBeanDefinition beanDefinition;
        try {
            beanDefinition = BeanDefinitionReaderUtils.createBeanDefinition(null, beanClass, ClassUtils.getDefaultClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new BeanCreationException("Could not find class '" + beanClass + "' to create " + beanType + " from", e);
        }
        
        if (ReflectionHelperAware.class.isAssignableFrom(beanDefinition.getBeanClass())) {
            mutablePropertyValues.addPropertyValue("reflectionHelper", this.reflectionHelper);
        }
        
        beanDefinition.setPropertyValues(mutablePropertyValues);
        this.childBeanFactory.registerBeanDefinition(beanName, beanDefinition);
        
        return this.childBeanFactory.getBean(beanName, beanType);
    }
}
