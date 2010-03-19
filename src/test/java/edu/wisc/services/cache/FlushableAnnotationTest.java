/**
 * 
 */
package edu.wisc.services.cache;

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
@ContextConfiguration("/testContext.xml")
public class FlushableAnnotationTest /* extends AbstractJUnit4SpringContextTests */ {

	private FlushableTestInterface flushableTestInterface;
	private CacheManager cacheManager;
	/**
	 * @param flushableTestInterface the flushableTestInterface to set
	 */
	@Autowired
	public void setFlushableTestInterface(
			FlushableTestInterface flushableTestInterface) {
		this.flushableTestInterface = flushableTestInterface;
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
		Cache cache = cacheManager.getCache("flushRemoveCountingCache");
		CountingListener listener = new CountingListener();
		cache.registerCacheUsageListener(listener);
		
		this.flushableTestInterface.notFlushableMethod();
		Assert.assertEquals(0, listener.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlush();
		Assert.assertEquals(0, listener.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlush();
		Assert.assertEquals(0, listener.getRemoveAllCount());
		this.flushableTestInterface.notFlushableMethod();
		Assert.assertEquals(0, listener.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlushAndRemoveAll();
		Assert.assertEquals(1, listener.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlushAndRemoveAll();
		Assert.assertEquals(2, listener.getRemoveAllCount());
	}
	
	static class CountingListener implements CacheUsageListener {
		private int removeAllCount = 0;
		/**
		 * @return the removeAllCount
		 */
		public int getRemoveAllCount() {
			return removeAllCount;
		}
		@Override
		public void dispose() {
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
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
