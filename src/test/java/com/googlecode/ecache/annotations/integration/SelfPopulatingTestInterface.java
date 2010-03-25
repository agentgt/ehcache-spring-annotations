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

package com.googlecode.ecache.annotations.integration;

import java.util.concurrent.CountDownLatch;

import com.googlecode.ecache.annotations.Cacheable;


/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface SelfPopulatingTestInterface {
    
    public void setThreadRunningLatch(CountDownLatch threadRunningLatch);

    public void setProccedLatch(CountDownLatch proccedLatch);

	/**
	 * Will be marked Cacheable with selfPopulating=true
	 * @param argument
	 */
	public String methodA(String argument);
	/**
	 * Will be marked Cacheable with selfPopulating=false
	 * @param argument
	 */
	public String methodB(String argument);
	public int getAInvocationCount();
	public int getBInvocationCount();
	
    @Cacheable(cacheName="interfaceAnnotatedCache", selfPopulating=true, exceptionCacheName="interfaceAnnotatedExceptionCache")
    public String interfaceAnnotatedExceptionCached(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedCount();
    public int interfaceAnnotatedExceptionCachedThrowsCount();
}
