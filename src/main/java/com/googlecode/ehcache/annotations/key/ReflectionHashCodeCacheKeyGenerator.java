/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
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
