/**
 * 
 */
package edu.wisc.services.cache;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.Before;
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

	private TestFlushAndRemoveAllCountingEhcache flushRemoveCountingCache = new TestFlushAndRemoveAllCountingEhcache();
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


	@Before
	public void registerCountingCache() {
		
			this.cacheManager.addCache(this.flushRemoveCountingCache);
		
	}
	
	
	@Test
	public void testHarness() {
		this.flushableTestInterface.notFlushableMethod();
		Assert.assertEquals(0, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(0, this.flushRemoveCountingCache.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlush();
		Assert.assertEquals(1, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(0, this.flushRemoveCountingCache.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlush();
		Assert.assertEquals(2, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(0, this.flushRemoveCountingCache.getRemoveAllCount());
		this.flushableTestInterface.notFlushableMethod();
		Assert.assertEquals(2, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(0, this.flushRemoveCountingCache.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlushAndRemoveAll();
		Assert.assertEquals(3, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(1, this.flushRemoveCountingCache.getRemoveAllCount());
		this.flushableTestInterface.methodTriggersFlushAndRemoveAll();
		Assert.assertEquals(4, this.flushRemoveCountingCache.getFlushCount());
		Assert.assertEquals(2, this.flushRemoveCountingCache.getRemoveAllCount());
	}
	
}
