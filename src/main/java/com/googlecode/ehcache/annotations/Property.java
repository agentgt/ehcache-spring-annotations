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

/**
 * A bean property used to configure the generator. Either {@link #value()} or {@link #ref()} must be specified
 * but not both.
 */
@Target( { ElementType.ANNOTATION_TYPE } )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Property {
    /**
     * Name of the bean property
     */
    String name();
    /**
     * Value of the bean property.
     */
    String value() default "";
    /**
     * Name of a bean in the Spring application context to inject a reference to.
     */
    String ref() default "";
}
