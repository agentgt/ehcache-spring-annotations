package com.googlecode.ehcache.annotations.integration;

import com.googlecode.ehcache.annotations.Cacheable;

public interface CacheLoaderTestInterface {
    
    @Cacheable(cacheName="cacheLoaderTestCache")
    public String getValue();
}