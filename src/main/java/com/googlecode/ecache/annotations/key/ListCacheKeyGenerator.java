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

/**
 * This key generator is a good option when you want to be 100% sure that two sets of method invocation arguments
 * are the same. The original arguments are completely preserved and used in every hashCode and equals call made
 * on the key.
 * 
 * The caveats with this approach are:
 * <ul>
 *  <li>
 *      All method arguments must be completely {@link Serializable}. A {@link ClassCastException} will be thrown if
 *      any argument does not implmenent {@link Serializable}.
 *  </li>
 *  <li>
 *      The more complex implementation of hashCode and equals on each argument the more expensive this key is to
 *      compare during cache access operations.
 *  </li>
 * </ul>  
 * 
 * Builds a List out of the method arguments. If any of the arguments are arrays they are converted to lists and this
 * check is done recursively on every visible array element. All non-array objects should have a good equals and hashCode
 * implementation as the List that is returned will delegate to the argument and array member hashCode and equals
 * implementations.
 * 
 * If includeMethod is set to true {@link Method#getDeclaringClass()}, {@link Method#getName()},
 * {@link Method#getReturnType()}, and {@link Method#getParameterTypes()} will be included in the generated
 * key. includeMethod defaults to true.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ListCacheKeyGenerator extends AbstractCacheKeyGenerator<ReadOnlyList<Serializable>> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ecache.annotations.key.ListCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    public ListCacheKeyGenerator() {
    }

    public ListCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    @Override
    protected ReadOnlyList<Serializable> generateKey(Object... data) {
        final ArrayList<Serializable> keyList = new ArrayList<Serializable>(data.length);
        
        for (final Object arg : data) {
            keyList.add(this.arrayCheck((Serializable)arg));
        }
        
        return new ReadOnlyList<Serializable>(keyList);
    }
    
    protected Serializable arrayCheck(Serializable object) {
        if (object == null || !register(object)) {
            //Return null in place of the actual hash code in the case of a circular reference
            return null;
        }
        try {
            final Class<? extends Object> c = object.getClass();
            if (!c.isArray()) {
                return object;
            }
    
            final int length = Array.getLength(object);
            final ArrayList<Object> objArray = new ArrayList<Object>(length);
            for (int index = 0; index < length; index++) {
                final Object arrayValue = Array.get(object, index);
                objArray.add(this.arrayCheck((Serializable)arrayValue));
            }
            
            return (Serializable)Collections.unmodifiableList(objArray);
        }
        finally {
            unregister(object);
        }
    }
}
