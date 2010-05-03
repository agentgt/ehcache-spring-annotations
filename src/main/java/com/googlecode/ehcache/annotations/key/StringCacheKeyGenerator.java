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

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StringCacheKeyGenerator extends AbstractDeepCacheKeyGenerator<StringCacheKeyGenerator.StringGenerator, String> {
    /**
     * Name of the bean this generator is registered under using the default constructor.
     */
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.StringCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public StringCacheKeyGenerator() {
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean)
     */
    public StringCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    public static class StringGenerator {
        private final StringBuilder builder = new StringBuilder();
        private int depth = 0;
        
        private StringGenerator() {
        }
    }

    @Override
    protected StringCacheKeyGenerator.StringGenerator getGenerator(Object... data) {
        return new StringGenerator();
    }

    @Override
    protected String generateKey(StringCacheKeyGenerator.StringGenerator generator) {
        return generator.builder.toString();
    }
    
    @Override
    protected void beginRecursion(StringCacheKeyGenerator.StringGenerator generator, Object e) {
        generator.builder.append("[");
        generator.depth++;
    }

    @Override
    protected void endRecursion(StringCacheKeyGenerator.StringGenerator generator, Object e) {
        final int length = generator.builder.length();
        if (generator.depth > 0 && length > 2) {
            generator.builder.delete(length - 2, length);
        }
        
        if (generator.depth > 1) {
            generator.builder.append("], ");
        }
        else {
            generator.builder.append("]");
        }
        
        generator.depth--;
    }
    

    @Override
    protected void append(StringGenerator generator, boolean[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, byte[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, char[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, double[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, float[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, int[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, long[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringGenerator generator, short[] a) {
        generator.builder.append("[");
        if (a.length > 0) {
            int index = 0;
            for (; index < a.length - 1; index++) {
                generator.builder.append(a[index]).append(", ");
            }
        
            generator.builder.append(a[a.length - 1]);
        }
        generator.builder.append("], ");
    }

    @Override
    protected void append(StringCacheKeyGenerator.StringGenerator generator, Object e) {
        generator.builder.append(e.toString()).append(", ");
    }

    @Override
    protected void appendGraphCycle(StringCacheKeyGenerator.StringGenerator generator, Object o) {
        generator.builder.append("[...], ");
    }

    @Override
    protected void appendNull(StringCacheKeyGenerator.StringGenerator generator) {
        generator.builder.append("null, ");
    }

    @Override
    protected boolean shouldReflect(Object element) {
        return !this.implementsToString(element);
    }
    
    /**
     * Checks if the object implements equals
     */
    protected final boolean implementsToString(Object element) {
        final Method toStringMethod = ReflectionUtils.findMethod(element.getClass(), "toString");
        return toStringMethod != null && toStringMethod.getDeclaringClass() != Object.class;
    }
    
}
