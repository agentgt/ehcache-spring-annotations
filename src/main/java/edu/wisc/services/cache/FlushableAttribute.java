/**
 * 
 */
package edu.wisc.services.cache;

import net.sf.ehcache.Ehcache;

/**
 * Represents the objects needed to intercept calls to methods annontated
 * with {@link edu.wisc.services.cache.annotations.Flushable}
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface FlushableAttribute {

	/**
     * @return The cache to use, will never be null.
     */
    public Ehcache getCache();
    
    /**
     * 
     * @return true if {@link Ehcache#removeAll()} should be called.
     */
    public boolean isRemoveAll();
}
