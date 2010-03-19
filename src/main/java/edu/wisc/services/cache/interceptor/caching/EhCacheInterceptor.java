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

package edu.wisc.services.cache.interceptor.caching;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wisc.services.cache.AdviceType;
import edu.wisc.services.cache.CacheAttributeSource;
import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * TODO object versus serializable caching
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
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Method method = methodInvocation.getMethod();
        final Class<?> targetClass = (methodInvocation.getThis() != null ? methodInvocation.getThis().getClass() : null);
        final AdviceType adviceType = this.cacheAttributeSource.getAdviceType(method, targetClass);
        
        switch (adviceType) {
            case CACHE: {
                final CacheableAttribute cacheableAttribute = this.cacheAttributeSource.getCacheableAttribute(method, targetClass);
                if (cacheableAttribute == null) {
                    this.logger.warn("TODO");
                    return methodInvocation.proceed();
                }
                
                return this.invokeCacheable(methodInvocation, cacheableAttribute);

            }

            case REMOVE: {
                final TriggersRemoveAttribute triggersRemoveAttribute = this.cacheAttributeSource.getTriggersRemoveAttribute(method, targetClass);
                if (triggersRemoveAttribute == null) {
                    this.logger.warn("TODO");
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
    
    private Object invokeCacheable(final MethodInvocation methodInvocation, final CacheableAttribute cacheableAttribute) throws Throwable {
        //Generate the cache key
        final CacheKeyGenerator cacheKeyGenerator = cacheableAttribute.getCacheKeyGenerator();
        final Serializable key = cacheKeyGenerator.generateKey(methodInvocation);
        
        final ThreadLocal<Callable<?>> entryFactory = cacheableAttribute.getEntryFactory();
        if (entryFactory != null) {
            //Determine if exception caching is enabled
            final Ehcache exceptionCache = cacheableAttribute.getExceptionCache();
            if (exceptionCache != null) {
                //See if there is a cached exception
                final Element execptionElement = exceptionCache.get(key);
                if (execptionElement != null) {
                    throw (Throwable)execptionElement.getObjectValue();
                }
            }
            
            //Setup the Callable
            entryFactory.set(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
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
            });

            //SelfPopulating cache, the get should always return an Element
            final Ehcache cache = cacheableAttribute.getCache();
            final Element element;
            try {
                element = cache.get(key);
            }
            catch (Throwable t) {
                //If exception caching, cache the exception
                if (exceptionCache != null) {
                    exceptionCache.put(new Element(key, t));
                }
                
                throw t;
            }
            finally {
                entryFactory.remove();
            }
            
            if (element == null) {
                throw new RuntimeException("This should not be possible, a self-populating cache should always return an Element");
            }
            
            return element.getObjectValue();
        }
        else {
            //See if there is a cached result
            final Ehcache cache = cacheableAttribute.getCache();
            final Element element = cache.get(key);
            if (element != null) {
                return element.getObjectValue();
            }
    
            //Determine if exception caching is enabled
            final Ehcache exceptionCache = cacheableAttribute.getExceptionCache();
            if (exceptionCache != null) {
                //See if there is a cached exception
                final Element execptionElement = exceptionCache.get(key);
                if (execptionElement != null) {
                    throw (Throwable)execptionElement.getObjectValue();
                }
            }
    
            //No cached value or exception, proceed
            final Object value;
            try {
                value = methodInvocation.proceed();
            }
            catch (Throwable t) {
                //If exception caching, cache the exception
                if (exceptionCache != null) {
                    exceptionCache.put(new Element(key, t));
                }
                
                throw t;
            }
            
            //Cache and return the value
            cache.put(new Element(key, value));
            return value;
        }
    }
    
    private Object invokeTriggersRemove(final MethodInvocation methodInvocation, final TriggersRemoveAttribute triggersRemoveAttribute) throws Throwable {
        Ehcache cache = triggersRemoveAttribute.getCache();
        if (triggersRemoveAttribute.isRemoveAll()) {
            cache.removeAll();
        }
        else {
            CacheKeyGenerator cacheKeyGenerator = triggersRemoveAttribute.getCacheKeyGenerator();
            Serializable cacheKey = cacheKeyGenerator.generateKey(methodInvocation);
            cache.remove(cacheKey);
        }

        Object result = methodInvocation.proceed();
        return result;
    }
}
