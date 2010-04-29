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

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<Long> {

    @Override
    protected AbstractDeepCacheKeyGenerator<?, Long> getCacheKeyGenerator() {
        return new HashCodeCacheKeyGenerator();
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(55143650171l), key);
    }
    
    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(14256610617786l), key);
    }
    
    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-78777307803691l), key);        
    }
    
    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-53035962851l), key);        
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-2056817271l), key);        
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-1232013994612058878l), key);        
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-8091702461737953799l), key);        
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(-7735583806644134507l), key);
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, Long key) {
        Assert.assertEquals(Long.valueOf(6372426439346797389l), key);        
    }
}
