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
import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.annotations.TriggersRemoveStaticMethodMatcherPointcut;
import edu.wisc.services.cache.impl.CacheableAttributeSourceImpl;
import edu.wisc.services.cache.impl.TriggersRemoveAttributeSourceImpl;
import edu.wisc.services.cache.interceptor.caching.CachingInterceptor;
import edu.wisc.services.cache.interceptor.caching.TriggersRemoveInterceptor;
import edu.wisc.services.cache.key.HashCodeCacheKeyGenerator;

/**
 * {@link BeanDefinitionParser} that sets up {@link DefaultBeanFactoryPointcutAdvisor}
 * instances to wrap {@link CachingInterceptor}s around {@link Cacheable}s and
 * {@link TriggersRemoveInterceptor}s around {@link TriggersRemove}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class AnnotationDrivenEhCacheBeanDefinitionParser implements BeanDefinitionParser {

    public static final String EHCACHE_CACHING_ADVISOR_BEAN_NAME = "edu.wisc.services.cache.config.internalEhCacheCachingAdvisor";
    public static final String EHCACHE_TRIGGERS_REMOVE_ADVISOR_BEAN_NAME = "edu.wisc.services.cache.config.internalEhCacheTriggersRemoveAdvisor";
    
    public static final String DEFAULT_CACHE_KEY_GENERATOR = HashCodeCacheKeyGenerator.class.getName() + "_DEFFAULT";
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
        if (!parserContext.getRegistry().containsBeanDefinition(EHCACHE_CACHING_ADVISOR_BEAN_NAME)) {
            Object elementSource = parserContext.extractSource(element);
            
            RootBeanDefinition cacheableAttributeSource = new RootBeanDefinition(CacheableAttributeSourceImpl.class);
            cacheableAttributeSource.setSource(elementSource);
            cacheableAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            cacheableAttributeSource.getPropertyValues().add("cacheManagerBeanName", element.getAttribute("cache-manager"));
            cacheableAttributeSource.getPropertyValues().add("createCaches", Boolean.parseBoolean(element.getAttribute("create-missing-caches")));
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
            String cachingInterceptorBeanName = parserContext.getReaderContext().registerWithGeneratedName(cachingInterceptorSource);
            
            
            RootBeanDefinition cachingPointcutAdvisorSource = new RootBeanDefinition(DefaultBeanFactoryPointcutAdvisor.class);
            cachingPointcutAdvisorSource.setSource(elementSource);
            cachingPointcutAdvisorSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            cachingPointcutAdvisorSource.getPropertyValues().add("adviceBeanName", cachingInterceptorBeanName);
            cachingPointcutAdvisorSource.getPropertyValues().add("pointcut", new RuntimeBeanReference(cacheablePointcutBeanName));
            parserContext.getRegistry().registerBeanDefinition(EHCACHE_CACHING_ADVISOR_BEAN_NAME, cachingPointcutAdvisorSource);
            
            
            final RootBeanDefinition defaultKeyGenerator = new RootBeanDefinition(HashCodeCacheKeyGenerator.class);
            defaultKeyGenerator.setSource(elementSource);
            defaultKeyGenerator.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            parserContext.getRegistry().registerBeanDefinition(DEFAULT_CACHE_KEY_GENERATOR, defaultKeyGenerator);
           
        }
        
        if (!parserContext.getRegistry().containsBeanDefinition(EHCACHE_TRIGGERS_REMOVE_ADVISOR_BEAN_NAME)) {
        	Object elementSource = parserContext.extractSource(element);
        	
        	RootBeanDefinition flushableAttributeSource = new RootBeanDefinition(TriggersRemoveAttributeSourceImpl.class);
            flushableAttributeSource.setSource(elementSource);
            flushableAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String flushableAttributeSourceBeanName = parserContext.getReaderContext().registerWithGeneratedName(flushableAttributeSource);
            RuntimeBeanReference flushableAttributeSourceRuntimeReference = new RuntimeBeanReference(flushableAttributeSourceBeanName);
            
            RootBeanDefinition flushablePointcutSource = new RootBeanDefinition(TriggersRemoveStaticMethodMatcherPointcut.class);
            flushablePointcutSource.setSource(elementSource);
            flushablePointcutSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            flushablePointcutSource.getPropertyValues().add("flushableAttributeSource", flushableAttributeSourceRuntimeReference);
            String flushablePointcutBeanName = parserContext.getReaderContext().registerWithGeneratedName(flushablePointcutSource);
            
            RootBeanDefinition flushingInterceptorSource = new RootBeanDefinition(TriggersRemoveInterceptor.class);
            flushingInterceptorSource.setSource(elementSource);
            flushingInterceptorSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            flushingInterceptorSource.getPropertyValues().add("flushableAttributeSource", flushableAttributeSourceRuntimeReference);
            String flushingInterceptorBeanName = parserContext.getReaderContext().registerWithGeneratedName(flushingInterceptorSource);
            
            RootBeanDefinition flushingPointcutAdvisorSource = new RootBeanDefinition(DefaultBeanFactoryPointcutAdvisor.class);
            flushingPointcutAdvisorSource.setSource(elementSource);
            flushingPointcutAdvisorSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            flushingPointcutAdvisorSource.getPropertyValues().add("adviceBeanName", flushingInterceptorBeanName);
            flushingPointcutAdvisorSource.getPropertyValues().add("pointcut", new RuntimeBeanReference(flushablePointcutBeanName));
            parserContext.getRegistry().registerBeanDefinition(EHCACHE_TRIGGERS_REMOVE_ADVISOR_BEAN_NAME, flushingPointcutAdvisorSource);
        }
        return null;
    }

}
