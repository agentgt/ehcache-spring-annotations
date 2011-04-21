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
import java.util.concurrent.CyclicBarrier;

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
@ContextConfiguration(locations = "/refreshingSelfPopulatingMethodTestContext.xml")
public class RefreshingSelfPopulatingTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private RefreshingSelfPopulatingTestInterface refreshingSelfPopulatingTestInterface;

    @Before
    public void testSetup() {
        this.refreshingSelfPopulatingTestInterface.reset();
    }
    
    /**
     * Verify that setting selfPopulating=true will guarantee only 1 invocation
     * of the cached method.
     * 
     * @throws Exception
     */
    @Test//(timeout=1000)
    public void testSelfPopulatingTrue() throws Exception {
        final CountDownLatch threadRunningLatch = new CountDownLatch(2);
        final CyclicBarrier proceedLatch = new CyclicBarrier(2);
        this.refreshingSelfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.refreshingSelfPopulatingTestInterface.setProccedLatch(proceedLatch);
        
        Assert.assertEquals(0, this.refreshingSelfPopulatingTestInterface.getBlockingAInvocationCount());
        
        final ThreadGroupRunner threadGroup = new ThreadGroupRunner("testSelfPopulatingTrue-", true);
        
        threadGroup.addTask(1, new Runnable() {	
            public void run() {
                threadRunningLatch.countDown();
                logger.trace("Calling blockingA(test2)");
                refreshingSelfPopulatingTestInterface.blockingA("test2");
            }
        });
        
        threadGroup.start();
        
        // wait for both threads to get going
        logger.trace("Waiting for threads to start");
        threadRunningLatch.await();
        
        // Let both threads complete
        logger.trace("Signal threads to proceed");
        proceedLatch.await();
        
        logger.trace("Waiting for threads to complete");
        threadGroup.join();
        
        // verify only 1 call between method A and method B
        Assert.assertEquals(1, this.refreshingSelfPopulatingTestInterface.getBlockingAInvocationCount());
        
        Thread.sleep(1500);
        proceedLatch.await();
        
        //Wait for the refresh thread to complete
        Thread.sleep(10);
        
        Assert.assertEquals(2, this.refreshingSelfPopulatingTestInterface.getBlockingAInvocationCount());
        
        
    }
}
