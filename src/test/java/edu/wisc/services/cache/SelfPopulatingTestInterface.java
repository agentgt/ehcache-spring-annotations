/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface SelfPopulatingTestInterface {

	/**
	 * Will be marked Cacheable with selfPopulating=true
	 * @param argument
	 * @return
	 */
	public String methodA(String argument);
	/**
	 * Will be marked Cacheable with selfPopulating=false
	 * @param argument
	 * @return
	 */
	public String methodB(String argument);
	public int getAInvocationCount();
	public int getBInvocationCount();
	
	public void submitNotifyAll();
}
