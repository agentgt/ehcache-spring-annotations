/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.services.cache;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@ContextConfiguration(locations = "/testContext.xml")
public class CacheableAnnotationTest extends AbstractJUnit4SpringContextTests {
    private CacheableTestInterface cacheableTestInterface;

    @Autowired
    public void setCachableTestClass(CacheableTestInterface cacheableTestInterface) {
        this.cacheableTestInterface = cacheableTestInterface;
    }

    @Test
    public void testCaching() {
        Assert.assertEquals(0, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resource-foo", cacheableTestInterface.getCachedResource("foo"));
        Assert.assertEquals(1, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resource-foo", cacheableTestInterface.getCachedResource("foo"));
        Assert.assertEquals(1, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resource-bar", cacheableTestInterface.getCachedResource("bar"));
        Assert.assertEquals(2, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resourceTwo-bar", cacheableTestInterface.getCachedResourceTwo("bar"));
        Assert.assertEquals(3, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resourceTwo-bar", cacheableTestInterface.getCachedResourceTwo("bar"));
        Assert.assertEquals(3, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resource", cacheableTestInterface.getCachedSingleResource());
        Assert.assertEquals(4, cacheableTestInterface.getCallCount());
        
        Assert.assertEquals("resource", cacheableTestInterface.getCachedSingleResource());
        Assert.assertEquals(4, cacheableTestInterface.getCallCount());
    }
}


