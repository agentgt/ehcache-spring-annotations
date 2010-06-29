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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Nicholas Blair
 * @version $Id$
 */
public class SelfPopulatingTestImpl implements SelfPopulatingTestInterface {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private volatile int interfaceAnnotatedExceptionCachedCount = 0;
    private volatile int interfaceAnnotatedExceptionCachedThrowsCount = 0;
    private volatile int blockingAInvocationCount = 0;
    private volatile int blockingBInvocationCount = 0;
    private volatile int nonBlockingInvocationCount = 0;
    private CountDownLatch threadRunningLatch;
    private CountDownLatch proccedLatch;
    
    public void reset() {
        this.blockingAInvocationCount = 0;
        this.blockingBInvocationCount = 0;
        this.nonBlockingInvocationCount = 0;
    }

    public void setThreadRunningLatch(CountDownLatch threadRunningLatch) {
        this.threadRunningLatch = threadRunningLatch;
    }

    public void setProccedLatch(CountDownLatch proccedLatch) {
        this.proccedLatch = proccedLatch;
    }

    public String blockingA(String argument) {
        logger.trace("Enter blockingA({})", argument);
        threadRunningLatch.countDown();
        try {
            logger.trace("Waiting in blockingA({})", argument);
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        blockingAInvocationCount++;
        logger.trace("Returning from blockingA({})", argument);
        return "blockingA says: " + argument;
    }

    public String blockingB(String argument) {
        logger.trace("Enter blockingB({})", argument);
        threadRunningLatch.countDown();
        try {
            logger.trace("Waiting in blockingB({})", argument);
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        blockingBInvocationCount++;
        logger.trace("Returning from blockingB({})", argument);
        return "blockingB says: " + argument;
    }
    
    public String nonBlocking(String argument) {
        logger.trace("Enter nonBlocking({})", argument);
        threadRunningLatch.countDown();
        try {
            logger.trace("Waiting in nonBlocking({})", argument);
            proccedLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        nonBlockingInvocationCount++;
        logger.trace("Returning from nonBlocking({})", argument);
        return "nonBlocking says: " + argument;
    }
    
    public int getBlockingAInvocationCount() {
        return this.blockingAInvocationCount;
    }
    public int getBlockingBInvocationCount() {
        return this.blockingBInvocationCount;
    }
    public int getNonBlockingInvocationCount() {
        return this.nonBlockingInvocationCount;
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
