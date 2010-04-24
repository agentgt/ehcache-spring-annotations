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

package com.googlecode.ehcache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This key generator is a good option when you want to be 100% sure that two sets of method invocation arguments
 * are the same.  It does depend on all arguments correctly implementing the hashCode and equals methods.  All of
 * the objects involved in the invocation arguments are put into {@link List}s and arrays are converted to
 * {@link List}s to provide better equals and hashCode behavior. 
 * 
 * <table>
 *  <tr>
 *      <th>Pros</th>
 *      <th>Cons</th>
 *  </tr>
 *  <tr>
 *      <td>
 *          100% assurance that generated keys will never collide.
 *      </td>
 *      <td>
 *          All method arguments must be completely {@link Serializable} if disk storage or any replication
 *          operations will be used. A {@link ClassCastException} may be thrown if any argument does not
 *          implement {@link Serializable}.
 *      </td>
 *  </tr>
 *  <tr>
 *      <td>
 *      </td>
 *      <td>
 *          Each time the key is compared using equals or has its hash generated via hashCode all arguments will
 *          be visited and have their equals and hashCode methods called. This results in a higher expense after
 *          key generation each time the key is inspected.
 *      </td>
 *  </tr>
 * </table>
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ListCacheKeyGenerator extends AbstractCacheKeyGenerator<ReadOnlyList<Serializable>> {
    /**
     * Name of the bean this generator is registered under using the default constructor.
     */
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.ListCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public ListCacheKeyGenerator() {
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean) 
     */
    public ListCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    @Override
    public ReadOnlyList<Serializable> generateKey(Object... data) {
        final ArrayList<Serializable> keyList = new ArrayList<Serializable>(data.length);
        
        for (final Object arg : data) {
            keyList.add(this.arrayCheck((Serializable)arg));
        }
        
        return new ReadOnlyList<Serializable>(keyList);
    }
    
    protected Serializable arrayCheck(Serializable object) {
        if (object == null || !register(object)) {
            //Return null in place of the actual hash code in the case of a circular reference
            return null;
        }
        try {
            final Class<? extends Object> c = object.getClass();
            if (!c.isArray()) {
                return object;
            }
    
            final int length = Array.getLength(object);
            final ArrayList<Object> objArray = new ArrayList<Object>(length);
            for (int index = 0; index < length; index++) {
                final Object arrayValue = Array.get(object, index);
                objArray.add(this.arrayCheck((Serializable)arrayValue));
            }
            
            return (Serializable)Collections.unmodifiableList(objArray);
        }
        finally {
            unregister(object);
        }
    }
}
