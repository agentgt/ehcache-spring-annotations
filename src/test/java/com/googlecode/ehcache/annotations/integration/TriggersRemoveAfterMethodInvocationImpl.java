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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

/**
 * Single use test dao for {@link TriggersRemoveAfterMethodInvocationTest}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public class TriggersRemoveAfterMethodInvocationImpl {
	
	private final CountDownLatch proceedLatch = new CountDownLatch(1);
	private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
	/**
	 * @return the proceedLatch
	 */
	public CountDownLatch getProceedLatch() {
		return proceedLatch;
	}

	/**
	 * @return the cyclicBarrier
	 */
	public CyclicBarrier getCyclicBarrier() {
		return cyclicBarrier;
	}

	/**
	 * 
	 * @param arg
	 */
	@TriggersRemove(cacheName="triggersRemoveAfterInvocationTestCache", when=When.AFTER_METHOD_INVOCATION, removeAll=true)
	public void afterMethodInvocation(String arg) {
		try {
			// 3
			cyclicBarrier.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e1) {
			e1.printStackTrace();
		}
		
		try {
			// 5
			proceedLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
