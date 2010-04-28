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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

/**
 * Base class for key generators that do deep inspection of the key data for generation. Arrays and Collections are
 * iterated over and their values recursively inspected. Also supports reflective recursion which can be useful for
 * objects that may not support the hashCode, equals or other methods required by the key generation implementation.
 * 
 * Reflective recursion add significant overhead to the deep inspection process.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractDeepCacheKeyGenerator<G, T extends Serializable> extends AbstractCacheKeyGenerator<T> {
    private boolean useReflection = false;
    
    public AbstractDeepCacheKeyGenerator() {
        super();
    }

    public AbstractDeepCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }

    public final boolean isUseReflection() {
        return this.useReflection;
    }

    /**
     * Sets if reflection should be used when recursing over 
     */
    public final void setUseReflection(boolean useReflection) {
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
    protected void deepHashCode(G generator, Object a[]) {
        this.beginRecursion(generator, a);
        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
        this.endRecursion(generator, a);
    }


    /**
     * Calls {@link #deepHashCode(KeyGenerationStream, Object)} on each element in the {@link Iterable}
     */
    protected void deepHashCode(G generator, Iterable<?> a) {
        this.beginRecursion(generator, a);
        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Calls {@link #deepHashCode(KeyGenerationStream, Object)} on both the key and the value.
     */
    protected void deepHashCode(G generator, Map.Entry<?, ?> e) {
        this.beginRecursion(generator, e);
        this.deepHashCode(generator, e.getKey());
        this.deepHashCode(generator, e.getValue());
        this.endRecursion(generator, e);
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
    
    /**
     * If {@link #shouldReflect(Object)} returns true it uses reflection to call
     * {@link #deepHashCode(Object, Object)} on each non-transient, non-static field.
     */
    protected final void reflectionDeepHashCode(G generator, final Object element) {
        //Special objects which shouldn't be reflected on due to lack of interesting fields
        if (element instanceof Class<?>) {
            this.append(generator, element);
            return;
        }

        //Determine if the element should be reflected on
        if (!this.shouldReflect(element)) {
            this.append(generator, element);
            return;
        }
        
        //Accumulate the data that makes up the object being reflected on so it can be recursed on as a single grouping of data
        final List<Object> reflectiveObject = new LinkedList<Object>();

        //Write out the target class so that two classes with the same fields can't collide
        reflectiveObject.add(element.getClass());

        try {
            for (Class<?> targetClass = element.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
                final Field[] fields = targetClass.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);

                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    final int modifiers = field.getModifiers();

                    //Ignore static and transient fields
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                        final Object fieldValue = field.get(element);
                        reflectiveObject.add(fieldValue);
                    }
                }
            }
        }
        catch (IllegalAccessException exception) {
            ReflectionUtils.handleReflectionException(exception);
        }
        
        this.deepHashCode(generator, reflectiveObject);
    }
    
    /**
     * Default implementation returns true if the {@link Object} doesn't implement hashCode or
     * doesn't implement equals. 
     */
    protected boolean shouldReflect(Object element) {
        return !this.implementsHashCode(element) || !this.implementsEquals(element);
    }
    
    /**
     * Checks if the object implements hashCode
     */
    protected final boolean implementsHashCode(Object element) {
        final Method hashCodeMethod = ReflectionUtils.findMethod(element.getClass(), "hashCode");
        return hashCodeMethod != null && hashCodeMethod.getDeclaringClass() != Object.class;
    }
    
    /**
     * Checks if the object implements equals
     */
    protected final boolean implementsEquals(Object element) {
        final Method equalsMethod = ReflectionUtils.findMethod(element.getClass(), "equals");
        return equalsMethod != null && equalsMethod.getDeclaringClass() != Object.class;
    }
    
    /**
     * Create the object used to generate the key.
     */
    protected abstract G getGenerator(Object... data);
    
    /**
     * Generate the cache key from the generator
     */
    protected abstract T generateKey(G generator);
    
    protected abstract void append(G generator, Object e);

    protected abstract void appendGraphCycle(G generator, Object o);

    protected abstract void appendNull(G generator);

    protected void append(G generator, boolean a[]) {
        this.beginRecursion(generator, a);
        for (final boolean element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }

    protected void append(G generator, byte a[]) {
        this.beginRecursion(generator, a);
        for (final byte element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void append(G generator, char a[]) {
        this.beginRecursion(generator, a);
        for (final char element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    protected void append(G generator, double a[]) {
        this.beginRecursion(generator, a);
        for (final double element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void append(G generator, float a[]) {
        this.beginRecursion(generator, a);
        for (final float element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void append(G generator, int a[]) {
        this.beginRecursion(generator, a);
        for (final int element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void append(G generator, long a[]) {
        this.beginRecursion(generator, a);
        for (final long element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void append(G generator, short a[]) {
        this.beginRecursion(generator, a);
        for (final short element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    protected void beginRecursion(G generator, Object e) {
    }
    
    protected void endRecursion(G generator, Object e) {
    }
}
