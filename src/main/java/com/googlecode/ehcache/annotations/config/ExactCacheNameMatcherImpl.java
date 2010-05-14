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
		boolean result = this.name.equals(cacheName);
		return result ? Boolean.TRUE : null;
	}
	
	/**
	 * @return the name this matcher is configured to match
	 */
	public String getName() {
		return name;
	}

}
