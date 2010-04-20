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
import java.util.Arrays;
import java.util.List;

/**
 * Uses {@link Arrays#deepToString(Object[])} to generate a cache key. This is a decent compromise on using
 * {@link ListCacheKeyGenerator} if all arguments implement toString well since hashCode and equals operations
 * on the generated key are much faster than on the {@link List} returned by {@link ListCacheKeyGenerator} and
 * the chance of key collision is very small for objects with a well implemented toString.
 * 
 * <table>
 *  <tr>
 *      <th>Pros</th>
 *      <th>Cons</th>
 *  </tr>
 *  <tr>
 *      <td>
 *          Faster both for key generation and key comparison than {@link ListCacheKeyGenerator}.
 *      </td>
 *      <td>
 *          Depends on all arguments implementing toString well enough to represent each argument's
 *          identity.
 *      </td>
 *  </tr>
 *  <tr>
 *      <td>
 *          Arguments do not have to implement {@link Serializable}
 *      </td>
 *      <td>
 *      </td>
 *  </tr>
 * </table>
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StringCacheKeyGenerator extends AbstractCacheKeyGenerator<String> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.StringCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public StringCacheKeyGenerator() {
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean)
     */
    public StringCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }
    
    
    @Override
    public boolean isCheckforCycles() {
        return true;
    }

    @Override
    public void setCheckforCycles(boolean checkforCycles) {
        if (!checkforCycles) {
            throw new UnsupportedOperationException("StringCacheKeyGenerator always checks for cycles");
        }
    }

    @Override
    public String generateKey(Object... data) {
        return Arrays.deepToString(data);
    }
}
