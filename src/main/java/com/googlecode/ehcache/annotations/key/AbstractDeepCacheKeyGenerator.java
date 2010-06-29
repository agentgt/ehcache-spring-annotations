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
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;


/**
 * Base class for key generators that do deep inspection of the key data for generation. Arrays, 
 * {@link Iterable} and {@link Map} are iterated over and their values recursively inspected. Also
 * supports reflective recursion which can be useful for objects that may not support the hashCode,
 * equals or other methods required by the key generation implementation.
 * 
 * Reflective recursion add significant overhead to the deep inspection process.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractDeepCacheKeyGenerator<G, T extends Serializable> extends AbstractCacheKeyGenerator<T> implements ReflectionHelperAware {
    private ReflectionHelper reflectionHelper = new SimpleReflectionHelper();
    private boolean useReflection = false;
    
    public AbstractDeepCacheKeyGenerator() {
        super();
    }

    public AbstractDeepCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    public ReflectionHelper getReflectionHelper() {
        return reflectionHelper;
    }

    public void setReflectionHelper(ReflectionHelper reflectionHelper) {
        Assert.notNull(reflectionHelper);
        this.reflectionHelper = reflectionHelper;
    }

    public final boolean isUseReflection() {
        return this.useReflection;
    }

    /**
     * Determines if reflection should be used on each object that is used in key generation. If true
     * each object that is added to the key has {@link #shouldReflect(Object)} called on it. If that
     * returns true reflection is used to recurse on all of the fields of the object.
     * 
     * @param useReflection Defaults to false.
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
     * Calls {@link #deepHashCode(Object, Object)} on each element in the array.
     * @param a will never be null
     */
    protected void deepHashCode(G generator, Object a[]) {
        this.beginRecursion(generator, a);
        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
        this.endRecursion(generator, a);
    }


    /**
     * Calls {@link #deepHashCode(Object, Object)} on each element in the {@link Iterable}
     * @param a will never be null
     */
    protected void deepHashCode(G generator, Iterable<?> a) {
        this.beginRecursion(generator, a);
        for (final Object element : a) {
            this.deepHashCode(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Calls {@link #deepHashCode(Object, Object)} on both the key and the value.
     * @param e will never be null
     */
    protected void deepHashCode(G generator, Map.Entry<?, ?> e) {
        this.beginRecursion(generator, e);
        this.deepHashCode(generator, e.getKey());
        this.deepHashCode(generator, e.getValue());
        this.endRecursion(generator, e);
    }

    /**
     * Does instanceof checks to determine the correct {@link #deepHashCode} method to call or 
     * {@link #append(Object, Object)} is called if no other recursion is needed or
     * {@link #reflectionDeepHashCode(Object, Object)} is called if {@link #setUseReflection(boolean)}
     * is true.
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
     * Calls {@link #shouldReflect(Object)} to determine if the object needs to be reflected on to
     * generate a good key. If so {@link AccessibleObject#setAccessible(AccessibleObject[], boolean)} is
     * used to enable access to private, protected and default fields. Each non-transient, non-static field
     * has {@link #deepHashCode(Object, Object)} called on it.
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
     * @param element will never be null
     */
    protected boolean shouldReflect(Object element) {
        return !this.reflectionHelper.implementsHashCode(element) || !this.reflectionHelper.implementsEquals(element);
    }
    
    
    /**
     * Create the object used to generate the key. This object is passed into every {@link #deepHashCode} and
     * {@link #append} call.
     */
    protected abstract G getGenerator(Object... data);
    
    /**
     * Generate the cache key from the generator
     */
    protected abstract T generateKey(G generator);
    
    /**
     * Append an object to the key.
     */
    protected abstract void append(G generator, Object e);

    /**
     * Called if a graph cycle is detected.
     * @param o The object that started the cycle
     */
    protected abstract void appendGraphCycle(G generator, Object o);

    /**
     * Called if a null value is found in the object graph
     */
    protected abstract void appendNull(G generator);

    /**
     * Append a boolean array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle boolean or boolean array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, boolean a[]) {
        this.beginRecursion(generator, a);
        for (final boolean element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }

    /**
     * Append a byte array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle byte or byte array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, byte a[]) {
        this.beginRecursion(generator, a);
        for (final byte element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a char array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle char or char array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, char a[]) {
        this.beginRecursion(generator, a);
        for (final char element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a double array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle double or double array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, double a[]) {
        this.beginRecursion(generator, a);
        for (final double element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a float array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle float or float array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, float a[]) {
        this.beginRecursion(generator, a);
        for (final float element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a int array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle int or int array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, int a[]) {
        this.beginRecursion(generator, a);
        for (final int element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a long array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle long or long array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, long a[]) {
        this.beginRecursion(generator, a);
        for (final long element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Append a short array. Calls {@link #append(Object, Object)} on each value in
     * the array. If the implementing class can handle short or short array primitives 
     * directly this should be overridden to avoid auto-boxing each array element.
     */
    protected void append(G generator, short a[]) {
        this.beginRecursion(generator, a);
        for (final short element : a) {
            this.append(generator, element);
        }
        this.endRecursion(generator, a);
    }
    
    /**
     * Called before each array/{@link Iterable}/{@link Map} is handled. Useful for sub-classes that
     * want to be aware of the tree structure of the object graph.
     */
    protected void beginRecursion(G generator, Object e) {
    }
    
    /**
     * Called after each array/{@link Iterable}/{@link Map} is handled. Useful for sub-classes that
     * want to be aware of the tree structure of the object graph.
     */
    protected void endRecursion(G generator, Object e) {
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" +
                "includeMethod=" + isIncludeMethod() + ", " +
                "includeParameterTypes="+ isIncludeParameterTypes() + ", " + 
                "useReflection=" + isUseReflection() + ", " +
                "checkforCycles="+ isCheckforCycles() + 
                "]";
    }
}
