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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * {@link CacheNameMatcher} that delegates to a {@link Pattern}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class PatternCacheNameMatcherImpl implements CacheNameMatcher {

	private final Pattern pattern;
	
	/**
	 * 
	 * @param pattern
	 */
	public PatternCacheNameMatcherImpl(Pattern pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * 
	 * @see Pattern#compile(String)
	 * @param regex
	 * @throws PatternSyntaxException 
	 */
	public PatternCacheNameMatcherImpl(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.ehcache.annotations.config.CacheNameMatcher#matches(java.lang.String)
	 */
	public Boolean matches(String cacheName) {
		Matcher m = this.pattern.matcher(cacheName);
		return m.matches() ? true : null;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

}
