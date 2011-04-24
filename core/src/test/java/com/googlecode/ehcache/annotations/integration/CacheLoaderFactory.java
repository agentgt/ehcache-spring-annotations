/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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

