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

import java.util.Arrays;
import java.util.Map;

/**
 * This key generator is a good option for fast but rough comparison of method invocations. The standard Java
 * hashCode method is used on the arguments to generate identity information. Essentially {@link Arrays#deepHashCode(Object[])}
 * is used except with a long instead of an int for a hash accumulator to provide a larger key space. This is
 * not a secure hashing algorithm.
 * 
 * <table>
 *  <tr>
 *      <th>Pros</th>
 *      <th>Cons</th>
 *  </tr>
 *  <tr>
 *      <td>
 *          This is the fastest of the included key generation techniques by over 450%
 *      </td>
 *      <td>
 *          The generated hash code is only a long so the key is only 64bits long. It is possible for different
 *          arguments to have the same hash code which would result in a key collision.
 *      </td>
 *  </tr>
 * </table>
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGenerator extends AbstractCacheKeyGenerator<Long> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    protected static final int INITIAL_HASH = 1;
    protected static final int MULTIPLIER = 31;
    
    public HashCodeCacheKeyGenerator() {
    }

    public HashCodeCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }


    @Override
    protected Long generateKey(Object... data) {
        return this.deepHashCode(data);
    }

    /**
     * @see Arrays#hashCode(long[])
     */
    protected long hashCode(long a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final long element : a) {
            int elementHash = (int)(element ^ (element >>> 32));
            result = MULTIPLIER * result + elementHash;
        }

        return result;
    }

    /**
     * @see Arrays#hashCode(int[])
     */
    protected long hashCode(int a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final int element : a)
            result = MULTIPLIER * result + element;

        return result;
    }

    /**
     * @see Arrays#hashCode(short[])
     */
    protected long hashCode(short a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final short element : a)
            result = MULTIPLIER * result + element;

        return result;
    }

    /**
     * @see Arrays#hashCode(char[])
     */
    protected long hashCode(char a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final char element : a)
            result = MULTIPLIER * result + element;

        return result;
    }

    /**
     * @see Arrays#hashCode(byte[])
     */
    protected long hashCode(byte a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final byte element : a)
            result = MULTIPLIER * result + element;

        return result;
    }

    /**
     * @see Arrays#hashCode(boolean[])
     */
    protected long hashCode(boolean a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final boolean element : a)
            result = MULTIPLIER * result + (element ? 1231 : 1237);

        return result;
    }

    /**
     * @see Arrays#hashCode(float[])
     */
    protected long hashCode(float a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final float element : a)
            result = MULTIPLIER * result + Float.floatToIntBits(element);

        return result;
    }

    /**
     * @see Arrays#hashCode(double[])
     */
    protected long hashCode(double a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;
        for (final double element : a) {
            long bits = Double.doubleToLongBits(element);
            result = MULTIPLIER * result + (int)(bits ^ (bits >>> 32));
        }
        return result;
    }

    /**
     * @see Arrays#deepHashCode(Object[])
     */
    protected long deepHashCode(Object a[]) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;

        for (final Object element : a) {
            final long elementHash = this.hashCode(element);
            result = MULTIPLIER * result + elementHash;
        }

        return result;
    }


    /**
     * @see Arrays#deepHashCode(Object[])
     */
    protected long deepHashCode(Iterable<?> a) {
        if (a == null)
            return 0;

        long result = INITIAL_HASH;

        for (final Object element : a) {
            final long elementHash = this.hashCode(element);
            result = MULTIPLIER * result + elementHash;
        }

        return result;
    }
    
    protected long hashCode(Map.Entry<?, ?> e) {
        if (e == null)
            return 0;
        
        long result = INITIAL_HASH;

        final long keyHash = this.hashCode(e.getKey());
        result = MULTIPLIER * result + keyHash;

        final long valueHash = this.hashCode(e.getValue());
        result = MULTIPLIER * result + valueHash;

        return result;
    }

    protected final long hashCode(Object element) {
        if (element == null || !register(element)) {
            //Return 0 in place of the actual hash code in the case of a circular reference
            return 0;
        }
        try {
            long elementHash = 0;
            if (element instanceof Object[])
                elementHash = deepHashCode((Object[]) element);
            else if (element instanceof byte[])
                elementHash = hashCode((byte[]) element);
            else if (element instanceof short[])
                elementHash = hashCode((short[]) element);
            else if (element instanceof int[])
                elementHash = hashCode((int[]) element);
            else if (element instanceof long[])
                elementHash = hashCode((long[]) element);
            else if (element instanceof char[])
                elementHash = hashCode((char[]) element);
            else if (element instanceof float[])
                elementHash = hashCode((float[]) element);
            else if (element instanceof double[])
                elementHash = hashCode((double[]) element);
            else if (element instanceof boolean[])
                elementHash = hashCode((boolean[]) element);
            else if (element instanceof Class<?>)
                elementHash = getHashCode((Class<?>)element);
            else if (element instanceof Iterable<?>)
                elementHash = deepHashCode((Iterable<?>)element);
            else if (element instanceof Map<?, ?>)
                elementHash = deepHashCode(((Map<?, ?>)element).entrySet());
            else if (element instanceof Map.Entry<?, ?>)
                elementHash = hashCode((Map.Entry<?, ?>)element);
            else
                elementHash = getHashCode(element);
            return elementHash;
        }
        finally {
            unregister(element);
        }
    }
    
    /**
     * Generate hash code for an Class which by default uses object identity hash codes
     */
    protected long getHashCode(Class<?> c) {
        return c.getName().hashCode();
    }
    
    /**
     * Generate hash code for an object
     */
    protected long getHashCode(Object o) {
        return o.hashCode();
    }
}
