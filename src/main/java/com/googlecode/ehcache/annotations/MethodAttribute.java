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

import java.io.Serializable;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

import net.sf.ehcache.Ehcache;

/**
 * Common information about advised methods.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface MethodAttribute {
    /**
     * @return The type of advice this attribute represents. Will never be null or {@link AdviceType#NONE}
     */
    public AdviceType getAdviceType();
    
    /**
     * @return The cache to use, will never be null.
     */
    public Ehcache getCache();
    
    /**
     * @return The key generator to use, will never be null.
     */
    public CacheKeyGenerator<? extends Serializable> getCacheKeyGenerator();
}
