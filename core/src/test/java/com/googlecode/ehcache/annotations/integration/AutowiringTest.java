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
@ContextConfiguration(locations = "/autowiringTestContext.xml")
public class AutowiringTest {
    private AutowiringTestInterface autowiringTestInterface;
    private AutowiringTestClass autowiringTestClass;

    @Autowired
    public void setAutowiringTestInterface(AutowiringTestInterface autowiringTestInterface) {
        this.autowiringTestInterface = autowiringTestInterface;
    }
    
    @Autowired
    public void setAutowiringTestClass(AutowiringTestClass autowiringTestClass) {
        this.autowiringTestClass = autowiringTestClass;
    }



    @Test
    public void testAutowiringInterface() {
        Assert.assertNotNull(this.autowiringTestInterface.getCacheManager());
        Assert.assertNotNull(this.autowiringTestInterface.getCacheManagerField());
    }
    
    @Test
    public void testAutowiringClass() {
        Assert.assertNotNull(this.autowiringTestClass.getCacheManager());
        Assert.assertNotNull(this.autowiringTestClass.getCacheManagerField());
    }

    @Test
    public void testCachingOnTheInterfaceNoArgs() {
        Assert.assertEquals(0, autowiringTestInterface.interfaceAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedNoArgCached()", autowiringTestInterface.interfaceAnnotatedNoArgCached());
        Assert.assertEquals(1, autowiringTestInterface.interfaceAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("interfaceAnnotatedNoArgCached()", autowiringTestInterface.interfaceAnnotatedNoArgCached());
        Assert.assertEquals(1, autowiringTestInterface.interfaceAnnotatedNoArgCachedCount());
    }

    @Test
    public void testCachingOnTheClassNoArgs() {
        Assert.assertEquals(0, autowiringTestClass.getAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("annotatedNoArgCached()", autowiringTestClass.annotatedNoArgCached());
        Assert.assertEquals(1, autowiringTestClass.getAnnotatedNoArgCachedCount());
        
        Assert.assertEquals("annotatedNoArgCached()", autowiringTestClass.annotatedNoArgCached());
        Assert.assertEquals(1, autowiringTestClass.getAnnotatedNoArgCachedCount());
    }
}


