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

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<String> {
    
    @Override
    protected AbstractDeepCacheKeyGenerator<?, String> getCacheKeyGenerator() {
        try {
            return new MessageDigestCacheKeyGenerator();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals("RWY43pjj6hHCdz5FWOWps0NYwvk", key);
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, String key) {
        Assert.assertEquals("o4DJ4OzLnJe8pcAtkSmlCBMDgoo", key);        
    }

    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, String key) {
        Assert.assertEquals("U6ls2IuYhBvXnh1ZpM0ttVnFgmY", key);        
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals("tz4aboTBWKBo4BCRaXXgoR82Zls", key);
    }

    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals("_WbnuoQPJpA3QBUAXMmcuXA9RKo", key);
    }

    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, String key) {
        Assert.assertEquals("hmJe_DeMMYOwcAurgREvm1ZLq0w", key);
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals("1OqPbM7S7WC511hbTG3b8Op3KJM", key);        
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals("OcJV9rvFhDjOWAXQ8OduEib18Yg", key);        
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals("ftUXRV1MFSDsWdDqD7ubwy55Wh8", key);        
    }
}
