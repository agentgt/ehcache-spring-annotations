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

package edu.wisc.services.cache.integration;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.wisc.services.cache.CacheNotFoundException;

/**
 * @author Eric Dalquist
 * @version $Revision: 255 $
 */
public class ConfigurationFailureTest {

    @Test
    public void testNoCacheManager() {
        try {
            new ClassPathXmlApplicationContext("/noCacheManagerTestContext.xml");
            Assert.fail("Test should have failed with no CacheManager defined");
        }
        catch (BeanCreationException bce) {
            Assert.assertEquals("cacheableTestImpl", bce.getBeanName());
            final NoSuchBeanDefinitionException nsbd = (NoSuchBeanDefinitionException)bce.getCause();
            Assert.assertEquals("cacheManager", nsbd.getBeanName());
        }
    }
    
    @Test
    public void testNoCache() {
        try {
            new ClassPathXmlApplicationContext("/noCacheTestContext.xml");
            Assert.fail("Test should have failed with no Cache defined");
        }
        catch (BeanCreationException bce) {
            Assert.assertEquals("cacheableTestImpl", bce.getBeanName());
            final CacheNotFoundException cnfe = (CacheNotFoundException)bce.getCause();
            Assert.assertEquals("interfaceDefined", cnfe.getCacheName());
        }
    }
    

    @Test
    public void testNoCacheKeyGenerator() {
        try {
            new ClassPathXmlApplicationContext("/noCacheKeyGeneratorTestContext.xml");
            Assert.fail("Test should have failed with no CacheKeyGenerator defined");
        }
        catch (BeanCreationException bce) {
            Assert.assertEquals("cacheConfigurationTestImpl", bce.getBeanName());
            final NoSuchBeanDefinitionException nsbd = (NoSuchBeanDefinitionException)bce.getCause();
            Assert.assertEquals("customKeyGenerator", nsbd.getBeanName());
        }
    }
}


