/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache;

/**
 * Test interface with intentionally no Cacheable annotations.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface PlainTestInterface {

	public String methodA(String arg);
	public String methodB(String arg);
	
	public int getaCallCount();
	public int getbCallCount();
}
