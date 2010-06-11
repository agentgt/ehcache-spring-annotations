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

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ListCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.MessageDigestCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.ReflectionHashCodeCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.StringCacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision: 255 $
 */
@SuppressWarnings("deprecation")
public interface CacheKeyGeneratorTestInterface {
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName="generatedHash")
    public String singleCachedCall(String argument);
    public int singleCachedCallCount();
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName=ListCacheKeyGenerator.DEFAULT_BEAN_NAME)
    public String callListCacheKeyGenerator(String argument);
    public int callListCacheKeyGeneratorCount();
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName=HashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME)
    public String callHashCodeCacheKeyGenerator(String argument);
    public int callHashCodeCacheKeyGeneratorCount();
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName=MessageDigestCacheKeyGenerator.DEFAULT_BEAN_NAME)
    public String callMessageDigestCacheKeyGenerator(String argument);
    public int callMessageDigestCacheKeyGeneratorCount();
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName=ReflectionHashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME)
    public String callReflectionHashCodeCacheKeyGenerator(String argument);
    public int callReflectionHashCodeCacheKeyGeneratorCount();
    
    @Cacheable(cacheName="singleCachedCall", keyGeneratorName=StringCacheKeyGenerator.DEFAULT_BEAN_NAME)
    public String callStringCacheKeyGenerator(String argument);
    public int callStringCacheKeyGeneratorCount();
    
}