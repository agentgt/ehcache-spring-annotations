/**
 * 
 */
package edu.wisc.services.cache;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class FlushableTestImpl implements FlushableTestInterface {

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableTestInterface#methodTriggersFlush()
	 */
	@Override
	public void methodTriggersFlush() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableTestInterface#methodTriggersFlushAndRemoveAll()
	 */
	@Override
	public void methodTriggersFlushAndRemoveAll() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableTestInterface#notFlushableMethod()
	 */
	@Override
	public void notFlushableMethod() {
	}

}
