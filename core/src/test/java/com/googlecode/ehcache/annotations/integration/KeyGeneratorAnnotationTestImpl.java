/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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
