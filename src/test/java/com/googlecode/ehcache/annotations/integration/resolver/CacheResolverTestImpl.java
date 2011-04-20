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

package com.googlecode.ehcache.annotations.integration.resolver;



/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheResolverTestImpl implements CacheResolverTestInterface {
    private int interfaceCachedCount = 0;
    private int interfaceCachedDefaultResolverCount = 0;
    
    public String interfaceCached(String arg) {
        interfaceCachedCount++;
        return "interfaceCached[" + arg + "]";
    }

    public int getInterfaceCachedCount() {
        return interfaceCachedCount;
    }

    public String interfaceCachedDefaultResolver(String arg) {
        interfaceCachedDefaultResolverCount++;
        return "interfaceCachedDefaultResolver[" + arg + "]";
    }

    public int getInterfaceCachedDefaultResolverCount() {
        return interfaceCachedDefaultResolverCount;
    }
 
    
}
