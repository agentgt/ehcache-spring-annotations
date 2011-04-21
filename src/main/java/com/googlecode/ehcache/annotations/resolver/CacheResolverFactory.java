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
package com.googlecode.ehcache.annotations.resolver;

import java.lang.reflect.Method;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * Factory for creating cache resolver instances during annotation loading (when the spring context is loading).
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheResolverFactory {
    /**
     * Get a resolver for the specified annotation and annotated method
     */
    public CacheableCacheResolver getCacheResolver(Cacheable cacheable, Method method);
    
    /**
     * Get a resolver for the specified annotation and annotated method
     */
    public TriggersRemoveCacheResolver getCacheResolver(TriggersRemove triggersRemove, Method method);
}
