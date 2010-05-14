/**
 * 
 */
package com.googlecode.ehcache.annotations.config;

/**
 * Implementation of {@link CacheNameMatcher} for
 * matching a specific name.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class ExactCacheNameMatcherImpl implements CacheNameMatcher {

	private final String name;
	/**
	 * 
	 * @param name
	 * @throws IllegalArgumentException if the name argument is null
	 */
	public ExactCacheNameMatcherImpl(String name) {
		if(null == name) {
			throw new IllegalArgumentException("name cannot be null");
		}
		this.name = name;
	}
	/**
	 * Returns true if the name field {@link String#equals(Object)} the cacheName argument,
	 * null otherwise.
	 * @see String#equals(Object)
	 * @see com.googlecode.ehcache.annotations.config.CacheNameMatcher#matches(java.lang.String)
	 */
	public Boolean matches(String cacheName) {
		return this.name.equals(cacheName) ? true : null;
	}

}
