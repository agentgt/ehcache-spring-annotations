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

/**
 * 
 */
package com.googlecode.ehcache.annotations.integration;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.CacheUsageListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Nicholas Blair
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/triggersRemoveTestContext.xml")
public class TriggersRemoveTest  {

	private TriggersRemoveTestInterface triggersRemoveTestInterface;
	private CacheManager cacheManager;
	/**
	 * @param triggersRemoveTestInterface the triggersRemoveTestInterface to set
	 */
	@Autowired
	public void setFlushableTestInterface(TriggersRemoveTestInterface triggersRemoveTestInterface) {
		this.triggersRemoveTestInterface = triggersRemoveTestInterface;
	}
	/**
	 * @param cacheManager the cacheManager to set
	 */
	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	
	@Test
	public void testHarness() {
	    final Ehcache ehcache = cacheManager.getEhcache("triggersRemoveCountingCache");

        CountingListener listener = new CountingListener();
        ehcache.registerCacheUsageListener(listener);
        
//        final CacheUsageListener loggingListener = (CacheUsageListener)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {CacheUsageListener.class}, new InvocationHandler() {
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println("Called: " + method.getName());
//                
//                return null;
//            }
//        });
//        ehcache.registerCacheUsageListener(loggingListener);
		
		Assert.assertEquals(0, listener.getRemoveCount());
		Assert.assertEquals(0, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.notTriggersRemoveMethod();
		Assert.assertEquals(0, listener.getRemoveCount());
        Assert.assertEquals(0, listener.getRemoveAllCount());
		
        this.triggersRemoveTestInterface.simpleCachedMethod();
        this.triggersRemoveTestInterface.methodTriggersRemove();
        Assert.assertEquals(1, listener.getRemoveCount());
		Assert.assertEquals(0, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.methodTriggersRemove();
        Assert.assertEquals(2, listener.getRemoveCount());
		Assert.assertEquals(0, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.notTriggersRemoveMethod();
        Assert.assertEquals(2, listener.getRemoveCount());
		Assert.assertEquals(0, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.methodTriggersRemoveAll();
        Assert.assertEquals(2, listener.getRemoveCount());
		Assert.assertEquals(1, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.methodTriggersRemoveAll();
        Assert.assertEquals(2, listener.getRemoveCount());
		Assert.assertEquals(2, listener.getRemoveAllCount());
		
        this.triggersRemoveTestInterface.implMethodTriggersRemove();
        Assert.assertEquals(3, listener.getRemoveCount());
        Assert.assertEquals(2, listener.getRemoveAllCount());
	}
	
	static class CountingListener implements CacheUsageListener {
		private int removeAllCount = 0;
		private int removeCount = 0;

		public int getRemoveAllCount() {
			return removeAllCount;
		}
		
		public int getRemoveCount() {
            return this.removeCount;
        }

		public void dispose() {
		}
		public void notifyCacheElementEvicted() {
		}
		public void notifyCacheElementExpired() {
		}
		public void notifyCacheElementPut() {
		}
		public void notifyCacheElementRemoved() {
            this.removeCount++;
		}
		public void notifyCacheElementUpdated() {
		}
		public void notifyCacheHitInMemory() {
		}
		public void notifyCacheHitOnDisk() {
		}
		public void notifyCacheMissedWithExpired() {
		}
		public void notifyCacheMissedWithNotFound() {
		}
		public void notifyRemoveAll() {
			this.removeAllCount++;
		}
		public void notifyStatisticsAccuracyChanged(int arg0) {
		}
		public void notifyStatisticsCleared() {
		}
		public void notifyStatisticsEnabledChanged(boolean arg0) {
		}
		public void notifyTimeTakenForGet(long arg0) {
		}
	}
}
