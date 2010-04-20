/**
 * Copyright 2010 Nicholas Blair, Eric Dalquist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.ehcache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Base class for cache key generators. Handles common logic for including/excluding the method
 * signature from key generation. Also provides support for avoiding circular references for key
 * generators that traverse the argument object graph via the {@link #register(Object)} and
 * {@link #unregister(Object)} APIs 
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractCacheKeyGenerator<T extends Serializable> implements CacheKeyGenerator<T> {
    //Used by circular reference tracking register/unregister APIs
    private static final ThreadLocal<Map<Object, Object>> REGISTRY = new ThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new IdentityHashMap<Object, Object>();
        }
    };
    
    private boolean checkforCycles = false;
    private boolean includeMethod = true;
    private boolean includeParameterTypes = true;
    
    /**
     * Default constructor, same as calling {@link #AbstractCacheKeyGenerator(boolean, boolean)} with (true, true) 
     */
    public AbstractCacheKeyGenerator() {
    }
    
    /**
     * @see AbstractCacheKeyGenerator#setIncludeMethod(boolean)
     * @see AbstractCacheKeyGenerator#setIncludeParameterTypes(boolean)
     */
    public AbstractCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        this.includeMethod = includeMethod;
        this.includeParameterTypes = includeParameterTypes;
    }

    public boolean isIncludeMethod() {
        return includeMethod;
    }
    /**
     * Determines if the invoked method signature should be included in the generated cache key. If
     * true {@link Method#getDeclaringClass()}, {@link Method#getName()}, and {@link Method#getReturnType()}
     * are included in the object array the key is generated from. Note that the effect of this option is
     * such that two methods with the same arguments will have different keys if true. If you have two methods
     * in the same class with the same name and return value you may want to enable {@link #setIncludeParameterTypes(boolean)}
     * for even more specific key generation.
     * 
     * Note that including the method signature in key generation reduces key generation speed between
     * 14% and 197% depending on the key generator implementation. See the full documentation on more
     * details on key generation approaches.
     * 
     * @param includeMethod true If the {@link Method} from the {@link MethodInvocation} should be
     *                      included in the generated key, defaults to true.
     * @see #setIncludeParameterTypes(boolean)
     */
    public void setIncludeMethod(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }
    
    public boolean isIncludeParameterTypes() {
        return includeParameterTypes;
    }
    /**
     * Determines if the method parameter types returned by {@link Method#getParameterTypes()} should be
     * included in the generated key. This is broken out into a separate option because the call results
     * in a clone() call on the Class[] every time {@link Method#getParameterTypes()} which reduces key
     * generation speed by between 5% and 22% depending on the key generator implementation. See the full
     * documentation on more details on key generation approaches.
     * 
     * This is option is only used if {@link #setIncludeMethod(boolean)} is true.
     * 
     * @param includeParameterTypes true if the {@link Method#getParameterTypes()} should be included in
     *                              the generated key, defaults to false.
     * @see #setIncludeMethod(boolean)
     */
    public void setIncludeParameterTypes(boolean includeParameterTypes) {
        this.includeParameterTypes = includeParameterTypes;
    }
    
    public boolean isCheckforCycles() {
        return checkforCycles;
    }
    /**
     * If the key generation should gracefully handle object graph cycles. If false the {@link #register(Object)} and
     * {@link #unregister(Object)} methods are non-functional. If true the implementation of this class must correctly
     * utilize the {@link #register(Object)} and {@link #unregister(Object)} methods to track visited objects.
     * 
     * @param checkforCycles Defaults to false.
     */
    public void setCheckforCycles(boolean checkforCycles) {
        this.checkforCycles = checkforCycles;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.key.CacheKeyGenerator#generateKey(org.aopalliance.intercept.MethodInvocation)
     */
    public final T generateKey(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        
        if (this.includeMethod) {
            final Method method = methodInvocation.getMethod();
            
            if (this.includeParameterTypes) {
                return this.generateKey(
                        method.getDeclaringClass(),
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        arguments);
            }
            
            return this.generateKey(
                    method.getDeclaringClass(),
                    method.getName(),
                    method.getReturnType(),
                    arguments);
        }
        
        try {
            return this.generateKey(arguments);
        }
        finally {
            if (this.checkforCycles) {
                //Cleanup our thread local data
                REGISTRY.remove();
            }
        }
    }

    
    /**
     * <p>
     * Registers the given object. Used by the reflection methods to avoid infinite loops.
     * </p>
     * 
     * @param element The object to register.
     */
    protected final boolean register(Object element) {
        if (!this.checkforCycles) {
            return true;
        }
        
        final Map<Object, Object> registry = REGISTRY.get();
        return registry.put(element, element) == null;
    }

    /**
     * <p>
     * Unregisters the given object.
     * </p>
     * 
     * <p>
     * Used by the reflection methods to avoid infinite loops.
     */
    protected final void unregister(Object element) {
        if (!this.checkforCycles) {
            return;
        }
        
        final Map<Object, Object> registry = REGISTRY.get();
        registry.remove(element);
    }
    
    /**
     * Called to generate the key. Depending on the {@link #setIncludeMethod(boolean)} and 
     * {@link #setIncludeParameterTypes(boolean)} the the appropriate parts of the {@link Method}
     * signature will be included in the data array.
     * 
     *  @return The {@link Serializable} cache key for the method invocation.
     */
    public abstract T generateKey(Object... data);
}
