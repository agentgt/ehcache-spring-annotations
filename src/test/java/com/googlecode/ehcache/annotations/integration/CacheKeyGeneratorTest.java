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
 * @version $Revision: 259 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cacheKeyGeneratorTestContext.xml")
public class CacheKeyGeneratorTest {
    private CacheKeyGeneratorTestInterface cacheableTestInterface;

    @Autowired
    public void setCacheableTestInterface(CacheKeyGeneratorTestInterface cacheableTestInterface) {
        this.cacheableTestInterface = cacheableTestInterface;
    }

    @Test
    public void testGeneratedHashCode() {
        Assert.assertEquals(0, cacheableTestInterface.singleCachedCallCount());
        
        Assert.assertEquals("singleCachedCall(foo)", cacheableTestInterface.singleCachedCall("foo"));
        Assert.assertEquals(1, cacheableTestInterface.singleCachedCallCount());
        
        Assert.assertEquals("singleCachedCall(foo)", cacheableTestInterface.singleCachedCall("foo"));
        Assert.assertEquals(1, cacheableTestInterface.singleCachedCallCount());
    }

    @Test
    public void testCallListCacheKeyGenerator() {
        Assert.assertEquals(0, cacheableTestInterface.callListCacheKeyGeneratorCount());
        
        Assert.assertEquals("callListCacheKeyGenerator(foo)", cacheableTestInterface.callListCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callListCacheKeyGeneratorCount());
        
        Assert.assertEquals("callListCacheKeyGenerator(foo)", cacheableTestInterface.callListCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callListCacheKeyGeneratorCount());
    }

    @Test
    public void testCallMessageDigestCacheKeyGenerator() {
        Assert.assertEquals(0, cacheableTestInterface.callMessageDigestCacheKeyGeneratorCount());
        
        Assert.assertEquals("callMessageDigestCacheKeyGenerator(foo)", cacheableTestInterface.callMessageDigestCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callMessageDigestCacheKeyGeneratorCount());
        
        Assert.assertEquals("callMessageDigestCacheKeyGenerator(foo)", cacheableTestInterface.callMessageDigestCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callMessageDigestCacheKeyGeneratorCount());
    }

    @Test
    public void testCallReflectionHashCodeCacheKeyGenerator() {
        Assert.assertEquals(0, cacheableTestInterface.callReflectionHashCodeCacheKeyGeneratorCount());
        
        Assert.assertEquals("callReflectionHashCodeCacheKeyGenerator(foo)", cacheableTestInterface.callReflectionHashCodeCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callReflectionHashCodeCacheKeyGeneratorCount());
        
        Assert.assertEquals("callReflectionHashCodeCacheKeyGenerator(foo)", cacheableTestInterface.callReflectionHashCodeCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callReflectionHashCodeCacheKeyGeneratorCount());
    }

    @Test
    public void testCallStringCacheKeyGenerator() {
        Assert.assertEquals(0, cacheableTestInterface.callStringCacheKeyGeneratorCount());
        
        Assert.assertEquals("callStringCacheKeyGenerator(foo)", cacheableTestInterface.callStringCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callStringCacheKeyGeneratorCount());
        
        Assert.assertEquals("callStringCacheKeyGenerator(foo)", cacheableTestInterface.callStringCacheKeyGenerator("foo"));
        Assert.assertEquals(1, cacheableTestInterface.callStringCacheKeyGeneratorCount());
    }
}


