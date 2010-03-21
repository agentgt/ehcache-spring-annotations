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
public interface SelfPopulatingTestInterface {
    
    public void setThreadRunningLatch(CountDownLatch threadRunningLatch);

    public void setProccedLatch(CountDownLatch proccedLatch);

	/**
	 * Will be marked Cacheable with selfPopulating=true
	 * @param argument
	 */
	public String methodA(String argument);
	/**
	 * Will be marked Cacheable with selfPopulating=false
	 * @param argument
	 */
	public String methodB(String argument);
	public int getAInvocationCount();
	public int getBInvocationCount();
	
    @Cacheable(cacheName="interfaceAnnotatedCache", exceptionCacheName="interfaceAnnotatedExceptionCache")
    public String interfaceAnnotatedExceptionCached(boolean throwsException);
    public int interfaceAnnotatedExceptionCachedCount();
    public int interfaceAnnotatedExceptionCachedThrowsCount();
}
