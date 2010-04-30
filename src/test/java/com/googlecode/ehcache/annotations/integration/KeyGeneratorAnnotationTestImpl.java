/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ehcache.annotations.integration;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class KeyGeneratorAnnotationTestImpl implements KeyGeneratorAnnotationTestInterface {
    private int listCacheKeyGeneratorCount = 0;

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.integration.KeyGeneratorAnnotationTestInterface#listCacheKeyGenerator()
     */
    public String listCacheKeyGenerator() {
        listCacheKeyGeneratorCount++;
        return "listCacheKeyGenerator()";
    }

    public int listCacheKeyGeneratorCount() {
        return listCacheKeyGeneratorCount;
    }

    public String customGeneratorFour() {
        return "customGeneratorFour()";
    }

    public String customGeneratorOne() {
        return "customGeneratorOne()";
    }

    public String customGeneratorThree() {
        return "customGeneratorThree()";
    }

    public String customGeneratorTwo() {
        return "customGeneratorTwo()";
    }
}
