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

import com.googlecode.ehcache.annotations.resolver.CacheableCacheResolver;

/**
 * Represents the objects needed to intercept calls to methods annotated
 * with {@link com.googlecode.ehcache.annotations.Cacheable}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheableAttribute extends MethodAttribute {
    /**
     * @return The {@link CacheableCacheResolver} used to determine the Cache and ExceptionCache to use. Cannot return null.
     */
    public CacheableCacheResolver getCacheResolver();
    
    /**
     * @return true If the null return values should be cached.
     */
    public boolean isCacheNull();
    
    /**
     * @return The CacheInterceptor to use when handling intercepted method invocations. Cannot return null.
     */
    public CacheableInterceptor getCacheInterceptor();
}
