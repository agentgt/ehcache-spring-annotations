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

import org.aopalliance.intercept.MethodInvocation;

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
public class ArgumentListCacheKeyGenerator implements CacheKeyGenerator<ReadOnlyList<Serializable>> {
    private boolean includeMethod = true;
    
    public ArgumentListCacheKeyGenerator() {
    }
    
    /**
     * @see ArgumentListCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public ArgumentListCacheKeyGenerator(boolean includeMethod) {
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
    public ReadOnlyList<Serializable> generateKey(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        
        final ArrayList<Serializable> keyList;
        if (this.includeMethod) {
            keyList = new ArrayList<Serializable>(arguments.length + 4);
            
            final Method method = methodInvocation.getMethod();
            keyList.add(method.getDeclaringClass());
            keyList.add(method.getName());
            keyList.add(method.getReturnType());
            keyList.add(this.arrayCheck(method.getParameterTypes()));
        }
        else {
            keyList = new ArrayList<Serializable>(arguments.length);
        }
        
        for (final Object arg : arguments) {
            keyList.add(this.arrayCheck((Serializable)arg));
        }
        
        //Cast here is ugly by we know unmodifiable list is serializable
        return new ReadOnlyList<Serializable>(keyList);
    }
    
    protected Serializable arrayCheck(Serializable object) {
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
            objArray.add(this.arrayCheck((Serializable)arrayValue));
        }
        
        return (Serializable)Collections.unmodifiableList(objArray);
    }
}
