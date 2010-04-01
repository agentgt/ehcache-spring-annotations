/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

/**
 * This key generator is a compromise on {@link ListCacheKeyGenerator}. The returned key is a String representation
 * of the {@link List} similar to that returned by {@link ListCacheKeyGenerator}. This helps reduce the cost of
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
 * Uses {@link ListCacheKeyGenerator} to generate a {@link List} key from the {@link MethodInvocation} then
 * calls {@link List#toString()} to generate the actual key.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StringCacheKeyGenerator extends AbstractCacheKeyGenerator<String> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ecache.annotations.key.StringCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    protected final static String NULL_STRING = String.valueOf((Object)null);
    
    public StringCacheKeyGenerator() {
    }

    public StringCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    @Override
    protected String generateKey(Object... data) {
        final ArrayList<Object> keyList = new ArrayList<Object>(data.length);
        for (final Object arg : data) {
            keyList.add(this.arrayCheck(arg));
        }
        
        return keyList.toString();
    }
    
    protected String arrayCheck(Object object) {
        if (object == null) {
            return NULL_STRING;
        }
        
        if (!register(object)) {
            //Return class name in place of the actual hash code in the case of a circular reference
            return "\"CIRCULAR_REFERENCE:" + object.getClass().getName() + "\"";
        }
        try {
            final Class<? extends Object> c = object.getClass();
            if (!c.isArray()) {
                return object.toString();
            }
    
            final int length = Array.getLength(object);
            final ArrayList<Object> objArray = new ArrayList<Object>(length);
            for (int index = 0; index < length; index++) {
                final Object arrayValue = Array.get(object, index);
                objArray.add(this.arrayCheck(arrayValue));
            }
            
            return objArray.toString();
        }
        finally {
            unregister(object);
        }
    }
}
