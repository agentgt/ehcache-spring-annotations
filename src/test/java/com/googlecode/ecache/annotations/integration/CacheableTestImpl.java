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

package com.googlecode.ecache.annotations.integration;

import com.googlecode.ecache.annotations.Cacheable;


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheableTestImpl implements CacheableTestInterface {
    private int interfaceAnnotatedExceptionCachedCount = 0;
    private int interfaceAnnotatedExceptionCachedThrowsCount = 0;
    private int interfaceAnnotatedCachedCount = 0;
    private int interfaceAnnotatedNoArgCachedCount = 0;
    private int interfaceDefinedCount = 0;
    
    public String interfaceAnnotatedExceptionCached(boolean throwsException) {
        if (throwsException) {
            this.interfaceAnnotatedExceptionCachedThrowsCount++;
            throw new RuntimeException("throwsException was true");
        }
        
        this.interfaceAnnotatedExceptionCachedCount++;
        return "interfaceAnnotatedExceptionCached(" + throwsException + ")";
    }

    public int interfaceAnnotatedExceptionCachedCount() {
        return this.interfaceAnnotatedExceptionCachedCount;
    }

    public int interfaceAnnotatedExceptionCachedThrowsCount() {
        return this.interfaceAnnotatedExceptionCachedThrowsCount;
    }

    public String interfaceAnnotatedCached(String argument) {
        this.interfaceAnnotatedCachedCount++;
        return "interfaceAnnotatedCached(" + argument + ")";
    }

    public int interfaceAnnotatedCachedCount() {
        return this.interfaceAnnotatedCachedCount;
    }

    public String interfaceAnnotatedNoArgCached() {
        this.interfaceAnnotatedNoArgCachedCount++;
        return "interfaceAnnotatedNoArgCached()";
    }

    public int interfaceAnnotatedNoArgCachedCount() {
        return this.interfaceAnnotatedNoArgCachedCount;
    }

    @Cacheable(cacheName="interfaceDefined")
    public String interfaceDefined(String argument) {
        this.interfaceDefinedCount++;
        return "interfaceDefined(" + argument + ")";
    }

    public int interfaceDefinedCount() {
        return this.interfaceDefinedCount;
    }
}
