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

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.resolver.CacheResolverFactory;

/**
 * Annotation for methods whose return values should be cached.
 */
@Target( { ElementType.METHOD } )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {
    /**
     * The name of the cache to use
     */
    String cacheName();

    /**
     * If a EhCache {@link SelfPopulatingCache} wrapper should be used to ensure only one
     * value per key is created.
     */
    boolean selfPopulating() default false;
    
    /**
     * The Spring Bean name of the {@link CacheKeyGenerator} to use.
     * Ignored if {@link #keyGenerator()} is specified. 
     * If this and {@link #keyGenerator()} are not specified the default generator will be used.
     */
    String keyGeneratorName() default "";
    
    /**
     * The name of the cache to use for storing exceptions thrown by the advised method. If not specified
     * exceptions will result in no caching taking place. 
     */
    String exceptionCacheName() default "";
    
    /**
     * Used the specify and configure the {@link CacheKeyGenerator} to use.
     * If this and {@link #keyGeneratorName()} are not specified the default generator will be used.  
     */
    KeyGenerator keyGenerator() default @KeyGenerator(name = "");
    
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
