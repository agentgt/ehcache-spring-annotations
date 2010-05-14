/**
 * 
 */
package com.googlecode.ehcache.annotations.config;

/**
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public interface CacheNameMatcher {

	/**
	 * 
	 * 
	 * @param cacheName
	 * @return true if this should definitely be included, false if definitely excluded, or null for a no vote (leave status as is)
	 */
	Boolean matches(String cacheName);
}
