/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.integration;

import java.util.concurrent.CountDownLatch;

import edu.wisc.services.cache.annotations.Cacheable;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class SelfPopulatingTestImpl implements SelfPopulatingTestInterface {

	private final Object lock = new Object();
	private volatile int aInvocationCount = 0;
	private volatile int bInvocationCount = 0;
	private CountDownLatch startSignal;
	
	public void setStartSignal(CountDownLatch startSignal) {
        this.startSignal = startSignal;
    }

    @Override
	@Cacheable(cacheName="blockingCache", selfPopulating=true)
	public String methodA(String argument) {
	    startSignal.countDown();
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		aInvocationCount++;
		return "methodA says: " + argument;
	}
	
	@Override
	@Cacheable(cacheName="blockingCache", selfPopulating=false)
	public String methodB(String argument) {
        startSignal.countDown();
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		bInvocationCount++;
		return "methodB says: " + argument;
	}
	
	@Override
	public int getAInvocationCount() {
		return this.aInvocationCount;
	}
	@Override
	public int getBInvocationCount() {
		return this.bInvocationCount;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.SelfPopulatingTestInterface#submitNotifyAll()
	 */
	@Override
	public void submitNotifyAll() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
}
