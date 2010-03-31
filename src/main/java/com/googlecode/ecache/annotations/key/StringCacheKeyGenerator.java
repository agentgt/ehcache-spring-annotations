/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

/**
 * This key generator is a compromise on {@link ArgumentListCacheKeyGenerator}. The returned key is a String representation
 * of the {@link List} similar to that returned by {@link ArgumentListCacheKeyGenerator}. This helps reduce the cost of
 * hashCode and equals calls on the key at the cost of a very small loss in uniqueness guarantee. This generator also does
 * not require that all arguments implement {@link Serializable}.
 * 
 * The caveats with this approach are:
 * <ul>
 *  <li>
 *      All method arguments must have well implemented toString methods for it to be useful. In practice this key 
 *      generator is primarily useful for methods that use primitive or String arguments
 *  </li>
 * </ul>  
 * 
 * Uses {@link ArgumentListCacheKeyGenerator} to generate a {@link List} key from the {@link MethodInvocation} then
 * calls {@link List#toString()} to generate the actual key.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StringCacheKeyGenerator implements CacheKeyGenerator<String> {
    private boolean includeMethod = true;
    
    public StringCacheKeyGenerator() {
    }
    
    /**
     * @see ArgumentListCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public StringCacheKeyGenerator(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }

    /**
     * @see ArgumentListCacheKeyGenerator#setIncludeMethod(boolean)
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
    public String generateKey(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        
        final ArrayList<Object> keyList;
        if (this.includeMethod) {
            keyList = new ArrayList<Object>(arguments.length + 4);
            
            final Method method = methodInvocation.getMethod();
            keyList.add(method.getDeclaringClass());
            keyList.add(method.getName());
            keyList.add(method.getReturnType());
            keyList.add(this.arrayCheck(method.getParameterTypes()));
        }
        else {
            keyList = new ArrayList<Object>(arguments.length);
        }
        
        for (final Object arg : arguments) {
            keyList.add(this.arrayCheck(arg));
        }
        
        return keyList.toString();
    }
    
    protected Object arrayCheck(Object object) {
        if (object == null) {
            return null;
        }
        
        final Class<? extends Object> c = object.getClass();
        if (!c.isArray()) {
            return object;
        }

        final int length = Array.getLength(object);
        final ArrayList<Object> objArray = new ArrayList<Object>(length);
        for (int index = 0; index < length; index++) {
            final Object arrayValue = Array.get(object, index);
            objArray.add(this.arrayCheck(arrayValue));
        }
        
        return Collections.unmodifiableList(objArray);
    }
}
