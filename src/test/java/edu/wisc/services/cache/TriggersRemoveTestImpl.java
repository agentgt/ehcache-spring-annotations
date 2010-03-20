/**
 * 
 */
package edu.wisc.services.cache;

import edu.wisc.services.cache.annotations.TriggersRemove;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class TriggersRemoveTestImpl implements TriggersRemoveTestInterface {
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#implMethodTriggersFlush()
	 */
	@Override
	@TriggersRemove(cacheName="triggersRemoveCountingCache")
    public void implMethodTriggersRemove() {
    }

    /* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#methodTriggersFlush()
	 */
	@Override
	public void methodTriggersRemove() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#methodTriggersFlushAndRemoveAll()
	 */
	@Override
	public void methodTriggersRemoveAll() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#notFlushableMethod()
	 */
	@Override
	public void notTriggersRemoveMethod() {
	}
}
