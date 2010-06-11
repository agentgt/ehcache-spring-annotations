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

package com.googlecode.ehcache.annotations.config;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.SelfPopulatingCacheScope;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.impl.CacheAttributeSourceImpl;
import com.googlecode.ehcache.annotations.impl.CacheStaticMethodMatcherPointcut;
import com.googlecode.ehcache.annotations.interceptor.EhCacheInterceptor;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ListCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.MessageDigestCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ReflectionHashCodeCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ReflectionHelperAware;
import com.googlecode.ehcache.annotations.key.StringCacheKeyGenerator;
import com.googlecode.ehcache.annotations.util.guice.CachingReflectionHelper;


/**
 * {@link BeanDefinitionParser} that sets up {@link DefaultBeanFactoryPointcutAdvisor}
 * instances to wrap {@link EhCacheInterceptor}s around {@link Cacheable}s and {@link TriggersRemove}
 * advised methods.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class AnnotationDrivenEhCacheBeanDefinitionParser implements BeanDefinitionParser {
    public static final String XSD_ATTR__CREATE_MISSING_CACHES = "create-missing-caches";
    public static final String XSD_ATTR__CACHE_MANAGER = "cache-manager";
    public static final String XSD_ATTR__DEFAULT_CACHE_KEY_GENERATOR = "default-cache-key-generator";
    public static final String XSD_ATTR__SELF_POPULATING_CACHE_SCOPE = "self-populating-cache-scope";

    public static final String EHCACHE_CACHING_ADVISOR_BEAN_NAME = AnnotationDrivenEhCacheBeanDefinitionParser.class.getPackage().getName() + ".internalEhCacheCachingAdvisor";
    
    public static final String DEFAULT_CACHE_KEY_GENERATOR = HashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME;
    public static final String CACHING_REFLECTION_HELPER_BEAN_NAME = CachingReflectionHelper.class.getName();
    
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
        if (!parserContext.getRegistry().containsBeanDefinition(EHCACHE_CACHING_ADVISOR_BEAN_NAME)) {
            final Object elementSource = parserContext.extractSource(element);
            
            this.setupCachingReflectionHelper(parserContext, elementSource);
            
            final RuntimeBeanReference defaultCacheKeyGeneratorReference = 
                this.setupDefaultCacheKeyGenerators(element, parserContext, elementSource);
            
            final RuntimeBeanReference cacheAttributeSourceReference = 
                this.setupCacheAttributeSource(element, parserContext, elementSource, defaultCacheKeyGeneratorReference);
            
            final RuntimeBeanReference pointcutReference = 
                this.setupPointcut(element, parserContext, elementSource, cacheAttributeSourceReference);
            
            final RuntimeBeanReference interceptorReference = 
                this.setupInterceptor(element, parserContext, elementSource, cacheAttributeSourceReference);
            
            this.setupPointcutAdvisor(element, parserContext, elementSource, pointcutReference, interceptorReference);
           
        }
        return null;
    }

    protected final void setupCachingReflectionHelper(ParserContext parserContext, Object elementSource) {
        final RootBeanDefinition defaultKeyGenerator = new RootBeanDefinition(CachingReflectionHelper.class);
        defaultKeyGenerator.setSource(elementSource);
        defaultKeyGenerator.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        
        final BeanDefinitionRegistry registry = parserContext.getRegistry();
        registry.registerBeanDefinition(CACHING_REFLECTION_HELPER_BEAN_NAME, defaultKeyGenerator);
    }

    /**
     * Setup the default cache key generator. 
     * 
     * @return A reference to the default cache key generator. Should never be null.
     */
    protected RuntimeBeanReference setupDefaultCacheKeyGenerators(Element element, ParserContext parserContext, Object elementSource) {
        //Register all of the default cache key generator types
        this.setupDefaultCacheKeyGenerator(ListCacheKeyGenerator.class, parserContext, elementSource);
        this.setupDefaultCacheKeyGenerator(HashCodeCacheKeyGenerator.class, parserContext, elementSource);
        this.setupDefaultCacheKeyGenerator(MessageDigestCacheKeyGenerator.class, parserContext, elementSource);
        this.setupDefaultCacheKeyGenerator(ReflectionHashCodeCacheKeyGenerator.class, parserContext, elementSource);
        this.setupDefaultCacheKeyGenerator(StringCacheKeyGenerator.class, parserContext, elementSource);
        
        //If the default cache key generator was specified simply return a bean reference for that
        final String defaultCacheKeyGeneratorName = element.getAttribute(XSD_ATTR__DEFAULT_CACHE_KEY_GENERATOR);
        if (StringUtils.hasLength(defaultCacheKeyGeneratorName)) {
            return new RuntimeBeanReference(defaultCacheKeyGeneratorName);
        }
        
        //Use the default name for the bean reference
        return new RuntimeBeanReference(DEFAULT_CACHE_KEY_GENERATOR);
    }

    /**
     * Utility API used to setup each of the default {@link CacheKeyGenerator} implementations. Requires
     * that the class has a static String field named DEFAULT_BEAN_NAME declared that is used for the bean
     * name. 
     */
    protected final void setupDefaultCacheKeyGenerator(Class<? extends CacheKeyGenerator<? extends Serializable>> generatorClass, ParserContext parserContext, Object elementSource) {
        final String generatorName;
        try {
            final Field field = generatorClass.getField("DEFAULT_BEAN_NAME");
            generatorName = (String)field.get(null);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not access static field 'DEFAULT_BEAN_NAME' on " + generatorClass + ". This field is required to be setup as a default CacheKeyGenerator", e);
        }
        
        final RootBeanDefinition defaultKeyGenerator = new RootBeanDefinition(generatorClass);
        defaultKeyGenerator.setSource(elementSource);
        defaultKeyGenerator.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        
        if (ReflectionHelperAware.class.isAssignableFrom(generatorClass)) {
            final RuntimeBeanReference cacheManagerReference = new RuntimeBeanReference(CACHING_REFLECTION_HELPER_BEAN_NAME);
            
            final MutablePropertyValues propertyValues = defaultKeyGenerator.getPropertyValues();
            propertyValues.addPropertyValue("reflectionHelper", cacheManagerReference);
        }
        
        final BeanDefinitionRegistry registry = parserContext.getRegistry();
        registry.registerBeanDefinition(generatorName, defaultKeyGenerator);
    }
    

    /**
     * Create a {@link CacheAttributeSource} bean that will be used by the advisor and interceptor
     * 
     * @return Reference to the {@link CacheAttributeSource}. Should never be null.
     */
    protected RuntimeBeanReference setupCacheAttributeSource(Element element, ParserContext parserContext, Object elementSource, RuntimeBeanReference defaultCacheKeyGenerator) {
        final RootBeanDefinition cacheAttributeSource = new RootBeanDefinition(CacheAttributeSourceImpl.class);
        cacheAttributeSource.setSource(elementSource);
        cacheAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        
        final MutablePropertyValues propertyValues = cacheAttributeSource.getPropertyValues();
        RuntimeBeanReference cacheManagerReference = new RuntimeBeanReference(element.getAttribute(XSD_ATTR__CACHE_MANAGER));
        propertyValues.addPropertyValue("cacheManager", cacheManagerReference);
        propertyValues.addPropertyValue("createCaches", Boolean.parseBoolean(element.getAttribute(XSD_ATTR__CREATE_MISSING_CACHES)));
        propertyValues.addPropertyValue("defaultCacheKeyGenerator", defaultCacheKeyGenerator);
        final String blockingCacheScope = element.getAttribute(XSD_ATTR__SELF_POPULATING_CACHE_SCOPE);
        if (blockingCacheScope != null) {
            propertyValues.addPropertyValue("selfPopulatingCacheScope", SelfPopulatingCacheScope.valueOf(blockingCacheScope.toUpperCase()));
        }

        final XmlReaderContext readerContext = parserContext.getReaderContext();
        final String cacheAttributeSourceBeanName = readerContext.registerWithGeneratedName(cacheAttributeSource);
        return new RuntimeBeanReference(cacheAttributeSourceBeanName);
    }

    /**
     * Create the {@link Pointcut} used to apply the caching interceptor
     * 
     * @return Reference to the {@link Pointcut}. Should never be null.
     */
    protected RuntimeBeanReference setupPointcut(Element element, ParserContext parserContext, Object elementSource, RuntimeBeanReference cacheAttributeSource) {
        final RootBeanDefinition pointcut = new RootBeanDefinition(CacheStaticMethodMatcherPointcut.class);
        pointcut.setSource(elementSource);
        pointcut.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        
        final MutablePropertyValues propertyValues = pointcut.getPropertyValues();
        propertyValues.addPropertyValue("cacheAttributeSource", cacheAttributeSource);
        
        final XmlReaderContext readerContext = parserContext.getReaderContext();
        final String pointcutBeanName = readerContext.registerWithGeneratedName(pointcut);
        return new RuntimeBeanReference(pointcutBeanName);
    }

    /**
     * Create {@link MethodInterceptor} that is applies the caching logic to advised methods.
     * 
     * @return Reference to the {@link MethodInterceptor}. Should never be null.
     */
    protected RuntimeBeanReference setupInterceptor(Element element, ParserContext parserContext, Object elementSource, RuntimeBeanReference cacheableAttributeSourceRuntimeReference) {
        final RootBeanDefinition interceptor = new RootBeanDefinition(EhCacheInterceptor.class);
        interceptor.setSource(elementSource);
        interceptor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        
        final MutablePropertyValues propertyValues = interceptor.getPropertyValues();
        propertyValues.addPropertyValue("cacheAttributeSource", cacheableAttributeSourceRuntimeReference);
        
        final XmlReaderContext readerContext = parserContext.getReaderContext();
        final String interceptorBeanName = readerContext.registerWithGeneratedName(interceptor);
        return new RuntimeBeanReference(interceptorBeanName);
    }

    /**
     * Create {@link PointcutAdvisor} that puts the {@link Pointcut} and {@link MethodInterceptor} together.
     * 
     * @return Reference to the {@link PointcutAdvisor}. Should never be null.
     */
    protected RuntimeBeanReference setupPointcutAdvisor(Element element, ParserContext parserContext, Object elementSource, RuntimeBeanReference cacheablePointcutBeanReference,  RuntimeBeanReference cachingInterceptorBeanReference) {
        final RootBeanDefinition pointcutAdvisor = new RootBeanDefinition(DefaultBeanFactoryPointcutAdvisor.class);
        pointcutAdvisor.setSource(elementSource);
        pointcutAdvisor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        final MutablePropertyValues propertyValues = pointcutAdvisor.getPropertyValues();
        propertyValues.addPropertyValue("adviceBeanName", cachingInterceptorBeanReference.getBeanName());
        propertyValues.addPropertyValue("pointcut", cacheablePointcutBeanReference);
        if (element.hasAttribute("order")) {
            propertyValues.addPropertyValue("order", element.getAttribute("order"));
        }
        
        final BeanDefinitionRegistry registry = parserContext.getRegistry();
        registry.registerBeanDefinition(EHCACHE_CACHING_ADVISOR_BEAN_NAME, pointcutAdvisor);
        return new RuntimeBeanReference(EHCACHE_CACHING_ADVISOR_BEAN_NAME);
    }

}
