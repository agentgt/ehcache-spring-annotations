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
package com.googlecode.ehcache.annotations.integration;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
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
@ContextConfiguration(locations = "/partialCacheKeyTestContext.xml")
public class PartialCacheKeyTest {
    private PartialCacheKeyTestInterface partialCacheKeyTestInterface;

    @Autowired
    public void setPartialCacheKeyTestInterface(PartialCacheKeyTestInterface partialCacheKeyTestInterface) {
        this.partialCacheKeyTestInterface = partialCacheKeyTestInterface;
    }
    
    @Before
    public void setup() {
        this.partialCacheKeyTestInterface.reset();
    }
    
    @Test
    public void testPartialKeyGeneration() {
        assertEquals(0, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodOne("a", 1, 12, TimeUnit.MILLISECONDS);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodOne("a", 1, 12, TimeUnit.MILLISECONDS);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodOne("a", 1, 12, null);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodOne("a", 1, 0, TimeUnit.MILLISECONDS);
        assertEquals(2, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
    }
    
    @Test
    public void testSharedPartialKeyGeneration() {
        assertEquals(0, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodOne("b", 1, 12, TimeUnit.MILLISECONDS);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodOneCount());
        
        
        assertEquals(0, this.partialCacheKeyTestInterface.cacheableMethodThreeCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodThree("b", 1, 12);
        assertEquals(0, this.partialCacheKeyTestInterface.cacheableMethodThreeCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodThree("c", 1, 12);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodThreeCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodThree("c", 1, 12);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodThreeCount());
        

        assertEquals(0, this.partialCacheKeyTestInterface.cacheableMethodTwoCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodTwo(1, 12);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodTwoCount());
        
        this.partialCacheKeyTestInterface.cacheableMethodTwo(1, 12);
        assertEquals(1, this.partialCacheKeyTestInterface.cacheableMethodTwoCount());
    }
}


