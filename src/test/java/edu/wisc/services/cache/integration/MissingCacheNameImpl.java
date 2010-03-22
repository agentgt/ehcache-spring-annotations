/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.integration;

import edu.wisc.services.cache.annotations.Cacheable;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class MissingCacheNameImpl {

	@Cacheable(cacheName="nonexistent")
	public String method(String argument) {
		return "method says " + argument;
	}
}
