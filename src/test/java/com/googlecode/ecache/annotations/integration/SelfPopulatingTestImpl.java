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
public class SelfPopulatingTestImpl implements SelfPopulatingTestInterface {
    private volatile int interfaceAnnotatedExceptionCachedCount = 0;
    private volatile int interfaceAnnotatedExceptionCachedThrowsCount = 0;
	private volatile int aInvocationCount = 0;
	private volatile int bInvocationCount = 0;
	private CountDownLatch threadRunningLatch;
	private CountDownLatch proccedLatch;
	
    public void setThreadRunningLatch(CountDownLatch threadRunningLatch) {
        this.threadRunningLatch = threadRunningLatch;
    }

    public void setProccedLatch(CountDownLatch proccedLatch) {
        this.proccedLatch = proccedLatch;
    }

	@Cacheable(cacheName="blockingCache", selfPopulating=true)
	public String methodA(String argument) {
	    threadRunningLatch.countDown();
	    try {
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        aInvocationCount++;
		return "methodA says: " + argument;
	}
	
	@Cacheable(cacheName="blockingCache", selfPopulating=false)
	public String methodB(String argument) {
	    threadRunningLatch.countDown();
        try {
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
		bInvocationCount++;
		return "methodB says: " + argument;
	}
	
	public int getAInvocationCount() {
		return this.aInvocationCount;
	}
	public int getBInvocationCount() {
		return this.bInvocationCount;
	}
	
    public String interfaceAnnotatedExceptionCached(boolean throwsException) {
        threadRunningLatch.countDown();
        try {
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        if (throwsException) {
            this.interfaceAnnotatedExceptionCachedThrowsCount++;
            throw new RuntimeException("throwsException was true");
        }
        
        this.interfaceAnnotatedExceptionCachedCount++;
        return "interfaceAnnotatedExceptionCached(" + throwsException + ")";
    }

    public int interfaceAnnotatedExceptionCachedCount() {
        return this.interfaceAnnotatedExceptionCachedCount;
    }

    public int interfaceAnnotatedExceptionCachedThrowsCount() {
        return this.interfaceAnnotatedExceptionCachedThrowsCount;
    }
}
