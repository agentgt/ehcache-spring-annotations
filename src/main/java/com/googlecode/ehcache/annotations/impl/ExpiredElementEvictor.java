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

import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Runnable} implementation that depends on a {@link CacheManager}
 * reference.
 * When {@link #run()} is invoked, {@link Ehcache#evictExpiredElements()}
 * is invoked on each cache.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public final class ExpiredElementEvictor implements Runnable {

	private CacheManager cacheManager;
	private Set<String> includedCacheNames = null;
	private Set<String> excludedCacheNames = null;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * @param cacheManager the cacheManager to set
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	/**
	 * @param includedCacheNames the includedCacheNames to set
	 */
	public void setIncludedCacheNames(Set<String> includedCacheNames) {
		this.includedCacheNames = includedCacheNames;
	}
	/**
	 * @param excludedCacheNames the excludedCacheNames to set
	 */
	public void setExcludedCacheNames(Set<String> excludedCacheNames) {
		this.excludedCacheNames = excludedCacheNames;
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		final long startTime = System.currentTimeMillis();
		final String [] cacheNames = this.cacheManager.getCacheNames();

		// TODO calculate union of cacheNames and includedCacheNames
		// TODO and/or remove excludedCacheNames
		
		long evictedTotal = 0;
		for(String cacheName : cacheNames) {
			Ehcache cache = this.cacheManager.getEhcache(cacheName);
			
			long preEvictSize = cache.getMemoryStoreSize();
			long evictStart = System.currentTimeMillis();
			cache.evictExpiredElements();
			
			if(logger.isDebugEnabled()) {
				long evicted = preEvictSize - cache.getMemoryStoreSize();
				evictedTotal += evicted;
				logger.debug("Evicted " + evicted + " elements from cache '" + cacheName + "' in " + (System.currentTimeMillis() - evictStart) + " ms");
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug("Evicted " + evictedTotal + " elements from " + cacheNames.length + " caches  in " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}

}
