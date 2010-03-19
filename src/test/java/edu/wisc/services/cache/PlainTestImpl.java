/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.Cacheable;

/**
 * Has a {@link Cacheable} annotation on {@link PlainTestImpl#methodB()}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class PlainTestImpl implements PlainTestInterface {

	private int aCallCount = 0;
	private int bCallCount = 0;
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodA(java.lang.String)
	 */
	@Override
	public String methodA(String arg) {
		aCallCount++;
		return "methodA says " + arg;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodB(java.lang.String)
	 */
	@Override
	@Cacheable(cacheName="cachedResource")
	public String methodB(String arg) {
		bCallCount++;
		return "methodB says " + arg;
	}

	/**
	 * @return the aCallCount
	 */
	@Override
	public int getaCallCount() {
		return aCallCount;
	}

	/**
	 * @return the bCallCount
	 */
	@Override
	public int getbCallCount() {
		return bCallCount;
	}

}
