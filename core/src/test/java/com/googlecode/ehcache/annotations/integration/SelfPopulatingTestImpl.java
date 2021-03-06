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
package com.googlecode.ehcache.annotations.integration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Nicholas Blair
 * @version $Id$
 */
public class SelfPopulatingTestImpl implements SelfPopulatingTestInterface {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final AtomicInteger interfaceAnnotatedExceptionCachedCount = new AtomicInteger(0);
    private final AtomicInteger interfaceAnnotatedExceptionCachedThrowsCount = new AtomicInteger(0);
    private final AtomicInteger blockingAInvocationCount = new AtomicInteger(0);
    private final AtomicInteger blockingBInvocationCount = new AtomicInteger(0);
    private final AtomicInteger nonBlockingInvocationCount = new AtomicInteger(0);
    private CountDownLatch threadRunningLatch;
    private CountDownLatch proccedLatch;
    
    public void reset() {
        this.blockingAInvocationCount.set(0);
        this.blockingBInvocationCount.set(0);
        this.nonBlockingInvocationCount.set(0);
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
        blockingAInvocationCount.incrementAndGet();
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
        blockingBInvocationCount.incrementAndGet();
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
        nonBlockingInvocationCount.incrementAndGet();
        logger.trace("Returning from nonBlocking({})", argument);
        return "nonBlocking says: " + argument;
    }
    
    public int getBlockingAInvocationCount() {
        return this.blockingAInvocationCount.get();
    }
    public int getBlockingBInvocationCount() {
        return this.blockingBInvocationCount.get();
    }
    public int getNonBlockingInvocationCount() {
        return this.nonBlockingInvocationCount.get();
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
            this.interfaceAnnotatedExceptionCachedThrowsCount.incrementAndGet();
            throw new RuntimeException("throwsException was true");
        }
        
        this.interfaceAnnotatedExceptionCachedCount.incrementAndGet();
        return "interfaceAnnotatedExceptionCached(" + throwsException + ")";
    }

    public int interfaceAnnotatedExceptionCachedCount() {
        return this.interfaceAnnotatedExceptionCachedCount.get();
    }

    public int interfaceAnnotatedExceptionCachedThrowsCount() {
        return this.interfaceAnnotatedExceptionCachedThrowsCount.get();
    }
}
