/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.services.cache.impl;

import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import edu.wisc.services.cache.AdviceType;
import edu.wisc.services.cache.CacheAttributeSource;

/**
 * Pointcut that delegates matching checks to a {@link CacheAttributeSource}.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheStaticMethodMatcherPointcut extends StaticMethodMatcherPointcut {
    private CacheAttributeSource cacheAttributeSource;
    
    public void setCacheAttributeSource(CacheAttributeSource cacheAttributeSource) {
        this.cacheAttributeSource = cacheAttributeSource;
    }

    /**
     * Returns true if the configured {@link CacheAttributeSource#getAdviceType(Method, Class)}
     * method returns an {@link AdviceType} other than {@link AdviceType#NONE}.
     * @see org.springframework.aop.MethodMatcher#matches(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return AdviceType.NONE != this.cacheAttributeSource.getAdviceType(method, targetClass);
    }
}
