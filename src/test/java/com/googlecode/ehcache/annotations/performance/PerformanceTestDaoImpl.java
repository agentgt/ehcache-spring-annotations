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

package com.googlecode.ehcache.annotations.performance;

import java.util.Arrays;
import java.util.Set;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class PerformanceTestDaoImpl implements PerformanceTestDao {

    @Cacheable(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestOne() {
        return "performanceTestOne()";
    }

    @Cacheable(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestTwo(StringBuilder arg1) {
        return "performanceTestTwo(" + arg1 + ")";
    }

    @Cacheable(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestThree(int[] arg1, String arg2, boolean[] arg3, Set<String> arg4) {
        return "performanceTestThree(" + Arrays.toString(arg1) + ", " + arg2 + ", " + Arrays.toString(arg3) + ", " + arg4 + ")";
    }

    @Cacheable(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestFour(Object... args) {
        return "performanceTestFour(" + Arrays.toString(args) + ")";
    }

    @TriggersRemove(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestRemoveFour(Object... args) {
        return "performanceTestRemoveFour(" + Arrays.toString(args) + ")";
    }

    @Cacheable(cacheName="cacheTwo", selfPopulating=true, keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestFive(Object... args) {
        return "performanceTestFive(" + Arrays.toString(args) + ")";
    }
    
    @TriggersRemove(cacheName="cacheTwo", removeAll=true)
    public String performanceTestRemoveFive(Object... args) {
        return "performanceTestRemoveFive(" + Arrays.toString(args) + ")";
    }

    @Cacheable(cacheName="cacheOne", keyGeneratorName="switchingCacheKeyGenerator")
    public String performanceTestSix(int[] arg1, @PartialCacheKey String arg2, boolean[] arg3, @PartialCacheKey Set<String> arg4) {
        return "performanceTestSix(" + Arrays.toString(arg1) + ", " + arg2 + ", " + Arrays.toString(arg3) + ", " + arg4 + ")";
    }
    
    
}
