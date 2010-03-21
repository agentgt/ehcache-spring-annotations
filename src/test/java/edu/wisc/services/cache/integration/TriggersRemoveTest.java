/**
 * 
 */
package edu.wisc.services.cache.integration;

import junit.framework.Assert;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.CacheUsageListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Nicholas Blair, npblair@wisc.edu
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
		Cache cache = cacheManager.getCache("triggersRemoveCountingCache");
		CountingListener listener = new CountingListener();
		cache.registerCacheUsageListener(listener);
		
		Assert.assertEquals(0, listener.getRemoveCount());
		Assert.assertEquals(0, listener.getRemoveAllCount());
		
		this.triggersRemoveTestInterface.notTriggersRemoveMethod();
		Assert.assertEquals(0, listener.getRemoveCount());
        Assert.assertEquals(0, listener.getRemoveAllCount());
		
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

        @Override
		public void dispose() {
		}
		@Override
		public void notifyCacheElementEvicted() {
		}
		@Override
		public void notifyCacheElementExpired() {
		}
		@Override
		public void notifyCacheElementPut() {
		}
		@Override
		public void notifyCacheElementRemoved() {
            this.removeCount++;
		}
		@Override
		public void notifyCacheElementUpdated() {
		}
		@Override
		public void notifyCacheHitInMemory() {
		}
		@Override
		public void notifyCacheHitOnDisk() {
		}
		@Override
		public void notifyCacheMissedWithExpired() {
		}
		@Override
		public void notifyCacheMissedWithNotFound() {
		}
		@Override
		public void notifyRemoveAll() {
			this.removeAllCount++;
		}
		@Override
		public void notifyStatisticsAccuracyChanged(int arg0) {
		}
		@Override
		public void notifyStatisticsCleared() {
		}
		@Override
		public void notifyStatisticsEnabledChanged(boolean arg0) {
		}
		@Override
		public void notifyTimeTakenForGet(long arg0) {
		}
	}
}
