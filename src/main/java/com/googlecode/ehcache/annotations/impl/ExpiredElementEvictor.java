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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.ehcache.annotations.config.CacheNameMatcher;

/**
 * {@link Runnable} implementation that depends on a {@link CacheManager}
 * reference.
 * When {@link #run()} is invoked, {@link Ehcache#evictExpiredElements()}
 * is invoked on each cache identified by the cacheNames field.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public final class ExpiredElementEvictor extends TimerTask implements InitializingBean, DisposableBean {

	private CacheManager cacheManager;
	private List<CacheNameMatcher> cacheNameMatchers = new ArrayList<CacheNameMatcher>();
	private Timer timer;
	private long interval;
	private Set<String> cacheNames = new HashSet<String>();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param cacheManager the cacheManager to set
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	/**
	 * @param cacheNameMatchers the cacheNameMatchers to set
	 */
	public void setCacheNameMatchers(List<CacheNameMatcher> cacheNameMatchers) {
		this.cacheNameMatchers = cacheNameMatchers;
	}
	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}
	/**
	 * @return the cacheNames
	 */
	public Set<String> getCacheNames() {
		return cacheNames;
	}
	/**
	 * @return the cacheNameMatchers
	 */
	public List<CacheNameMatcher> getCacheNameMatchers() {
		return cacheNameMatchers;
	}
	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}
	/*
	 * (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		final long startTime = System.currentTimeMillis();

		long evictedTotal = 0;
		for(String cacheName : this.cacheNames) {
			Ehcache cache = this.cacheManager.getEhcache(cacheName);
			if(null != cache) {
				long preEvictSize = cache.getMemoryStoreSize();
				long evictStart = System.currentTimeMillis();
				cache.evictExpiredElements();
				if(logger.isDebugEnabled()) {
					long evicted = preEvictSize - cache.getMemoryStoreSize();
					evictedTotal += evicted;
					logger.debug("Evicted " + evicted + " elements from cache '" + cacheName + "' in " + (System.currentTimeMillis() - evictStart) + " ms");
				}
			} else {
				if(logger.isDebugEnabled()) {
					logger.debug("no cache found with name " + cacheName);
				}
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug("Evicted " + evictedTotal + " elements from " + cacheNames.size() + " caches  in " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if(null == this.cacheManager) {
			throw new IllegalStateException("cacheManager reference must be set");
		}
		
		cacheNames = calculateEvictableCacheNames(this.cacheManager.getCacheNames());
		cacheNames = Collections.unmodifiableSet(cacheNames);
		
		timer = new Timer(this.cacheManager.getName() + "expiredElementEvictorTimer", true);
		timer.schedule(this, interval, interval);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		this.timer.cancel();
	}
	/**
	 * 
	 * @return
	 */
	protected Set<String> calculateEvictableCacheNames(final String [] cacheManagerCacheNames) {
		Set<String> result = new HashSet<String>();
		// from the list of matchers, calculate the cacheNames set
		for(String cacheManagerCacheName: cacheManagerCacheNames) {
			Boolean status = null;
			for(CacheNameMatcher matcher : this.cacheNameMatchers) {
				status = matcher.matches(cacheManagerCacheName);
				if(null == status) {
					continue;
				} else if (status) {
					result.add(cacheManagerCacheName);
				} else {
					result.remove(cacheManagerCacheName);
				}
			}
		}
		
		return result;
	}
}
