/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ReflectionHashCodeCacheKeyGenerator extends SimpleHashCodeCacheKeyGenerator {
    
    public ReflectionHashCodeCacheKeyGenerator() {
    }

    public ReflectionHashCodeCacheKeyGenerator(boolean includeMethod) {
        super(includeMethod);
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
        final Method hashCodeMethod = ReflectionUtils.findMethod(clazz, "hashCode", new Class[0]);
        
        //No hashCode method on the class, simply call it on the object passed in
        if (hashCodeMethod != null) {
            return o.hashCode();
        }

        // could not find a hashCode other than the one declared by java.lang.Object
        long result = 1;

        try {
            for (Class<?> targetClass = o.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
                final Field[] fields = targetClass.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);

                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();

                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                        final Object fieldValue = field.get(o);
                        result = 31 * result + this.hashCode(fieldValue);
                    }
                }
            }
        }
        catch (IllegalAccessException exception) {
            ReflectionUtils.handleReflectionException(exception);
        }

        return result;
    }
    
}
