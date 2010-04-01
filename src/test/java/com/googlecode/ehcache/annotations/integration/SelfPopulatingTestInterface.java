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

import java.util.concurrent.CountDownLatch;

import com.googlecode.ehcache.annotations.Cacheable;


/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface SelfPopulatingTestInterface {
    
    public void reset();
    
    public void setThreadRunningLatch(CountDownLatch threadRunningLatch);

    public void setProccedLatch(CountDownLatch proccedLatch);

    @Cacheable(cacheName="blockingCache", selfPopulating=true)
    public String blockingA(String argument);
    public int getBlockingAInvocationCount();
    
    @Cacheable(cacheName="blockingCache", selfPopulating=true)
    public String blockingB(String argument);
    public int getBlockingBInvocationCount();
    
    @Cacheable(cacheName="blockingCache", selfPopulating=false)
    public String nonBlocking(String argument);
    public int getNonBlockingInvocationCount();
	
    @Cacheable(cacheName="interfaceAnnotatedCache", selfPopulating=true, exceptionCacheName="interfaceAnnotatedExceptionCache")
    public String interfaceAnnotatedExceptionCached(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedCount();
    public int interfaceAnnotatedExceptionCachedThrowsCount();
}
