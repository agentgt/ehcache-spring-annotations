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

import com.googlecode.ehcache.annotations.resolver.CacheResolverFactory;

/**
 * Specifies and configures a {@link CacheResolverFactory} in a Spring {@link org.springframework.context.ApplicationContext} that is a
 * child of the application's context. All beans available in the application's context can be referenced
 * by a {@link CacheResolverFactory} configured this way.
 */
@Target( { ElementType.ANNOTATION_TYPE } )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResolverFactory {
    /**
     * The class name of the resolver to use. If one of the included resolver in the <code>com.googlecode.ehcache.annotations.resolver</code>
     * package is used only the class name is required. If a class outside that package is used the fully qualified class name
     * must be specified.
     */
    String name();
    
    /**
     * Bean properties used to configure the resolver.
     */
    Property[] properties() default {};
}
