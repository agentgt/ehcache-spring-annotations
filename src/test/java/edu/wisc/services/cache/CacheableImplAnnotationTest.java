/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.services.cache;

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
@ContextConfiguration(locations = "/cacheableImplTestContext.xml")
public class CacheableImplAnnotationTest {
    private PlainTestInterface plainTestInterface;
    
    @Autowired
    public void setPlainTestInterface(PlainTestInterface plainTestInterface) {
    	this.plainTestInterface = plainTestInterface;
    }
    
    /**
     * Test where Cacheable annotations exist only on the underlying implementation.
     */
    @Test
    public void testCachingOnTheImpl() {
    	 Assert.assertEquals(0, plainTestInterface.getaCallCount());
    	 Assert.assertEquals(0, plainTestInterface.getbCallCount());
    	 
    	 Assert.assertEquals("methodA says foo", plainTestInterface.methodA("foo"));
    	 Assert.assertEquals(1, plainTestInterface.getaCallCount());
    	 Assert.assertEquals(0, plainTestInterface.getbCallCount());
    	 Assert.assertEquals("methodB says bar", plainTestInterface.methodB("bar"));
    	 Assert.assertEquals(1, plainTestInterface.getaCallCount());
    	 Assert.assertEquals(1, plainTestInterface.getbCallCount());
    	 
    	 Assert.assertEquals("methodA says foo", plainTestInterface.methodA("foo"));
    	 Assert.assertEquals(2, plainTestInterface.getaCallCount());
    	 Assert.assertEquals("methodA says foo", plainTestInterface.methodA("foo"));
    	 Assert.assertEquals(3, plainTestInterface.getaCallCount());
    	 
    	 Assert.assertEquals("methodB says bar", plainTestInterface.methodB("bar"));
    	 Assert.assertEquals(1, plainTestInterface.getbCallCount());
    	 Assert.assertEquals("methodB says bar", plainTestInterface.methodB("bar"));
    	 Assert.assertEquals(1, plainTestInterface.getbCallCount());
    }
}


