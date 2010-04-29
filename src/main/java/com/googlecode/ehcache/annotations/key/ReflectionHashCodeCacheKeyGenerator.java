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

package com.googlecode.ehcache.annotations.key;


/**
 * @author Eric Dalquist
 * @version $Revision$
 * @deprecated {@link HashCodeCacheKeyGenerator} includes support for reflection by setting {@link #setUseReflection(boolean)} to true. Use that class directly.
 */
@Deprecated
public class ReflectionHashCodeCacheKeyGenerator extends HashCodeCacheKeyGenerator {
    /**
     * Name of the bean this generator is registered under using the default constructor.
     */
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.ReflectionHashCodeCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public ReflectionHashCodeCacheKeyGenerator() {
        this.setUseReflection(true);
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean) 
     */
    public ReflectionHashCodeCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
        this.setUseReflection(true);
    }
}
