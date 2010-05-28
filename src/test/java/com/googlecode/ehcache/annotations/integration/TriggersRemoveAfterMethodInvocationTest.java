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

import java.util.concurrent.BrokenBarrierException;

import junit.framework.Assert;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.ehcache.annotations.When;
import com.googlecode.ehcache.annotations.util.ThreadGroupRunner;

/**
 * Test harness for {@link TriggersRemove} with {@link When#AFTER_METHOD_INVOCATION}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/triggersRemoveAfterMethodInvocationTestContext.xml")
public class TriggersRemoveAfterMethodInvocationTest {

	private TriggersRemoveAfterMethodInvocationImpl triggersRemoveTestImpl;
	private CacheManager cacheManager;
	/**
	 * @param triggersRemoveTestImpl the triggersRemoveTestImpl to set
	 */
	@Autowired
	public void setTriggersRemoveTestImpl(
			TriggersRemoveAfterMethodInvocationImpl triggersRemoveTestImpl) {
		this.triggersRemoveTestImpl = triggersRemoveTestImpl;
	}
	/**
	 * @param cacheManager the cacheManager to set
	 */
	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * 
	 */
	@Test
	public void testHarness() {
		Cache cache = cacheManager.getCache("triggersRemoveAfterInvocationTestCache");
		
		Assert.assertEquals(0, cache.getSize());
		// place an element in the cache so we can tell when removeAll was called
		cache.put(new Element("key", "value"));
		Assert.assertEquals(1, cache.getSize());
		
		ThreadGroupRunner groupRunner = new ThreadGroupRunner("triggersRemoveAfterMethodInvocationTest", true);
		groupRunner.addTask(new Runnable() {
			public void run() {
				//2 
				triggersRemoveTestImpl.afterMethodInvocation("foo");
				try {
					// 6
					triggersRemoveTestImpl.getCyclicBarrier().await();
				} catch (InterruptedException e) {
					Assert.fail(e.getMessage());
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					Assert.fail(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		// 1
		groupRunner.start();
		try {
			// 2
			triggersRemoveTestImpl.getCyclicBarrier().await();
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		
		
		// 4: child thread inside TriggersRemove annotated method
		Assert.assertEquals(1, cache.getSize());
		// 5a
		triggersRemoveTestImpl.getProceedLatch().countDown();
		// TriggersRemove annotated method invocation complete
		try {
			// 6 
			triggersRemoveTestImpl.getCyclicBarrier().await();
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		
		// at this point the removeAll has been called, verify cache is empty
		Assert.assertEquals(0, cache.getSize());
		try {
			groupRunner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
