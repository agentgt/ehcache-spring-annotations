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

package com.googlecode.ehcache.annotations.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.ehcache.annotations.config.CacheNameMatcher;
import com.googlecode.ehcache.annotations.config.EhCacheConfigBeanDefinitionParser;
import com.googlecode.ehcache.annotations.config.ExactCacheNameMatcherImpl;
import com.googlecode.ehcache.annotations.config.NotCacheNameMatcherImpl;

/**
 * Tests for {@link ExpiredElementEvictor}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public class ExpiredElementEvictorTest {

	/**
	 * 
	 */
	@Test
	public void testCalculateEvictableCacheNamesNoCacheNameMatchers() {
		final String [] cacheNames = new String[] { "foo", "bar" };
		
		ExpiredElementEvictor evictor = new ExpiredElementEvictor();
		
		Set<String> evictableNames = evictor.calculateEvictableCacheNames(cacheNames);
		Assert.assertEquals(0, evictableNames.size());
	}
	
	/**
	 * Test with {@link EhCacheConfigBeanDefinitionParser#INCLUDE_ALL_CACHE_NAME_MATCHER}.
	 */
	@Test
	public void testCalculateEvictableCacheNamesControl() {
		final String [] cacheNames = new String[] { "foo", "bar" };
		
		List<CacheNameMatcher> matchers = Collections.singletonList(EhCacheConfigBeanDefinitionParser.INCLUDE_ALL_CACHE_NAME_MATCHER);
		ExpiredElementEvictor evictor = new ExpiredElementEvictor();
		evictor.setCacheNameMatchers(matchers);
		
		Set<String> evictableNames = evictor.calculateEvictableCacheNames(cacheNames);
		Assert.assertEquals(2, evictableNames.size());
		Assert.assertTrue(evictableNames.contains("foo"));
		Assert.assertTrue(evictableNames.contains("bar"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testCalculateEvictableCacheNamesSingleNameInclude() {
		final String [] cacheNames = new String[] { "foo", "bar" };
		
		CacheNameMatcher m = new ExactCacheNameMatcherImpl("foo");
		List<CacheNameMatcher> matchers = Collections.singletonList(m);
		ExpiredElementEvictor evictor = new ExpiredElementEvictor();
		evictor.setCacheNameMatchers(matchers);
		
		Set<String> evictableNames = evictor.calculateEvictableCacheNames(cacheNames);
		Assert.assertEquals(1, evictableNames.size());
		Assert.assertTrue(evictableNames.contains("foo"));
		Assert.assertFalse(evictableNames.contains("bar"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testCalculateEvictableCacheNamesSingleNameExclude() {
		final String [] cacheNames = new String[] { "foo", "bar" };
		
		CacheNameMatcher m = new NotCacheNameMatcherImpl(new ExactCacheNameMatcherImpl("bar"));
		
		List<CacheNameMatcher> matchers = new ArrayList<CacheNameMatcher>();
		matchers.add(EhCacheConfigBeanDefinitionParser.INCLUDE_ALL_CACHE_NAME_MATCHER);
		matchers.add(m);
		
		ExpiredElementEvictor evictor = new ExpiredElementEvictor();
		evictor.setCacheNameMatchers(matchers);
		
		Set<String> evictableNames = evictor.calculateEvictableCacheNames(cacheNames);
		Assert.assertEquals(1, evictableNames.size());
		Assert.assertTrue(evictableNames.contains("foo"));
		Assert.assertFalse(evictableNames.contains("bar"));
	}
}
