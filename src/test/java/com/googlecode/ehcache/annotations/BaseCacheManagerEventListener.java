/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.googlecode.ehcache.annotations;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class BaseCacheManagerEventListener implements CacheManagerEventListener {

    /* (non-Javadoc)
     * @see net.sf.ehcache.event.CacheManagerEventListener#init()
     */
    public void init() throws CacheException {

    }

    /* (non-Javadoc)
     * @see net.sf.ehcache.event.CacheManagerEventListener#getStatus()
     */
    public Status getStatus() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.sf.ehcache.event.CacheManagerEventListener#dispose()
     */
    public void dispose() throws CacheException {

    }

    /* (non-Javadoc)
     * @see net.sf.ehcache.event.CacheManagerEventListener#notifyCacheAdded(java.lang.String)
     */
    public void notifyCacheAdded(String cacheName) {

    }

    /* (non-Javadoc)
     * @see net.sf.ehcache.event.CacheManagerEventListener#notifyCacheRemoved(java.lang.String)
     */
    public void notifyCacheRemoved(String cacheName) {

    }

}
