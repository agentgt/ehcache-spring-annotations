package edu.wisc.services.cache.impl;

import java.lang.reflect.Method;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultCacheKey)) {
            return false;
        }
        DefaultCacheKey other = (DefaultCacheKey) obj;
        if (method == null) {
            if (other.method != null) {
                return false;
            }
        }
        else if (!method.equals(other.method)) {
            return false;
        }
        if (targetClass == null) {
            if (other.targetClass != null) {
                return false;
            }
        }
        else if (!targetClass.equals(other.targetClass)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultCacheKey [method=" + method + ", targetClass=" + targetClass + "]";
    }
}