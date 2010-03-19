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
import java.util.concurrent.Callable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wisc.services.cache.CacheableAttribute;
import edu.wisc.services.cache.CacheableAttributeSource;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * TODO object versus serializable caching
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CachingInterceptor implements MethodInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private CacheableAttributeSource cacheableAttributeSource;
    

    public void setCacheableAttributeSource(CacheableAttributeSource cacheableAttributeSource) {
        this.cacheableAttributeSource = cacheableAttributeSource;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final CacheableAttribute cacheableAttribute = this.cacheableAttributeSource.getCacheableAttribute(methodInvocation.getMethod(), methodInvocation.getClass());
        if (cacheableAttribute == null) {
            this.logger.trace("Don't need to cache [{}]: This method isn't cacheable.", methodInvocation);
            return methodInvocation.proceed();
        }
        
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
}
