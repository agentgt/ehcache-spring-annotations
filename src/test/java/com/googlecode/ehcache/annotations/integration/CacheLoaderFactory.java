// $Id$
package com.googlecode.ehcache.annotations.integration;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

public class CacheLoaderFactory extends net.sf.ehcache.loader.CacheLoaderFactory {

	@Override
	public CacheLoader createCacheLoader(Ehcache cache, Properties properties) {
		return new CacheLoader() {

			public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
				return this;
			}

			public void dispose() throws CacheException { 
			}

			public String getName() {
				return null;
			}

			public Status getStatus() {
				return null;
			}

			public void init() { 
			}

			public Object load(Object key) throws CacheException {
				return load(key, null);
			}

			public Object load(Object key, Object argument) {
				return CacheLoaderTest.CACHE_LOADER_VALUE;
			}

			public Map<?, ?> loadAll(@SuppressWarnings("rawtypes") Collection keys) {
				return null;
			}

			public Map<?, ?>  loadAll(@SuppressWarnings("rawtypes") Collection keys, Object argument) {
				return null;
			}
		};
	}
}

