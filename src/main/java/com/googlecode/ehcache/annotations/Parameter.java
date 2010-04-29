/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ehcache.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration parameter. Either {@link #value()} or {@link #ref()} must be specified.
 */
@Target( { ElementType.ANNOTATION_TYPE } )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Parameter {
    /**
     * Name of the parameter
     */
    String name();
    /**
     * Value of the parameter
     */
    String value() default "";
    /**
     * Spring bean reference to use as the value for the parameter
     */
    String ref() default "";
}
