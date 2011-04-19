package com.googlecode.ehcache.annotations.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cacheLoaderTestContext.xml")
public class CacheLoaderTest {

	private CacheLoaderTestInterface cacheLoaderTestInterface;

	public static final String CACHE_LOADER_TEST_VALUE = "value1";
	public static final String CACHE_LOADER_VALUE = "value2";
	
    @Autowired
    public void setCacheableTestInterface(CacheLoaderTestInterface cacheLoaderTestInterface) {
        this.cacheLoaderTestInterface = cacheLoaderTestInterface;
    }
    
    @Test
    public void testCacheable() {
    	String value = cacheLoaderTestInterface.getValue();
    	assertEquals(CACHE_LOADER_VALUE, value);
    }
}


