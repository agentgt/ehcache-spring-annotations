/**
 * 
 */
package edu.wisc.services.cache;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.annotations.TriggersRemove;


/**
 * Represents the objects needed to intercept calls to methods annotated
 * with {@link TriggersRemove}
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface TriggersRemoveAttribute extends MethodAttribute {
    /**
     * @return true if {@link Ehcache#removeAll()} should be called.
     */
    public boolean isRemoveAll();
    
}
