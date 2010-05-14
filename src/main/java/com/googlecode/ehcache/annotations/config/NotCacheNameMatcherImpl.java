/**
 * 
 */
package com.googlecode.ehcache.annotations.config;

/**
 * {@link CacheNameMatcher} that wraps another {@link CacheNameMatcher}
 * and performs a logical NOT on the result of the wrapped {@link #matches(String)} method
 * if not null.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public final class NotCacheNameMatcherImpl implements CacheNameMatcher {

	private final CacheNameMatcher wrapped;
	/**
	 * 
	 * @param wrapped
	 */
	public NotCacheNameMatcherImpl(CacheNameMatcher wrapped) {
		if(null == wrapped) {
			throw new IllegalArgumentException("CacheNameMatcher argument cannot be null");
		}
		this.wrapped = wrapped;
	}
	/**
	 * If the wrapped {@link CacheNameMatcher} returns null, this will also return null.
	 * Otherwise, it will return the opposite value of the wrapped result.
	 * 
	 * @see com.googlecode.ehcache.annotations.config.CacheNameMatcher#matches(java.lang.String)
	 */
	public Boolean matches(String cacheName) {
		Boolean result = this.wrapped.matches(cacheName);
		if(null != result) {
			return !result;
		} else {
			return null;
		}
	}

}
