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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@ContextConfiguration(locations = "/selfPopulatingMethodTestContext.xml")
public class SelfPopulatingMethodTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
     * Verify that setting selfPopulating=true will guarantee only 1 invocation
     * of the cached method.
     * 
     * @throws Exception
     */
    @Test
    public void testSelfPopulatingTrue() throws Exception {
        final CountDownLatch threadRunningLatch = new CountDownLatch(6);
        final CountDownLatch proccedLatch = new CountDownLatch(1);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);
        
        Assert.assertEquals(0, this.selfPopulatingTestInterface.getBlockingAInvocationCount());
        Assert.assertEquals(0, this.selfPopulatingTestInterface.getBlockingBInvocationCount());
        
        final ThreadGroupRunner threadGroup = new ThreadGroupRunner("testSelfPopulatingTrue-", true);
        
        threadGroup.addTask(2, new Runnable() {	
            public void run() {
                threadRunningLatch.countDown();
                logger.trace("Calling blockingA(test2)");
                selfPopulatingTestInterface.blockingA("test2");
            }
        });
        threadGroup.addTask(2, new Runnable() { 
            public void run() {
                threadRunningLatch.countDown();
                logger.trace("Calling blockingB(test2)");
                selfPopulatingTestInterface.blockingB("test2");
            }
        });
        
        threadGroup.start();
        
        // wait for both threads to get going
        logger.trace("Waiting for threads to start");
        threadRunningLatch.await();
        
        // Let both threads complete
        logger.trace("Waiting for threads to signal returns");
        proccedLatch.countDown();
        
        logger.trace("Waiting for threads to complete");
        threadGroup.join();
        
        // verify only 1 call between method A and method B
        Assert.assertEquals(1, this.selfPopulatingTestInterface.getBlockingAInvocationCount());
        Assert.assertEquals(1, this.selfPopulatingTestInterface.getBlockingBInvocationCount());
    }
}
