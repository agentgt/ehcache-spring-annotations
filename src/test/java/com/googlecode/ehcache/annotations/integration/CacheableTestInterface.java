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

package com.googlecode.ehcache.annotations.integration;

import java.util.concurrent.TimeUnit;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableTestInterface {
    
    @Cacheable(cacheName="interfaceAnnotatedCache", exceptionCacheName="interfaceAnnotatedExceptionCache")
    public String interfaceAnnotatedExceptionCached(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedCount();
    public int interfaceAnnotatedExceptionCachedThrowsCount();
    
    @Cacheable(cacheName="interfaceAnnotatedCached")
    public String interfaceAnnotatedCached(int argument);
    public int interfaceAnnotatedCachedCount();
    
    @Cacheable(cacheName="interfaceAnnotatedNoArgCached")
    public String interfaceAnnotatedNoArgCached();
    public int interfaceAnnotatedNoArgCachedCount();
    
    public String interfaceDefined(String argument);
    public int interfaceDefinedCount();
    
    @Cacheable(cacheName="interfaceAnnotatedEnumArgCached")
    public String enumParameter(TimeUnit timeUnit);
    public int enumParameterCount();
    
    @Cacheable(cacheName="interfaceAnnotatedArrayReturn")
    public String[] arrayReturn(String a, String b);
    public int arrayReturnCount();
    
}