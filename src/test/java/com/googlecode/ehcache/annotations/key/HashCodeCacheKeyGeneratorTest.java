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

import static org.junit.Assert.assertEquals;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<AbstractDeepCacheKeyGenerator<?, Long>, Long> {

    @Override
    protected AbstractDeepCacheKeyGenerator<?, Long> getCacheKeyGenerator() {
        return new HashCodeCacheKeyGenerator();
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-75704985491986817l), key);
    }
    
    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-2346842003094128842l), key);
    }
    
    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-78777307803691l), key);        
    }
    
    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-61970522899999l), key);        
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-2056817271l), key);        
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(7228491118810439268l), key);        
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-8091702461737953799l), key);        
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(-7735481857378548977l), key);
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(6372426439346797389l), key);        
    }
    
    @Override
    protected void verifyTestNoArguments(MethodInvocation invocation, Long key) {
        assertEquals(Long.valueOf(1l), key);        
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfDoubleParameter(
            MethodInvocation invocation, Long firstKey, Long secondKey) {
        assertEquals(Long.valueOf(4609434218613702687l), firstKey);
        assertEquals(Long.valueOf(4610334938539176786l), secondKey);
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfFloatParameter(
            MethodInvocation invocation, Long firstKey, Long secondKey) {
        assertEquals(Long.valueOf(1069547551l), firstKey);
        assertEquals(Long.valueOf(1071225273l), secondKey);
    }
}
