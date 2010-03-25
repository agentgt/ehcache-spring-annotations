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

/**
 * 
 */
package edu.wisc.services.cache;

import net.sf.ehcache.Ehcache;
import edu.wisc.services.cache.annotations.TriggersRemove;


/**
 * Represents the objects needed to intercept calls to methods annotated
 * with {@link TriggersRemove}
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public interface TriggersRemoveAttribute extends MethodAttribute {
    /**
     * @return true if {@link Ehcache#removeAll()} should be called.
     */
    public boolean isRemoveAll();
    
}
