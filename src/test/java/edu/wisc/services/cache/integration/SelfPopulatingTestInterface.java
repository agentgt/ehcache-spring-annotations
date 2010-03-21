/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.integration;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface SelfPopulatingTestInterface {
    
    public void setStartSignal(CountDownLatch startSignal);

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
	
	public void submitNotifyAll();
}
