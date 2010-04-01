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

package com.googlecode.ehcache.annotations.integration;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheKeyGeneratorTestImpl implements CacheKeyGeneratorTestInterface {
    private int singleCachedCall = 0;
    
    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.integration.CacheConfigurationTestInterface#singleCachedCall(java.lang.String)
     */
    public String singleCachedCall(String argument) {
        this.singleCachedCall++;
        return "singleCachedCall(" + argument + ")";
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.integration.CacheConfigurationTestInterface#singleCachedCallCount()
     */
    public int singleCachedCallCount() {
        return this.singleCachedCall;
    }

    private int callListCacheKeyGenerator = 0;
    public String callListCacheKeyGenerator(String argument) {
        callListCacheKeyGenerator++;
        return "callListCacheKeyGenerator(" + argument + ")";
    }
    public int callListCacheKeyGeneratorCount() {
        return callListCacheKeyGenerator;
    }

    private int callHashCodeCacheKeyGenerator = 0;
    public String callHashCodeCacheKeyGenerator(String argument) {
        callHashCodeCacheKeyGenerator++;
        return "callHashCodeCacheKeyGenerator(" + argument + ")";
    }
    public int callHashCodeCacheKeyGeneratorCount() {
        return callHashCodeCacheKeyGenerator;
    }

    private int callMessageDigestCacheKeyGenerator = 0;
    public String callMessageDigestCacheKeyGenerator(String argument) {
        callMessageDigestCacheKeyGenerator++;
        return "callMessageDigestCacheKeyGenerator(" + argument + ")";
    }
    public int callMessageDigestCacheKeyGeneratorCount() {
        return callMessageDigestCacheKeyGenerator;
    }

    private int callReflectionHashCodeCacheKeyGenerator = 0;
    public String callReflectionHashCodeCacheKeyGenerator(String argument) {
        callReflectionHashCodeCacheKeyGenerator++;
        return "callReflectionHashCodeCacheKeyGenerator(" + argument + ")";
    }
    public int callReflectionHashCodeCacheKeyGeneratorCount() {
        return callReflectionHashCodeCacheKeyGenerator;
    }

    private int callStringCacheKeyGenerator = 0;
    public String callStringCacheKeyGenerator(String argument) {
        callStringCacheKeyGenerator++;
        return "callStringCacheKeyGenerator(" + argument + ")";
    }
    public int callStringCacheKeyGeneratorCount() {
        return callStringCacheKeyGenerator;
    }
}
