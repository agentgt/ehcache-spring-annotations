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

}
