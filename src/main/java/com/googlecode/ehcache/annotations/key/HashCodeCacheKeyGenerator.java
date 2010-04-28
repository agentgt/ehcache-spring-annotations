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


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGenerator extends AbstractDeepCacheKeyGenerator<HashCodeCacheKeyGenerator.LongGenerator, Long> {
    /**
     * Name of the bean this generator is registered under using the default constructor.
     */
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    protected static final int INITIAL_HASH = 1;
    protected static final int MULTIPLIER = 31;
    
    /**
     * Little utility class to fake a mutable long
     */
    public static class LongGenerator {
        private long hash = INITIAL_HASH;
        
        private LongGenerator() {
        }
    }
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public HashCodeCacheKeyGenerator() {
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean) 
     */
    public HashCodeCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }

    @Override
    public LongGenerator getGenerator(Object... data) {
        return new LongGenerator();
    }

    @Override
    public Long generateKey(LongGenerator generator) {
        return generator.hash;
    }
    
    @Override
    protected void append(LongGenerator generator, long a[]) {
        for (final long element : a) {
            generator.hash = MULTIPLIER * generator.hash + element;
        }
    }

    @Override
    protected void append(LongGenerator generator, int a[]) {
        for (final int element : a) {
            generator.hash = MULTIPLIER * generator.hash + element;
        }
    }

    @Override
    protected void append(LongGenerator generator, short a[]) {
        for (final short element : a) {
            generator.hash = MULTIPLIER * generator.hash + element;
        }
    }

    @Override
    protected void append(LongGenerator generator, char a[]) {
        for (final char element : a) {
            generator.hash = MULTIPLIER * generator.hash + element;
        }
    }

    @Override
    protected void append(LongGenerator generator, byte a[]) {
        for (final byte element : a) {
            generator.hash = MULTIPLIER * generator.hash + element;
        }
    }

    @Override
    protected void append(LongGenerator generator, boolean a[]) {
        for (final boolean element : a) {
            generator.hash = MULTIPLIER * generator.hash + (element ? 1231 : 1237);
        }
    }

    @Override
    protected void append(LongGenerator generator, float a[]) {
        for (final float element : a) {
            generator.hash = MULTIPLIER * generator.hash + Float.floatToIntBits(element);
        }
    }

    @Override
    protected void append(LongGenerator generator, double a[]) {
        for (final double element : a) {
            generator.hash = MULTIPLIER * generator.hash + Double.doubleToLongBits(element);
        }
    }

    @Override
    protected void appendGraphCycle(LongGenerator generator, Object o) {
        generator.hash = MULTIPLIER * generator.hash;
    }

    @Override
    protected void appendNull(LongGenerator generator) {
        generator.hash = MULTIPLIER * generator.hash;
    }

    @Override
    protected void append(LongGenerator generator, Object e) {
        if (e instanceof Class<?>) {
            this.append(generator, ((Class<?>)e).getName());
        }
        else if (e instanceof Enum<?>) {
            this.append(generator, ((Enum<?>)e).getClass().getName());
            this.append(generator, ((Enum<?>)e).name());
        }
        else if (e instanceof Double) {
            generator.hash = MULTIPLIER * generator.hash + Double.doubleToLongBits(((Double)e).doubleValue());
        }
        else if (e instanceof Long) {
            generator.hash = MULTIPLIER * generator.hash + ((Long)e).longValue();
        }
        else {        
            generator.hash = MULTIPLIER * generator.hash + e.hashCode();
        }
    }

    @Override
    protected boolean shouldReflect(Object element) {
        return !this.implementsHashCode(element);
    }
}
