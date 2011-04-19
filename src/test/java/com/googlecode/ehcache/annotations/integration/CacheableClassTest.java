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

package com.googlecode.ehcache.annotations.integration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cacheableClassTestContext.xml")
public class CacheableClassTest {
    private CacheableClassTestImpl cacheableClassTestImpl;

    @Autowired
    public void setCacheableClassTestImpl(CacheableClassTestImpl cacheableClassTestImpl) {
        this.cacheableClassTestImpl = cacheableClassTestImpl;
    }
    
    @Test
    public void testAutowiring() {
        Assert.assertNotNull(this.cacheableClassTestImpl.getCacheManager());
        Assert.assertNotNull(this.cacheableClassTestImpl.getCacheManagerField());
    }

    @Test
    public void testCachingClassArgs() {
        Assert.assertEquals(0, cacheableClassTestImpl.getClassAnnotatedCachedCount());
        
        Assert.assertEquals("classAnnotatedCached(foo)", cacheableClassTestImpl.classAnnotatedCached("foo"));
        Assert.assertEquals(1, cacheableClassTestImpl.getClassAnnotatedCachedCount());
        
        Assert.assertEquals("classAnnotatedCached(foo)", cacheableClassTestImpl.classAnnotatedCached("foo"));
        Assert.assertEquals(1, cacheableClassTestImpl.getClassAnnotatedCachedCount());
        
        Assert.assertEquals("classAnnotatedCached(bar)", cacheableClassTestImpl.classAnnotatedCached("bar"));
        Assert.assertEquals(2, cacheableClassTestImpl.getClassAnnotatedCachedCount());
        
        Assert.assertEquals("classAnnotatedCached(bar)", cacheableClassTestImpl.classAnnotatedCached("bar"));
        Assert.assertEquals(2, cacheableClassTestImpl.getClassAnnotatedCachedCount());
    }
    
}


