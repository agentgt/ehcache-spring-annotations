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

package com.googlecode.ecache.annotations.integration;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test to verify that the "default-cache-key-generator" attribute
 * in the ehcache config is properly triggered.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/overrideDefaultCacheKeyGeneratorTestContext.xml")
public class OverrideDefaultCacheKeyGeneratorTest {

	private OverrideTestInterface testDao;
	private OverrideTestCustomCacheKeyGenerator cacheKeyGenerator;
	/**
	 * @param testDao the testDao to set
	 */
	@Autowired
	public void setTestDao(OverrideTestInterface testDao) {
		this.testDao = testDao;
	}
	/**
	 * @param cacheKeyGenerator the cacheKeyGenerator to set
	 */
	@Autowired
	public void setCacheKeyGenerator(
			OverrideTestCustomCacheKeyGenerator cacheKeyGenerator) {
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	/**
	 * Verify the custom generator is applied to the testDao.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOverrideDefaultCacheKeyGenerator() throws Exception {
		this.testDao.call("foo");
		Assert.assertEquals(1, testDao.getCallCount());
		Assert.assertEquals(1, cacheKeyGenerator.getCallCount());
		
		this.testDao.call("foo");
		Assert.assertEquals(1, testDao.getCallCount());
		Assert.assertEquals(2, cacheKeyGenerator.getCallCount());
		
		this.testDao.call("foo");
		Assert.assertEquals(1, testDao.getCallCount());
		Assert.assertEquals(3, cacheKeyGenerator.getCallCount());
	}
}
