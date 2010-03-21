/**
 * 
 */
package edu.wisc.services.cache.integration;

import edu.wisc.services.cache.annotations.TriggersRemove;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface TriggersRemoveTestInterface {

	public void notTriggersRemoveMethod();
	
    public void implMethodTriggersRemove();
	
	@TriggersRemove(cacheName="triggersRemoveCountingCache")
	public void methodTriggersRemove();
	
	@TriggersRemove(cacheName="triggersRemoveCountingCache", removeAll=true)
	public void methodTriggersRemoveAll();
}
