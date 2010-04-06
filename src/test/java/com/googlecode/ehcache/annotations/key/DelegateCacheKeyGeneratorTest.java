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

package com.googlecode.ehcache.annotations.key;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link DelegateCacheKeyGenerator}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class DelegateCacheKeyGeneratorTest {

	/**
	 * Verify expected behavior for a {@link DelegateCacheKeyGenerator} wrapping
	 * a {@link StringCacheKeyGenerator}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		StringCacheKeyGenerator delegate = new StringCacheKeyGenerator();
		DelegateCacheKeyGenerator impl = new DelegateCacheKeyGenerator(delegate) {
			@Override
			public Serializable getAdditionalDataForKeyGeneration() {
				return "foo";
			}
		};
		
		// set the impl's checkForCycles to the opposite of the delegate
		impl.setCheckforCycles(!delegate.isCheckforCycles());
		
		String data = "bar";
		Object[] expected = new Object[] {"foo", new Object[] { data } };
		
		Serializable delegateValue = delegate.generateKey(data);
		Serializable expectedValue = delegate.generateKey(expected);
		Serializable implValue = impl.generateKey(data);
		
		Assert.assertNotSame(delegateValue, implValue);
		
		Assert.assertEquals(expectedValue, implValue);
		
		// verify that the impl's checkForCycles field was updated to that of the delegate on first generateKey call
		Assert.assertEquals(delegate.isCheckforCycles(), impl.isCheckforCycles());
		
	}
}
