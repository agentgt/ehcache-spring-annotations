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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ReflectionHashCodeCacheKeyGeneratorTest {
    @Test
    public void testCircularReference() {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(false, false);
        generator.setCheckforCycles(true);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = new ReflectionKey(arg);
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        Assert.assertEquals(Long.valueOf(55143680954l), key);
        
        EasyMock.verify(invocation);
    }

    @Test
    public void testForDocs() throws SecurityException, NoSuchMethodException {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator();
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { new WeatherId("49931") });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        Assert.assertEquals(Long.valueOf(-78777307802668l), key);
        
        EasyMock.verify(invocation);
    }
    
    private static class WeatherId {
        private final String id;
    
        public WeatherId(String id) {
            this.id = id;
        }
    }
    
    
    @Test
    public void testNegativeOne() {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { -1 });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(30), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMinimumInt() {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MIN_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(-2147483617), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMaximumInt() {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MAX_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(2147483678l), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testComplexHashCode() throws SecurityException, NoSuchMethodException {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(true, true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new ReflectionKey(new boolean[] {false, true}),
                null
                });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        
        Assert.assertEquals(Long.valueOf(-43138117839501l), key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testEnumHashCode() {
        final ReflectionHashCodeCacheKeyGenerator generator = new ReflectionHashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { TimeUnit.DAYS });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        
        Assert.assertEquals(Long.valueOf(-53035962820l), key);
        
        
        EasyMock.verify(invocation);
    }
    
    private class ReflectionKey {
        private final Object hiddenKey;

        public ReflectionKey(Object hiddenKey) {
            this.hiddenKey = hiddenKey;
        }
    }
}
