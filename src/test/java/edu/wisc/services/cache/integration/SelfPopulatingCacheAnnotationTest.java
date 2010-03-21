/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.integration;

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
public class SelfPopulatingCacheAnnotationTest {

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
	    final CountDownLatch startLatch = new CountDownLatch(2);
        this.selfPopulatingTestInterface.setStartSignal(startLatch);
	    
		Assert.assertEquals(0, this.selfPopulatingTestInterface.getBInvocationCount());
		
		// set up 2 threads 
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				selfPopulatingTestInterface.methodB("foo");
			}
		});
		Thread t2 = new Thread(new Runnable() {	
			@Override
			public void run() {
				selfPopulatingTestInterface.methodB("foo");
			}
		});
		
		t1.setDaemon(true);
		t2.setDaemon(true);
		
		t1.start();
		t2.start();
		
		// wait for both threads to get going
		startLatch.await();
		
		this.selfPopulatingTestInterface.submitNotifyAll();
		
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
        final CountDownLatch startLatch = new CountDownLatch(3);
        this.selfPopulatingTestInterface.setStartSignal(startLatch);
        
		Assert.assertEquals(0, this.selfPopulatingTestInterface.getAInvocationCount());
		
		// set up 2 threads 
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
			    startLatch.countDown();
				selfPopulatingTestInterface.methodA("foo");
			}
		});
		Thread t2 = new Thread(new Runnable() {	
			@Override
			public void run() {
			    startLatch.countDown();
				selfPopulatingTestInterface.methodA("foo");
			}
		});
		
		t1.setDaemon(true);
		t2.setDaemon(true);
		
		t1.start();
		t2.start();
		
		// wait for both threads to get going
        startLatch.await();
		
		this.selfPopulatingTestInterface.submitNotifyAll();
		
		t1.join();
		t2.join();
		
		// verify only 1 call to methodA
		Assert.assertEquals(1, this.selfPopulatingTestInterface.getAInvocationCount());
	}
	
}
