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