package com.googlecode.ehcache.annotations.integration;


public class CacheLoaderTestImpl implements CacheLoaderTestInterface {
	
	public String getValue() {
		return CacheLoaderTest.CACHE_LOADER_TEST_VALUE;
	}
}
