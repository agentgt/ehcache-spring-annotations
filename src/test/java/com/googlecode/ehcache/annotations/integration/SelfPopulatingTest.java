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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.ehcache.annotations.util.ThreadGroupRunner;

/**
 *
 * @author Nicholas Blair
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/selfPopulatingTestContext.xml")
public class SelfPopulatingTest {
    private SelfPopulatingTestInterface selfPopulatingTestInterface;

    /**
     * @param selfPopulatingTestInterface the selfPopulatingTestInterface to set
     */
    @Autowired
    public void setSelfPopulatingTestInterface(
            SelfPopulatingTestInterface selfPopulatingTestInterface) {
        this.selfPopulatingTestInterface = selfPopulatingTestInterface;
    }
    
    @Before
    public void testSetup() {
        this.selfPopulatingTestInterface.reset();
    }
    
    /**
     * Control case on a method with selfPopulating = false.
     * 
     * @throws Exception
     */
    @Test(timeout=1000)
    public void testSelfPopulatingFalse() throws Exception {
        final CountDownLatch threadRunningLatch = new CountDownLatch(2);
        final CountDownLatch proccedLatch = new CountDownLatch(1);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);
        
        Assert.assertEquals(0, this.selfPopulatingTestInterface.getBlockingBInvocationCount());
        
        final ThreadGroupRunner threadGroup = new ThreadGroupRunner("testSelfPopulatingFalse-", true);
        
        threadGroup.addTask(new Runnable() {	
            public void run() {
                selfPopulatingTestInterface.nonBlocking("test1");
            }
        });
        threadGroup.addTask(new Runnable() {	
            public void run() {
                selfPopulatingTestInterface.nonBlocking("test1");
            }
        });
        
        threadGroup.start();
        
        // wait for both threads to get going
        threadRunningLatch.await();
        
        // Let both threads complete
        proccedLatch.countDown();
        
        threadGroup.join();
        
        // verify 2 calls to methodB
        Assert.assertEquals(2, this.selfPopulatingTestInterface.getNonBlockingInvocationCount());
    }
    /**
     * Verify that setting selfPopulating=true will guarantee only 1 invocation
     * of the cached method.
     * 
     * @throws Exception
     */
    @Test(timeout=1000)
    public void testSelfPopulatingTrue() throws Exception {
        final CountDownLatch threadRunningLatch = new CountDownLatch(5);
        final CountDownLatch proccedLatch = new CountDownLatch(1);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);
        
        Assert.assertEquals(0, this.selfPopulatingTestInterface.getBlockingAInvocationCount());
        Assert.assertEquals(0, this.selfPopulatingTestInterface.getBlockingBInvocationCount());
        
        final ThreadGroupRunner threadGroup = new ThreadGroupRunner("testSelfPopulatingFalse-", true);
        
        // set up threads 
        threadGroup.addTask(new Runnable() {	
            public void run() {
                threadRunningLatch.countDown();
                selfPopulatingTestInterface.blockingA("test2");
            }
        });
        threadGroup.addTask(new Runnable() {	
            public void run() {
                threadRunningLatch.countDown();
                selfPopulatingTestInterface.blockingA("test2");
            }
        });
        threadGroup.addTask(new Runnable() { 
            public void run() {
                threadRunningLatch.countDown();
                selfPopulatingTestInterface.blockingB("test2");
            }
        });
        threadGroup.addTask(new Runnable() { 
            public void run() {
                threadRunningLatch.countDown();
                selfPopulatingTestInterface.blockingB("test2");
            }
        });
        
        
        threadGroup.start();
        
        // wait for all threads to get going
        threadRunningLatch.await();
        Thread.sleep(100);
        
        // Let both threads complete
        proccedLatch.countDown();
        
        threadGroup.join();
        
        // verify only 1 call between method A and method B
        Assert.assertEquals(1, this.selfPopulatingTestInterface.getBlockingAInvocationCount() + this.selfPopulatingTestInterface.getBlockingBInvocationCount());
    }

    
    @Test(timeout=1000)
    public void testExceptionCaching() {
        final CountDownLatch threadRunningLatch = new CountDownLatch(0);
        final CountDownLatch proccedLatch = new CountDownLatch(0);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);

        Assert.assertEquals(0, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        Assert.assertEquals("interfaceAnnotatedExceptionCached(false)", selfPopulatingTestInterface.interfaceAnnotatedExceptionCached(false));
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        Assert.assertEquals("interfaceAnnotatedExceptionCached(false)", selfPopulatingTestInterface.interfaceAnnotatedExceptionCached(false));
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            selfPopulatingTestInterface.interfaceAnnotatedExceptionCached(true);
            Assert.fail("interfaceAnnotatedExceptionCached(true) should have thrown an exception");
        }
        catch (RuntimeException re) {
            Assert.assertEquals("throwsException was true", re.getMessage());
        }
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            selfPopulatingTestInterface.interfaceAnnotatedExceptionCached(true);
            Assert.fail("interfaceAnnotatedExceptionCached(true) should have thrown an exception");
        }
        catch (RuntimeException re) {
            Assert.assertEquals("throwsException was true", re.getMessage());
        }
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(1, selfPopulatingTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
    }
}
