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
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractCacheKeyGenerator<T extends Serializable> implements CacheKeyGenerator<T> {
    private static final ThreadLocal<Map<Object, Object>> REGISTRY = new ThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new IdentityHashMap<Object, Object>();
        }
    };
    
    private boolean checkforCycles = false;
    private boolean includeMethod = true;
    private boolean includeParameterTypes = true;
    
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
     * @param includeMethod true If the {@link Method} from the {@link MethodInvocation} should be included in the generated key. Defaults to true.
     */
    public void setIncludeMethod(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }
    
    public boolean isIncludeParameterTypes() {
        return includeParameterTypes;
    }
    /**
     * @param includeParameterTypes true if the {@link Method#getParameterTypes()} should be included in the generated key. Defaults to false.
     */
    public void setIncludeParameterTypes(boolean includeParameterTypes) {
        this.includeParameterTypes = includeParameterTypes;
    }
    
    public boolean isCheckforCycles() {
        return checkforCycles;
    }
    /**
     * @param checkforCycles true If the hash code generation should gracefully handle object graph cycles. If false
     *                       cycles result in undefined behavior. Defaults to false.
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
    
    protected abstract T generateKey(Object... data);
}
