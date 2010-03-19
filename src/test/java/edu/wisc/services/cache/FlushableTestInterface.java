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
	
	@Flushable(cacheName="flushRemoveCountingCache")
	public void methodTriggersFlush();
	
	@Flushable(cacheName="flushRemoveCountingCache", removeAll=true)
	public void methodTriggersFlushAndRemoveAll();
}
