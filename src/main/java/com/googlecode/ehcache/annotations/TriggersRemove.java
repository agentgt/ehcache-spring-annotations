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

package com.googlecode.ehcache.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.resolver.CacheResolverFactory;


/**
 * Annotation for methods who's calls trigger removal of data from a cache
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TriggersRemove {
    /**
     * The name(s) of the cache to use. Note that the same {@link CacheKeyGenerator} is used for all caches.
     */
    String[] cacheName();

    /**
     * If a call should remove all elements from the cache. Will result in the specified keyGeneratorName 
     * being ignored.
     */
    boolean removeAll() default false;
    
    /**
     * The Spring Bean name of the {@link CacheKeyGenerator} to use for all caches.
     * Ignored if {@link #keyGenerator()} is specified. 
     * If this and {@link #keyGenerator()} are not specified the default generator will be used.
     * If {@link #removeAll()} is true no {@link CacheKeyGenerator} is used.
     */
    String keyGeneratorName() default "";
    
    /**
     * Used the specify and configure the {@link CacheKeyGenerator} to use for all caches.
     * If this and {@link #keyGeneratorName()} are not specified the default generator will be used.  
     * If {@link #removeAll()} is true no {@link CacheKeyGenerator} is used.
     */
    KeyGenerator keyGenerator() default @KeyGenerator(name = "");
    
    /**
     * Used to specify 'when' to run the ehcache remove call:
     * before or after method invocation.
     * If you specify {@link When#AFTER_METHOD_INVOCATION} and your method invocation throws
     * an exception, the ehcache remove call WILL NOT execute.
     * Default value is {@link When#BEFORE_METHOD_INVOCATION}.
     */
    When when() default When.BEFORE_METHOD_INVOCATION;
    
    /**
     * The Spring Bean name of the {@link CacheResolverFactory} to use to resolve the correct cache at runtime.
     * Ignored if {@link #resolverFactory()} is specified.
     * If this and {@link #resolverFactory()} are not specified the specified {@link #cacheName()} will be used.
     */
    String resolverFactoryName() default "";
    
    /**
     * Used the specify and configure the {@link CacheResolverFactory} to use.
     * If this and {@link #resolverFactoryName()} are not specified the specified {@link #cacheName()} will be used. 
     */
    ResolverFactory resolverFactory() default @ResolverFactory(name = "");
}
