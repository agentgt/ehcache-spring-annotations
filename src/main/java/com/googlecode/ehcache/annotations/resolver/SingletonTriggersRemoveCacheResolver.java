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

package com.googlecode.ehcache.annotations.resolver;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Resolver that returns the {@link Ehcache}s provided to the constructor, no resolution logic is performed.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SingletonTriggersRemoveCacheResolver implements TriggersRemoveCacheResolver {
    private final Iterable<Ehcache> caches;
    
    public SingletonTriggersRemoveCacheResolver(Collection<Ehcache> caches) {
        this.caches = Collections.unmodifiableSet(new LinkedHashSet<Ehcache>(caches));
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.resolver.TriggersRemoveCacheResolver#resolveRemoveCaches(java.io.Serializable, org.aopalliance.intercept.MethodInvocation)
     */
    public Iterable<Ehcache> resolveRemoveCaches(Serializable cacheKey, MethodInvocation invocation) {
        return this.caches;
    }

    public Iterable<Ehcache> resolveRemoveAllCaches(MethodInvocation invocation) {
        return this.caches;
    }
}
