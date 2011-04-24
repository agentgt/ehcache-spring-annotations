/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.google.common.collect.MapMaker;


/**
 * {@link ReflectionHelper} that uses a cache to track reflection operations. A background thread is started on first
 * usage to perform cache cleanup to avoid class reference leaks. Code that uses this class should call {@link #destroy()}
 * when complete to ensure the cleanup thread shuts down.  
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public final class CachingReflectionHelper implements ReflectionHelper {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private enum ImplementsMethod {
        HASH_CODE,
        EQUALS,
        TO_STRING;
    }
    
    private final Map<Class<?>, Set<ImplementsMethod>> implementsCache = new MapMaker().weakKeys().makeMap();


    private Map<Class<?>, Set<ImplementsMethod>> getCache() {
        return this.implementsCache;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.util.guice.ReflectionHelper#implementsHashCode(java.lang.Object)
     */
    public boolean implementsHashCode(Object element) {
        return this.doesImplement(element.getClass(), ImplementsMethod.HASH_CODE);
    }
    
    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.util.guice.ReflectionHelper#implementsEquals(java.lang.Object)
     */
    public boolean implementsEquals(Object element) {
        return this.doesImplement(element.getClass(), ImplementsMethod.EQUALS);
    }
    
    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.util.guice.ReflectionHelper#implementsToString(java.lang.Object)
     */
    public boolean implementsToString(Object element) {
        return this.doesImplement(element.getClass(), ImplementsMethod.TO_STRING);
    }
    
    public void clearCache() {
        final Map<Class<?>, Set<ImplementsMethod>> cache = this.getCache();
        cache.clear();
    }
    
    /**
     * Scans a class to see if it implements the hashCode, toString and equals methods which are commonly
     * used by key generators
     */
    private boolean doesImplement(final Class<?> elementClass, ImplementsMethod method) {
        final Map<Class<?>, Set<ImplementsMethod>> cache = this.getCache();
        Set<ImplementsMethod> methodCache = cache.get(elementClass);

        if (methodCache == null) {
            methodCache = EnumSet.noneOf(ImplementsMethod.class);
            cache.put(elementClass, methodCache);

            //Create final reference for use by anonymous class
            final Set<ImplementsMethod> implementsSet = methodCache;
            ReflectionUtils.doWithMethods(elementClass, new MethodCallback() {
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    if (implementsSet.size() == 3 || method.getDeclaringClass() == Object.class) {
                        return;
                    }
                    
                    if (ReflectionUtils.isEqualsMethod(method)) {
                        implementsSet.add(ImplementsMethod.EQUALS);
                    }
                    else if (ReflectionUtils.isHashCodeMethod(method)) {
                        implementsSet.add(ImplementsMethod.HASH_CODE);
                    }
                    else if (ReflectionUtils.isToStringMethod(method)) {
                        implementsSet.add(ImplementsMethod.TO_STRING);
                    }
                }
            });
        }
        
        return methodCache.contains(method);
    }
}
