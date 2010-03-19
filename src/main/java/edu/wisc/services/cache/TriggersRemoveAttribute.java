/**
 * 
 */
package edu.wisc.services.cache;


/**
 * Represents the objects needed to intercept calls to methods annontated
 * with {@link edu.wisc.services.cache.annotations.TriggersRemove}
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface TriggersRemoveAttribute extends MethodAttribute {

    /**
     * 
     * @return true if {@link Ehcache#removeAll()} should be called.
     */
    public boolean isRemoveAll();
    
    
}
