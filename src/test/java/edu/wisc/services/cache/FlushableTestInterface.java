/**
 * 
 */
package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.Flushable;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface FlushableTestInterface {

	public void notFlushableMethod();
	
	@Flushable(cacheName=TestFlushAndRemoveAllCountingEhcache.FLUSH_REMOVE_COUNT_CACHE_NAME)
	public void methodTriggersFlush();
	
	@Flushable(cacheName=TestFlushAndRemoveAllCountingEhcache.FLUSH_REMOVE_COUNT_CACHE_NAME, removeAll=true)
	public void methodTriggersFlushAndRemoveAll();
}
