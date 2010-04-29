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

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

/**
 * Specifies and configures a {@link CacheKeyGenerator} 
 */
@Target( { ElementType.ANNOTATION_TYPE } )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface KeyGenerator {
    /**
     * The class name of the generator to use. If one of the included generators in the <code>com.googlecode.ehcache.annotations.key</code>
     * package is used only the class name is required. If a different generator is used the fully qualified class name
     * must be specified.
     */
    String name();
    
    /**
     * Configuration parameters for the generator. These are treated as bean properties.
     */
    Parameter[] parameters() default {};
}
