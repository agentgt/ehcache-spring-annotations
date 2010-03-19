/**
 * 
 */
package edu.wisc.services.cache;

import java.lang.reflect.Method;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface FlushableAttributeSource {

	/**
     * Gets the {@link FlushableAttribute} for a method. If the method is not eligible for
     * flushing, null is returned. 
     * 
     * @param method The method to inspect
     * @param targetClass The class the method exists on
     * @return cache flushing configuration for the method, null if the method is not marked {@link Flushable}.
     */
    public FlushableAttribute getFlushableAttribute(Method method, Class<?> targetClass);
}
