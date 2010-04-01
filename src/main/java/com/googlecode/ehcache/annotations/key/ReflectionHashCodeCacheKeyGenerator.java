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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.springframework.util.ReflectionUtils;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ReflectionHashCodeCacheKeyGenerator extends HashCodeCacheKeyGenerator {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.ReflectionHashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    public ReflectionHashCodeCacheKeyGenerator() {
    }

    public ReflectionHashCodeCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }

    @Override
    protected long getHashCode(Object o) {
        //Resolve the class for the object
        final Class<?> clazz;
        if (o instanceof Class<?>) {
            clazz = (Class<?>) o;
        }
        else {
            clazz = o.getClass();
        }
        
        //Resolve the hashCode method to call
        final Method hashCodeMethod = ReflectionUtils.findMethod(clazz, "hashCode");
        
        //hashCode method on the class, simply call it
        if (hashCodeMethod != null && hashCodeMethod.getDeclaringClass() != Object.class) {
            return o.hashCode();
        }

        // could not find a hashCode other than the one declared by java.lang.Object
        long result = INITIAL_HASH;

        try {
            for (Class<?> targetClass = o.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
                final Field[] fields = targetClass.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);

                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    final int modifiers = field.getModifiers();

                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && !field.getName().startsWith("this$")) {
                        final Object fieldValue = field.get(o);
                        result = MULTIPLIER * result + this.hashCode(fieldValue);
                    }
                }
            }
        }
        catch (IllegalAccessException exception) {
            ReflectionUtils.handleReflectionException(exception);
        }

        return result;
    }

    protected long hashCode(Collection<?> collection) {
        long result = INITIAL_HASH;
        
        for (final Object element : collection) {
            result = MULTIPLIER * result + this.hashCode(element);
        }

        return result;
    }
}
