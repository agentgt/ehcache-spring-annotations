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
import org.springframework.util.ReflectionUtils.MethodCallback;

/**
 * Basic implementation of {@link ReflectionHelper} that determines which methods an object implements. The
 * reflection results are not cached so this implementation is fairly slow.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SimpleReflectionHelper implements ReflectionHelper {
    private static final class MutableBoolean {
        public boolean value = false;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.key.ReflectionHelper#implementsEquals(java.lang.Object)
     */
    public boolean implementsEquals(Object element) {
        final MutableBoolean found = new MutableBoolean();
        
        ReflectionUtils.doWithMethods(element.getClass(), new MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (found.value || method.getDeclaringClass() == Object.class) {
                    return;
                }
                
                if (ReflectionUtils.isEqualsMethod(method)) {
                    found.value = true;
                }
            }
        });
        
        return found.value;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.key.ReflectionHelper#implementsHashCode(java.lang.Object)
     */
    public boolean implementsHashCode(Object element) {
        final MutableBoolean found = new MutableBoolean();
        
        ReflectionUtils.doWithMethods(element.getClass(), new MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (found.value || method.getDeclaringClass() == Object.class) {
                    return;
                }
                
                if (ReflectionUtils.isHashCodeMethod(method)) {
                    found.value = true;
                }
            }
        });
        
        return found.value;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.key.ReflectionHelper#implementsToString(java.lang.Object)
     */
    public boolean implementsToString(Object element) {
        final MutableBoolean found = new MutableBoolean();
        
        ReflectionUtils.doWithMethods(element.getClass(), new MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (found.value || method.getDeclaringClass() == Object.class) {
                    return;
                }
                
                if (ReflectionUtils.isToStringMethod(method)) {
                    found.value = true;
                }
            }
        });
        
        return found.value;
    }
}
