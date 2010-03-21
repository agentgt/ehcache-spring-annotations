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

    @Override
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
	
	@Override
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
	
	@Override
	public int getAInvocationCount() {
		return this.aInvocationCount;
	}
	@Override
	public int getBInvocationCount() {
		return this.bInvocationCount;
	}
	
    @Override
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

    @Override
    public int interfaceAnnotatedExceptionCachedCount() {
        return this.interfaceAnnotatedExceptionCachedCount;
    }

    @Override
    public int interfaceAnnotatedExceptionCachedThrowsCount() {
        return this.interfaceAnnotatedExceptionCachedThrowsCount;
    }
}
