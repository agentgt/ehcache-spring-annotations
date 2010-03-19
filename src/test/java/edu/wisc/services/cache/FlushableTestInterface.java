/**
 * 
 */
package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.TriggersRemove;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface FlushableTestInterface {

	public void notFlushableMethod();
	
	@TriggersRemove(cacheName="flushRemoveCountingCache")
	public void methodTriggersFlush();
	
	@TriggersRemove(cacheName="flushRemoveCountingCache", removeAll=true)
	public void methodTriggersFlushAndRemoveAll();
}
