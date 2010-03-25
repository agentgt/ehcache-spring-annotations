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

package edu.wisc.services.cache.integration;

import junit.framework.Assert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.wisc.services.cache.CacheNotFoundException;
import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision: 255 $
 */
public class ConfigurationFailureTest {

	/**
	 * Test verifies behavior when no {@link CacheManager} is defined in the Spring configuration.
	 */
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
    
    /**
     * Test verifies behavior when no {@link Ehcache} is defined for
     * the cacheName attribute on the  {@link Cacheable} annotated method AND
     * 'createMissingCaches' is false.
     */
    @Test
    public void testNoCache() {
        try {
            new ClassPathXmlApplicationContext("/noCacheTestContext.xml");
            Assert.fail("Test should have failed with no Cache defined");
        }
        catch (BeanCreationException bce) {
            Assert.assertEquals("missingCacheNameImpl", bce.getBeanName());
            final CacheNotFoundException cnfe = (CacheNotFoundException)bce.getCause();
            Assert.assertEquals("nonexistent", cnfe.getCacheName());
        }
    }
    
    /**
     * Test verifies behavior when no {@link CacheKeyGenerator} is defined for
     * the cacheKeyGenerator attribute on the {@link Cacheable} annotated method.
     */
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


