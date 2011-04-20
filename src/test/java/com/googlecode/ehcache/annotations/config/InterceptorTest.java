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

package com.googlecode.ehcache.annotations.config;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.ehcache.annotations.CountingCacheableInterceptor;
import com.googlecode.ehcache.annotations.CountingTriggersRemoveInterceptor;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/com/googlecode/ehcache/annotations/config/interceptorTest.xml")
public class InterceptorTest {
    @Autowired 
    private InterceptorTestInterface interceptorTestInterface;
    
    @Autowired @Qualifier("countingCacheableInterceptor")
    private CountingCacheableInterceptor countingCacheableInterceptor;
    @Autowired @Qualifier("countingTriggersRemoveInterceptor")
    private CountingTriggersRemoveInterceptor countingTriggersRemoveInterceptor;
    
    @Autowired @Qualifier("customCacheableInterceptor")
    private CountingCacheableInterceptor customCacheableInterceptor;
    @Autowired @Qualifier("customTriggersRemoveInterceptor")
    private CountingTriggersRemoveInterceptor customTriggersRemoveInterceptor;
    
    @Before
    public void setup() {
        this.countingCacheableInterceptor.resetCounters();
        this.countingTriggersRemoveInterceptor.resetCounters();
        this.customCacheableInterceptor.resetCounters();
        this.customTriggersRemoveInterceptor.resetCounters();
    }

    @Test
    public void testInterfaceAnnotatedCached() {
        assertEquals(0, countingCacheableInterceptor.getPreInvCache());
        assertEquals(0, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedCached(false);

        assertEquals(0, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedCached(false);

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedCached(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedCached(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedCount());
        assertEquals(2, interceptorTestInterface.interfaceAnnotatedCachedThrowsCount());
    }

    @Test
    public void testInterfaceAnnotatedExceptionCached() {
        assertEquals(0, countingCacheableInterceptor.getPreInvCache());
        assertEquals(0, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedExceptionCached(false);

        assertEquals(0, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedExceptionCached(false);

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedExceptionCached(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(0, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(1, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedExceptionCached(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, countingCacheableInterceptor.getPreInvCache());
        assertEquals(1, countingCacheableInterceptor.getPostInvCache());
        assertEquals(1, countingCacheableInterceptor.getPreInvCacheEx());
        assertEquals(1, countingCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedThrowsCount());
    }

    @Test
    public void testCustomInterfaceAnnotatedCached() {
        assertEquals(0, customCacheableInterceptor.getPreInvCache());
        assertEquals(0, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedInterceptorThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedCachedInterceptor(false);

        assertEquals(0, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedInterceptorThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedCachedInterceptor(false);

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedCachedInterceptorThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedCachedInterceptor(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedInterceptorCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedInterceptorThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedCachedInterceptor(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedCachedInterceptorCount());
        assertEquals(2, interceptorTestInterface.interfaceAnnotatedCachedInterceptorThrowsCount());
    }

    @Test
    public void testCustomInterfaceAnnotatedExceptionCached() {
        assertEquals(0, customCacheableInterceptor.getPreInvCache());
        assertEquals(0, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptor(false);

        assertEquals(0, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorThrowsCount());
        
        interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptor(false);

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(0, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorCount());
        assertEquals(0, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptor(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(0, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(1, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorThrowsCount());
        
        try {
            interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptor(true);
        }
        catch (Exception e) {
            //expected
        }

        assertEquals(1, customCacheableInterceptor.getPreInvCache());
        assertEquals(1, customCacheableInterceptor.getPostInvCache());
        assertEquals(1, customCacheableInterceptor.getPreInvCacheEx());
        assertEquals(1, customCacheableInterceptor.getPostInvCacheEx());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorCount());
        assertEquals(1, interceptorTestInterface.interfaceAnnotatedExceptionCachedInterceptorThrowsCount());
    }
    
    

    @Test
    public void testInterfaceAnnotatedTriggersRemove() {
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemove(1);

        assertEquals(1, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemove(1);

        assertEquals(2, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
    }

    @Test
    public void testInterfaceAnnotatedTriggersRemoveAll() {
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveAll(1);

        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(1, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveAll(1);

        assertEquals(0, countingTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(2, countingTriggersRemoveInterceptor.getPreInvRemoveAll());
    }
    
    @Test
    public void testCustomInterfaceAnnotatedTriggersRemove() {
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveInterceptor(1);

        assertEquals(1, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveInterceptor(1);

        assertEquals(2, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemoveAll());
    }

    @Test
    public void testCustomInterfaceAnnotatedTriggersRemoveAll() {
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveAllInterceptor(1);

        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(1, customTriggersRemoveInterceptor.getPreInvRemoveAll());
        
        interceptorTestInterface.interfaceAnnotatedTriggersRemoveAllInterceptor(1);

        assertEquals(0, customTriggersRemoveInterceptor.getPreInvRemove());
        assertEquals(2, customTriggersRemoveInterceptor.getPreInvRemoveAll());
    }
}
