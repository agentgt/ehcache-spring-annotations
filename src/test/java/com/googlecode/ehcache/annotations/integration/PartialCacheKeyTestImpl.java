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

import java.util.Arrays;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;



/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class PartialCacheKeyTestImpl implements PartialCacheKeyTestInterface {
    private int cacheableMethodOneCount = 0;
    private int cacheableMethodThreeCount = 0;
    private int cacheableMethodTwoCount = 0;
    private int triggersRemoveMethodOneCount = 0;
    private int triggersRemoveMethodThreeCount = 0;
    private int triggersRemoveMethodTwoCount = 0;
    

    public void reset() {
        this.cacheableMethodOneCount = 0;
        this.cacheableMethodThreeCount = 0;
        this.cacheableMethodTwoCount = 0;
        this.triggersRemoveMethodOneCount = 0;
        this.triggersRemoveMethodThreeCount = 0;
        this.triggersRemoveMethodTwoCount = 0;        
    }

    @Cacheable(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    public String cacheableMethodOne(
            @PartialCacheKey String arg1, 
            @PartialCacheKey int arg2, 
            @PartialCacheKey long arg3, 
            Object arg4) {
        this.cacheableMethodOneCount++;
        return Arrays.asList(arg1, arg2, arg3, arg4).toString();
    }

    public int cacheableMethodOneCount() {
        return this.cacheableMethodOneCount;
    }

    @Cacheable(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    @SuppressWarnings("unchecked")
    public String cacheableMethodThree(
            @PartialCacheKey String arg1, 
            @PartialCacheKey int arg2, 
            @PartialCacheKey long arg3) {
        this.cacheableMethodThreeCount++;
        return Arrays.asList(arg1, arg2, arg3).toString();
    }

    public int cacheableMethodThreeCount() {
        return this.cacheableMethodThreeCount;
    }

    @Cacheable(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    @SuppressWarnings("unchecked")
    public String cacheableMethodTwo(int arg2, long arg3) {
        this.cacheableMethodTwoCount++;
        return Arrays.asList(arg2, arg3).toString();
    }

    public int cacheableMethodTwoCount() {
        return this.cacheableMethodTwoCount;
    }

    @TriggersRemove(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    public String triggersRemoveMethodOne(
            @PartialCacheKey String arg1, 
            @PartialCacheKey int arg2, 
            @PartialCacheKey long arg3, Object arg4) {
        this.triggersRemoveMethodOneCount++;
        return Arrays.asList(arg1, arg2, arg3, arg4).toString();
    }

    public int triggersRemoveMethodOneCount() {
        return this.triggersRemoveMethodOneCount;
    }

    @TriggersRemove(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    @SuppressWarnings("unchecked")
    public String triggersRemoveMethodThree(
            @PartialCacheKey String arg1, 
            @PartialCacheKey int arg2, 
            @PartialCacheKey long arg3) {
        this.triggersRemoveMethodThreeCount++;
        return Arrays.asList(arg1, arg2, arg3).toString();
    }

    public int triggersRemoveMethodThreeCount() {
        return this.triggersRemoveMethodThreeCount;
    }

    @TriggersRemove(cacheName="myCache", 
            keyGenerator = @KeyGenerator(name = "StringCacheKeyGenerator", 
                    properties=@Property(name="includeMethod", value="false")))
    @SuppressWarnings("unchecked")
    public String triggersRemoveMethodTwo(int arg2, long arg3) {
        this.triggersRemoveMethodTwoCount++;
        return Arrays.asList(arg2, arg3).toString();
    }

    public int triggersRemoveMethodTwoCount() {
        return this.triggersRemoveMethodTwoCount;
    }

}
