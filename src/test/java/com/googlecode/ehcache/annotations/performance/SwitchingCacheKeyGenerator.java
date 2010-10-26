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

package com.googlecode.ehcache.annotations.performance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SwitchingCacheKeyGenerator implements CacheKeyGenerator<Serializable> {
    private List<CacheKeyGenerator<Serializable>> cacheKeyGenerators;
    private Iterator<CacheKeyGenerator<Serializable>> cacheKeyGeneratorIterator;
    private volatile CacheKeyGenerator<Serializable> currentCacheKeyGenerator;
    
    @Autowired
    public void setCacheKeyGenerators(List<CacheKeyGenerator<Serializable>> cacheKeyGenerators) {
        this.cacheKeyGenerators = new ArrayList<CacheKeyGenerator<Serializable>>(cacheKeyGenerators);
        this.reset();
    }
    
    public void reset() {
        this.currentCacheKeyGenerator = null;
        this.cacheKeyGeneratorIterator = this.cacheKeyGenerators.iterator();
    }
    
    public boolean hasNext() {
        return cacheKeyGeneratorIterator.hasNext();
    }

    public CacheKeyGenerator<Serializable> nextCacheKeyGenerator() {
        if (this.cacheKeyGeneratorIterator.hasNext()) {
            this.currentCacheKeyGenerator = this.cacheKeyGeneratorIterator.next();
        }
        else {
            this.currentCacheKeyGenerator = null;
        }

        return this.currentCacheKeyGenerator;
    }
    
    public CacheKeyGenerator<Serializable> getCurrentCacheKeyGenerator() {
        return currentCacheKeyGenerator;
    }

    public Serializable generateKey(MethodInvocation methodInvocation) {
        return currentCacheKeyGenerator.generateKey(methodInvocation);
    }

    public Serializable generateKey(Object... data) {
        return currentCacheKeyGenerator.generateKey(data);
    }
}
