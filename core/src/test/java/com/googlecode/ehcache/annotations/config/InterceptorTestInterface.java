/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface InterceptorTestInterface {
    
    @Cacheable(cacheName="interfaceAnnotatedCache")
    public String interfaceAnnotatedCached(boolean throwsException);
    public int interfaceAnnotatedCachedCount();
    public int interfaceAnnotatedCachedThrowsCount();
    
    @Cacheable(cacheName="interfaceAnnotatedCache", exceptionCacheName="interfaceAnnotatedExceptionCache")
    public String interfaceAnnotatedExceptionCached(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedCount();
    public int interfaceAnnotatedExceptionCachedThrowsCount();
    
    @TriggersRemove(cacheName="interfaceAnnotatedCached")
    public String interfaceAnnotatedTriggersRemove(int argument);
    
    @TriggersRemove(cacheName="interfaceAnnotatedCached", removeAll=true)
    public String interfaceAnnotatedTriggersRemoveAll(int argument);
    
    
    @Cacheable(cacheName="interfaceAnnotatedCache", cacheableInteceptorName="customCacheableInterceptor")
    public String interfaceAnnotatedCachedInterceptor(boolean throwsException);
    public int interfaceAnnotatedCachedInterceptorCount();
    public int interfaceAnnotatedCachedInterceptorThrowsCount();
    
    @Cacheable(cacheName="interfaceAnnotatedCache", exceptionCacheName="interfaceAnnotatedExceptionCache", cacheableInteceptorName="customCacheableInterceptor")
    public String interfaceAnnotatedExceptionCachedInterceptor(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedInterceptorCount();
    public int interfaceAnnotatedExceptionCachedInterceptorThrowsCount();
    
    @TriggersRemove(cacheName="interfaceAnnotatedCached", triggersRemoveInteceptorName="customTriggersRemoveInterceptor")
    public String interfaceAnnotatedTriggersRemoveInterceptor(int argument);
    
    @TriggersRemove(cacheName="interfaceAnnotatedCached", removeAll=true, triggersRemoveInteceptorName="customTriggersRemoveInterceptor")
    public String interfaceAnnotatedTriggersRemoveAllInterceptor(int argument);
}