/**
 * Copyright 2010 Nicholas Blair, Eric Dalquist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package com.googlecode.ehcache.annotations.config;

/**
 * {@link CacheNameMatcher} that wraps another {@link CacheNameMatcher}
 * and performs a logical NOT on the result of the wrapped {@link #matches(String)} method
 * if not null.
 * 
 * @author Nicholas Blair
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
	 * Retrieve the value from wrapped {@link CacheNameMatcher}'s matches method, then
	 * return the {@link Vote#not(Vote)} of the result.
	 * 
	 * @see Vote#not(Vote)
	 * @see com.googlecode.ehcache.annotations.config.CacheNameMatcher#matches(java.lang.String)
	 */
	public Vote matches(String cacheName) {
		Vote result = this.wrapped.matches(cacheName);
		result = Vote.not(result);
		return result;
	}
	/**
	 * @return the wrapped {@link CacheNameMatcher}
	 */
	public CacheNameMatcher getWrapped() {
		return wrapped;
	}

}
