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
package com.googlecode.ehcache.annotations.key;

import java.io.Serializable;


/**
 * {@link AbstractCacheKeyGenerator} implementation designed for subclassing.
 * 
 * Subclasses can implement the {@link #getAdditionalDataForKeyGeneration()} method to
 * seed additional data into the call to {@link #generateKey(Object...)}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public abstract class DelegateCacheKeyGenerator extends AbstractCacheKeyGenerator<Serializable> {

    private final AbstractCacheKeyGenerator<? extends Serializable> delegate;
    
    /**
     * @param delegate
     */
    public DelegateCacheKeyGenerator(
            AbstractCacheKeyGenerator<? extends Serializable> delegate) {
        super();
        this.delegate = delegate;
    }

    /**
     * Calls {@link #generateKey(Object...)} on the delegate passing in data argument AND the result
     * from {@link #getAdditionalDataForKeyGeneration()}.
     * 
     * @see #getAdditionalDataForKeyGeneration()
     * @see com.googlecode.ehcache.annotations.key.AbstractCacheKeyGenerator#generateKey(java.lang.Object[])
     */
    @Override
    public final Serializable generateKey(Object... data) {
        this.setCheckforCycles(delegate.isCheckforCycles());
        return this.delegate.generateKey(getAdditionalDataForKeyGeneration(), data);
    }

    /**
     * 
     * @return a {@link Serializable} object to inject into the calls to {@link #generateKey(Object...)}
     */
    public abstract Serializable getAdditionalDataForKeyGeneration();
}
