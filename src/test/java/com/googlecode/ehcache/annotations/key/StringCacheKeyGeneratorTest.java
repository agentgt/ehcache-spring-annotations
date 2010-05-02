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
public class StringCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<String> {

    @Override
    protected AbstractDeepCacheKeyGenerator<?, String> getCacheKeyGenerator() {
        return new StringCacheKeyGenerator();
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals("[class java.lang.Integer]", key);
        
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[[[[...], childArgString], argString]]",
                key);
    }

    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[[[[class com.googlecode.ehcache.annotations.key.RequiresReflectionKey, [...]], childArgString], argString]]", 
                key);
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod2, class java.lang.Object, [[1, 2, 3, 4], foo, [false, true], [null, Wed Dec 31 18:00:00 CST 1969]]]",
                key);
    }

    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, String key) {
        Assert.assertEquals("[DAYS]", key);
    }

    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod1, class java.lang.Object, [class java.lang.Object], [49931]]",
                key);
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod1, class java.lang.Object, [class java.lang.Object], [[[], [], [], [], [], [], [], [], [1], [2], [3], [4], [a], [6.8], [7.9], [true], [1, 2, 3], [4, 5, 6], [7, 8, 9], [10, 11, 12], [a, b, c], [16.1, 17.2, 18.3], [19.4, 20.5, 21.6], [true, false, false]]]]",
                key);
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod1, class java.lang.Object, [class java.lang.Object], [[[foo, bar, bop], [[A, 123], [B, [hello, world]]]]]]",
                key);        
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, String key) {
        Assert.assertEquals(
                "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod1, class java.lang.Object, [class java.lang.Object], [[1, 2, 3, 4, a, 6.8, 7.9, true]]]",
                key);        
    }
}
