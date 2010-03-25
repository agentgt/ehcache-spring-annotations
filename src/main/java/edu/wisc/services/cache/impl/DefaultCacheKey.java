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

package edu.wisc.services.cache.impl;

import java.lang.reflect.Method;

import org.springframework.util.ObjectUtils;

/**
 * Default cache key for the CacheableAttribute cache.
 */
class DefaultCacheKey {
    private final Method method;
    private final Class<?> targetClass;

    public DefaultCacheKey(Method method, Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultCacheKey)) {
            return false;
        }
        final DefaultCacheKey otherKey = (DefaultCacheKey) other;
        return (this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
    }

    @Override
    public int hashCode() {
        return this.method.hashCode() * 29 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "DefaultCacheKey [method=" + method + ", targetClass=" + targetClass + "]";
    }
}