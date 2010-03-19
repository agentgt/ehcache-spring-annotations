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
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public void notifyCacheElementEvicted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheElementExpired() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheElementPut() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheElementRemoved() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheElementUpdated() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheHitInMemory() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheHitOnDisk() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheMissedWithExpired() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyCacheMissedWithNotFound() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyRemoveAll() {
			this.removeAllCount++;
		}

		@Override
		public void notifyStatisticsAccuracyChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyStatisticsCleared() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyStatisticsEnabledChanged(boolean arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyTimeTakenForGet(long arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
