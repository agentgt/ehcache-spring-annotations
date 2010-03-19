/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache;

import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class TransactionalTestImpl implements TransactionalTestInterface {

	private int aCallCount = 0;
	private int bCallCount = 0;
	
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#getaCallCount()
	 */
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TransactionalTestInterface#getaCallCount()
	 */
	@Override
	public int getaCallCount() {
		return this.aCallCount;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#getbCallCount()
	 */
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TransactionalTestInterface#getbCallCount()
	 */
	@Override
	public int getbCallCount() {
		return this.bCallCount;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodA(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TransactionalTestInterface#methodA(java.lang.String)
	 */
	@Override
	public String methodA(String arg) {
		aCallCount++;
		return "methodA says " + arg;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodB(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TransactionalTestInterface#methodB(java.lang.String)
	 */
	@Override
	@Transactional
	public String methodB(String arg) {
		bCallCount++;
		return "methodB says " + arg;
	}

}
