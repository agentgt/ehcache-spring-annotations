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


/**
 * Raised when no cache can be found with the specified name.
 * 
 * @author Nicholas Blair
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheNotFoundException extends CacheException {
    private static final long serialVersionUID = 6601590278654078802L;
    
    private final String cacheName;

    /**
     * Creates a <code>CacheNotFoundException</code>.
     * 
     * @param cacheName
     *          the name of the cache that could be found
     */
    public CacheNotFoundException(String cacheName) {
        super("Unable to find cache '" + cacheName + "'");
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return this.cacheName;
    }
}
