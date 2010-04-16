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

import java.util.concurrent.TimeUnit;

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
@ContextConfiguration(locations = "/cacheableTestContext.xml")
public class CacheableTest {
    private CacheableTestInterface cacheableTestInterface;

    @Autowired
    public void setCacheableTestInterface(CacheableTestInterface cacheableTestInterface) {
        this.cacheableTestInterface = cacheableTestInterface;
    }

    @Test
    public void testCachingOnTheInterfaceWithArgs() {
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedCached(foo)", cacheableTestInterface.interfaceAnnotatedCached("foo"));
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedCached(foo)", cacheableTestInterface.interfaceAnnotatedCached("foo"));
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedCached(bar)", cacheableTestInterface.interfaceAnnotatedCached("bar"));
        Assert.assertEquals(2, cacheableTestInterface.interfaceAnnotatedCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedCached(bar)", cacheableTestInterface.interfaceAnnotatedCached("bar"));
        Assert.assertEquals(2, cacheableTestInterface.interfaceAnnotatedCachedCount());
    }

    @Test
    public void testCachingOnTheInterfaceNoArgs() {
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedNoArgCached()", cacheableTestInterface.interfaceAnnotatedNoArgCached());
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedNoArgCached()", cacheableTestInterface.interfaceAnnotatedNoArgCached());
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedNoArgCachedCount());
    }

    @Test
    public void testCachingOnTheImplNoArgs() {
        Assert.assertEquals(0, cacheableTestInterface.interfaceDefinedCount());
        
        Assert.assertEquals("interfaceDefined(foo)", cacheableTestInterface.interfaceDefined("foo"));
        Assert.assertEquals(1, cacheableTestInterface.interfaceDefinedCount());
        
        Assert.assertEquals("interfaceDefined(foo)", cacheableTestInterface.interfaceDefined("foo"));
        Assert.assertEquals(1, cacheableTestInterface.interfaceDefinedCount());
        
        Assert.assertEquals("interfaceDefined(bar)", cacheableTestInterface.interfaceDefined("bar"));
        Assert.assertEquals(2, cacheableTestInterface.interfaceDefinedCount());
        
        Assert.assertEquals("interfaceDefined(bar)", cacheableTestInterface.interfaceDefined("bar"));
        Assert.assertEquals(2, cacheableTestInterface.interfaceDefinedCount());
    }

    @Test
    public void testExceptionCaching() {
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        Assert.assertEquals("interfaceAnnotatedExceptionCached(false)", cacheableTestInterface.interfaceAnnotatedExceptionCached(false));
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        Assert.assertEquals("interfaceAnnotatedExceptionCached(false)", cacheableTestInterface.interfaceAnnotatedExceptionCached(false));
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(0, cacheableTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            cacheableTestInterface.interfaceAnnotatedExceptionCached(true);
            Assert.fail("interfaceAnnotatedExceptionCached(true) should have thrown an exception");
        }
        catch (RuntimeException re) {
            Assert.assertEquals("throwsException was true", re.getMessage());
        }
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            cacheableTestInterface.interfaceAnnotatedExceptionCached(true);
            Assert.fail("interfaceAnnotatedExceptionCached(true) should have thrown an exception");
        }
        catch (RuntimeException re) {
            Assert.assertEquals("throwsException was true", re.getMessage());
        }
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedCount());
        Assert.assertEquals(1, cacheableTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
    }

    @Test
    public void testEnumArgCaching() {
        Assert.assertEquals(0, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(DAYS)", cacheableTestInterface.enumParameter(TimeUnit.DAYS));
        Assert.assertEquals(1, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(DAYS)", cacheableTestInterface.enumParameter(TimeUnit.DAYS));
        Assert.assertEquals(1, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(null)", cacheableTestInterface.enumParameter(null));
        Assert.assertEquals(2, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(null)", cacheableTestInterface.enumParameter(null));
        Assert.assertEquals(2, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(MINUTES)", cacheableTestInterface.enumParameter(TimeUnit.MINUTES));
        Assert.assertEquals(3, cacheableTestInterface.enumParameterCount());
        
        Assert.assertEquals("enumParameter(MINUTES)", cacheableTestInterface.enumParameter(TimeUnit.MINUTES));
        Assert.assertEquals(3, cacheableTestInterface.enumParameterCount());
    }
}


