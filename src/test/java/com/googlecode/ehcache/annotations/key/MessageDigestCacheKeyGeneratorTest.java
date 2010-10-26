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

import java.security.NoSuchAlgorithmException;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<MessageDigestCacheKeyGenerator, String> {
    
    @Override
    protected MessageDigestCacheKeyGenerator getCacheKeyGenerator() {
        try {
            return new MessageDigestCacheKeyGenerator();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, String key) {
        assertEquals("RWY43pjj6hHCdz5FWOWps0NYwvk", key);
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, String key) {
        assertEquals("EkuuBNhNIgt8kz4WENI5aiqMaWI", key);        
    }

    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, String key) {
        assertEquals("1BIF0TspJAkNyJO8zw15dwJcXjw", key);        
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, String key) {
        assertEquals("tz4aboTBWKBo4BCRaXXgoR82Zls", key);
    }

    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, String key) {
        assertEquals("Ln6CP-lyuxbCbbm-k1JuSVdK6ao", key);
    }

    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, String key) {
        assertEquals("hmJe_DeMMYOwcAurgREvm1ZLq0w", key);
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, String key) {
        assertEquals("1OqPbM7S7WC511hbTG3b8Op3KJM", key);        
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, String key) {
        assertEquals("OcJV9rvFhDjOWAXQ8OduEib18Yg", key);        
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, String key) {
        assertEquals("ftUXRV1MFSDsWdDqD7ubwy55Wh8", key);        
    }

    @Override
    protected void verifyTestNoArguments(MethodInvocation invocation, String key) {
        assertEquals("2jmj7l5rSw0yVb_vlWAYkK_YBwk", key);        
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfDoubleParameter(
            MethodInvocation invocation, String firstKey, String secondKey) {

        assertEquals("J4a3xvqx6KbKaxKSOSjJegY-VW0", firstKey);
        assertEquals("nBsP2XOqv9xSkmV_JYspj-m1U40", secondKey);
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfFloatParameter(
            MethodInvocation invocation, String firstKey, String secondKey) {
        
        assertEquals("FK4cRs_LyZvZl9WFMkAeqaIFMTw", firstKey);
        assertEquals("2PDcbbvmD0GmFaQeTftgR-L_wZ4", secondKey);        
    }
    
    
}
