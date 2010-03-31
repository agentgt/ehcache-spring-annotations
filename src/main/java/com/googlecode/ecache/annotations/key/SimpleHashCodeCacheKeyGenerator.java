/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;

/**
 * This key generator is a good option for fast but rough comparison of method invocations. The standard Java
 * hashCode method is used which will only provide 32bits of key space.
 * 
 * The caveats with this approach are:
 * <ul>
 *  <li>
 *      The Java hashCode function returns an int so only the key ends up being only 32bits. This is fairly small and
 *      a collision could occur with no fallback check to disambiguate the two keys.
 *  </li>
 * </ul>  
 * 
 * If includeMethod is set to true {@link Method#getDeclaringClass()}, {@link Method#getName()},
 * {@link Method#getReturnType()}, and {@link Method#getParameterTypes()} will be included in the generated
 * key. includeMethod defaults to true.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SimpleHashCodeCacheKeyGenerator implements CacheKeyGenerator<Integer> {
    private boolean includeMethod = true;
    
    public SimpleHashCodeCacheKeyGenerator() {
    }
    
    /**
     * @see SimpleHashCodeCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public SimpleHashCodeCacheKeyGenerator(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }

    /**
     * @see SimpleHashCodeCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public boolean isIncludeMethod() {
        return includeMethod;
    }

    /**
     * @param includeMethod true If the {@link Method} from the {@link MethodInvocation} should be included in the generated key. Defaults to true.
     */
    public void setIncludeMethod(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ecache.annotations.key.CacheKeyGenerator#generateKey(org.aopalliance.intercept.MethodInvocation)
     */
    public Integer generateKey(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        
        if (this.includeMethod) {
            final Method method = methodInvocation.getMethod();
            
            return Arrays.deepHashCode(new Object[] {
                    method.getDeclaringClass(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    arguments});
        }
        
        return Arrays.deepHashCode(arguments);
    }
}
