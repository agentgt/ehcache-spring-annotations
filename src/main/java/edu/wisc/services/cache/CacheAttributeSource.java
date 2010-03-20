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

package edu.wisc.services.cache;

import java.lang.reflect.Method;

/**
 * This interface provides operations to determine if methods/classes
 * have {@link CacheableAttribute} or {@link TriggersRemoveAttribute}
 * information.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface CacheAttributeSource {
    /**
     * Determines if the method has should be cached or involved with cache entry removal. Returns {@link AdviceType#NONE} if the
     * method doesn't participate with either. 
     * 
     * @param method The method to inspect
     * @param targetClass The class the method exists on
     * @return The type of advice on the method, {@link AdviceType#NONE} if the method is not advised. Will never return null.
     */
    public AdviceType getAdviceType(Method method, Class<?> targetClass);
    
    /**
     * Gets the runtime triggers-remove information for a method. If the method is not eligible for
     * triggers-remove null is returned. 
     * 
     * @param method The method to inspect
     * @param targetClass The class the method exists on
     * @return Runtime triggers-remove configuration for the method, null if the method is not involved with triggers-remove.
     */
    public TriggersRemoveAttribute getTriggersRemoveAttribute(Method method, Class<?> targetClass);
    
    /**
     * Gets the runtime caching information for a method. If the method is not eligible for
     * caching null is returned. 
     * 
     * @param method The method to inspect
     * @param targetClass The class the method exists on
     * @return Runtime caching configuration for the method, null if the method is not cachable.
     */
    public CacheableAttribute getCacheableAttribute(Method method, Class<?> targetClass);
}
