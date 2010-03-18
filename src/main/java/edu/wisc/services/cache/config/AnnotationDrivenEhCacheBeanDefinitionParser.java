/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import edu.wisc.services.cache.annotations.CacheStaticMethodMatcherPointcut;
import edu.wisc.services.cache.impl.CacheableAttributeSourceImpl;
import edu.wisc.services.cache.interceptor.caching.CachingInterceptor;
import edu.wisc.services.cache.key.HashCodeCacheKeyGenerator;

/**
 * {@link BeanDefinitionParser} t
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class AnnotationDrivenEhCacheBeanDefinitionParser implements BeanDefinitionParser {

    public static final String EHCACHE_ADVISOR_BEAN_NAME = "edu.wisc.services.cache.config.internalEhCacheAdvisor";
    
    public static final String DEFAULT_CACHE_KEY_GENERATOR = HashCodeCacheKeyGenerator.class.getName() + "_DEFFAULT";
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
        if (!parserContext.getRegistry().containsBeanDefinition(EHCACHE_ADVISOR_BEAN_NAME)) {
            Object elementSource = parserContext.extractSource(element);
            
            RootBeanDefinition cacheableAttributeSource = new RootBeanDefinition(CacheableAttributeSourceImpl.class);
            cacheableAttributeSource.setSource(elementSource);
            cacheableAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String cacheableAttributeSourceBeanName = parserContext.getReaderContext().registerWithGeneratedName(cacheableAttributeSource);
            RuntimeBeanReference cacheableAttributeSourceRuntimeReference = new RuntimeBeanReference(cacheableAttributeSourceBeanName);
            
            RootBeanDefinition cacheablePointcutSource = new RootBeanDefinition(CacheStaticMethodMatcherPointcut.class);
            cacheablePointcutSource.setSource(elementSource);
            cacheablePointcutSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            
            cacheablePointcutSource.getPropertyValues().add("cacheableAttributeSource", cacheableAttributeSourceRuntimeReference);
            String cacheablePointcutBeanName = parserContext.getReaderContext().registerWithGeneratedName(cacheablePointcutSource);
            
            RootBeanDefinition cachingInterceptorSource = new RootBeanDefinition(CachingInterceptor.class);
            cachingInterceptorSource.setSource(elementSource);
            cachingInterceptorSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            cachingInterceptorSource.getPropertyValues().add("cacheableAttributeSource", cacheableAttributeSourceRuntimeReference);
            cachingInterceptorSource.getPropertyValues().add("cacheManagerBeanName", element.getAttribute("cache-manager"));
            cachingInterceptorSource.getPropertyValues().add("createCaches", Boolean.parseBoolean(element.getAttribute("create-missing-caches")));
            String cachingInterceptorBeanName = parserContext.getReaderContext().registerWithGeneratedName(cachingInterceptorSource);
            
            RootBeanDefinition pointcutAdvisorSource = new RootBeanDefinition(DefaultBeanFactoryPointcutAdvisor.class);
            pointcutAdvisorSource.setSource(elementSource);
            pointcutAdvisorSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            pointcutAdvisorSource.getPropertyValues().add("adviceBeanName", cachingInterceptorBeanName);
            pointcutAdvisorSource.getPropertyValues().add("pointcut", new RuntimeBeanReference(cacheablePointcutBeanName));
            
            parserContext.getRegistry().registerBeanDefinition(EHCACHE_ADVISOR_BEAN_NAME, pointcutAdvisorSource);
            
            
            final RootBeanDefinition defaultKeyGenerator = new RootBeanDefinition(HashCodeCacheKeyGenerator.class);
            defaultKeyGenerator.setSource(elementSource);
            defaultKeyGenerator.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            
            parserContext.getRegistry().registerBeanDefinition(DEFAULT_CACHE_KEY_GENERATOR, defaultKeyGenerator);
            
        }
        return null;
    }

}
