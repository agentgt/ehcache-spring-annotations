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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
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
	
	/**
	 * Control case on a method with selfPopulating = false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelfPopulatingFalse() throws Exception {
	    final CountDownLatch threadRunningLatch = new CountDownLatch(2);
	    final CountDownLatch proccedLatch = new CountDownLatch(1);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);
	    
		Assert.assertEquals(0, this.selfPopulatingTestInterface.getBInvocationCount());
		
		// set up 2 threads 
		Thread t1 = new Thread(new Runnable() {	
			public void run() {
				selfPopulatingTestInterface.methodB("foo");
			}
		});
		Thread t2 = new Thread(new Runnable() {	
			public void run() {
				selfPopulatingTestInterface.methodB("foo");
			}
		});
		
		t1.setDaemon(true);
		t2.setDaemon(true);
		
		t1.start();
		t2.start();
		
		// wait for both threads to get going
		threadRunningLatch.await();
		
		// Let both threads complete
		proccedLatch.countDown();
		
		t1.join();
		t2.join();
		
		// verify 2 calls to methodB
		Assert.assertEquals(2, this.selfPopulatingTestInterface.getBInvocationCount());
	}
	/**
	 * Verify that setting selfPopulating=true will guarantee only 1 invocation
	 * of the cached method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelfPopulatingTrue() throws Exception {
        final CountDownLatch threadRunningLatch = new CountDownLatch(3);
        final CountDownLatch proccedLatch = new CountDownLatch(1);
        this.selfPopulatingTestInterface.setThreadRunningLatch(threadRunningLatch);
        this.selfPopulatingTestInterface.setProccedLatch(proccedLatch);
        
		Assert.assertEquals(0, this.selfPopulatingTestInterface.getAInvocationCount());
		
		// set up 2 threads 
		Thread t1 = new Thread(new Runnable() {	
			public void run() {
			    threadRunningLatch.countDown();
				selfPopulatingTestInterface.methodA("foo");
			}
		}, "t1");
		Thread t2 = new Thread(new Runnable() {	
			public void run() {
			    threadRunningLatch.countDown();
				selfPopulatingTestInterface.methodA("foo");
			}
		}, "t2");
		
		t1.setDaemon(true);
		t2.setDaemon(true);
		
		t1.start();
		t2.start();
        
        // wait for both threads to get going
        threadRunningLatch.await();
        
        //TODO is a sleep needed here? The Latch doesn't guarantee that the second thread is actually waiting within the self populating ehcache decorator
        
        // Let both threads complete
        proccedLatch.countDown();
        
		t1.join();
		t2.join();
		
		// verify only 1 call to methodA
		Assert.assertEquals(1, this.selfPopulatingTestInterface.getAInvocationCount());
	}

	
    @Test
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
