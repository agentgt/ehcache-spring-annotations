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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

/**
 * 
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractDeepCacheKeyGenerator<G, T extends Serializable> extends AbstractCacheKeyGenerator<T> {
    
    private boolean useReflection = false;
    
    public boolean isUseReflection() {
        return this.useReflection;
    }

    public void setUseReflection(boolean useReflection) {
        this.useReflection = useReflection;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.key.AbstractCacheKeyGenerator#generateKey(java.lang.Object[])
     */
    @Override
    public final T generateKey(Object... data) {
        final G generator = this.getGenerator(data);
        this.deepHashCode(generator, data);
        return this.generateKey(generator);
    }
    
    /**
     * Calls {@link #deepHashCode(KeyGenerationStream, Object)} on each element in the array
     */
    protected final void deepHashCode(G generator, Object a[]) {
        if (a == null) {
            this.appendNull(generator);
            return;
        }

        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
    }


    /**
     * Calls {@link #deepHashCode(KeyGenerationStream, Object)} on each element in the {@link Iterable}
     */
    protected final void deepHashCode(G generator, Iterable<?> a) {
        if (a == null) {
            this.appendNull(generator);
            return;
        }

        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
    }
    
    /**
     * Calls {@link #deepHashCode(KeyGenerationStream, Object)} on both the key and the value.
     */
    protected final void deepHashCode(G generator, Map.Entry<?, ?> e) {
        if (e == null) {
            this.appendNull(generator);
            return;
        }
        
        this.deepHashCode(generator, e.getKey());
        this.deepHashCode(generator, e.getValue());
    }
    
    protected final void reflectionDeepHashCode(G generator, Object element) {
        //Special objects which shouldn't be reflected on due to lack of interesting fields
        if (element instanceof Class<?>) {
            this.append(generator, element);
            return;
        }

        //Resolve the class for the object
        final Class<?> clazz = element.getClass();
        
        //Resolve the hashCode method to call
        final Method hashCodeMethod = ReflectionUtils.findMethod(clazz, "hashCode");
        
        //hashCode method on the class, simply call it
        if (hashCodeMethod != null && hashCodeMethod.getDeclaringClass() != Object.class) {
            this.append(generator, element);
            return;
        }

        try {
            //Write out the target class so that two classes with the same fields can't collide
            this.append(generator, clazz);
            
            for (Class<?> targetClass = element.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
                final Field[] fields = targetClass.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);

                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    final int modifiers = field.getModifiers();

                    //Ignore static and transient fields
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                        final Object fieldValue = field.get(element);
                        this.deepHashCode(generator, fieldValue);
                    }
                }
            }
        }
        catch (IllegalAccessException exception) {
            ReflectionUtils.handleReflectionException(exception);
        }
    }

    /**
     * Does instanceof checks to determine the correct deepHashCode to call or 
     * {@link #write(KeyGenerationStream, Object)} is called.
     */
    protected final void deepHashCode(G generator, Object element) {
        if (element == null) {
            this.appendNull(generator);
            return;
        }
        
        if (!register(element)) {
            this.appendGraphCycle(generator, element);
            return;
        }
        
        try {
            if (element instanceof byte[])
                this.append(generator, (byte[]) element);
            else if (element instanceof short[])
                this.append(generator, (short[]) element);
            else if (element instanceof int[])
                this.append(generator, (int[]) element);
            else if (element instanceof long[])
                this.append(generator, (long[]) element);
            else if (element instanceof char[])
                this.append(generator, (char[]) element);
            else if (element instanceof float[])
                this.append(generator, (float[]) element);
            else if (element instanceof double[])
                this.append(generator, (double[]) element);
            else if (element instanceof boolean[])
                this.append(generator, (boolean[]) element);
            else if (element instanceof Object[])
                this.deepHashCode(generator, (Object[]) element);
            else if (element instanceof Iterable<?>)
                this.deepHashCode(generator, (Iterable<?>)element);
            else if (element instanceof Map<?, ?>)
                this.deepHashCode(generator, ((Map<?, ?>)element).entrySet());
            else if (element instanceof Map.Entry<?, ?>)
                this.deepHashCode(generator, (Map.Entry<?, ?>)element);
            else if (this.useReflection)
                this.reflectionDeepHashCode(generator, element);
            else
                this.append(generator, element);
        }
        finally {
            unregister(element);
        }
    }
    
    public abstract G getGenerator(Object... data);
    
    public abstract T generateKey(G generator);

    public abstract void append(G generator, boolean a[]);

    public abstract void append(G generator, byte a[]);
    
    public abstract void append(G generator, char a[]);
    
    public abstract void append(G generator, double a[]);
    
    public abstract void append(G generator, float a[]);
    
    public abstract void append(G generator, int a[]);
    
    public abstract void append(G generator, long a[]);
    
    public abstract void append(G generator, short a[]);
    
    public abstract void append(G generator, Object e);

    public abstract void appendGraphCycle(G generator, Object o);

    public abstract void appendNull(G generator);
}
