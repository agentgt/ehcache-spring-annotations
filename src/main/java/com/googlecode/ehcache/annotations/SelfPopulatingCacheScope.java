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

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * Scope at which the {@link SelfPopulatingCache} should be applied.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public enum SelfPopulatingCacheScope {
    /**
     * The {@link SelfPopulatingCache} wrapper is only used for invocations
     * on the annotated method. Other annotations using the same cache name
     * will have their own wrappers.
     */
    METHOD,
    /**
     * The {@link SelfPopulatingCache} wrapper is shared among all methods
     * using the same cache name and marked as self populating.
     */
    SHARED;
}
