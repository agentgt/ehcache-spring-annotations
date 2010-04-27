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

import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGeneratorTest {

    @Test
    public void testCircularReference() {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(false, false);
        generator.setCheckforCycles(true);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = arg;
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        Assert.assertEquals(Long.valueOf(55143650171l), key);
        
        EasyMock.verify(invocation);
    }

    @Test
    public void testForDocs() throws SecurityException, NoSuchMethodException {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator();
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { "49931" });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        Assert.assertEquals(Long.valueOf(-78777307803691l), key);
        
        EasyMock.verify(invocation);
    }
    
    
    @Test
    public void testNegativeOne() {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { -1 });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(30), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMinimumLong() {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Long.MIN_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(-9223372036854775777l), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMaximumLong() {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Long.MAX_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Long key = generator.generateKey(negOneCall);
        Assert.assertEquals(Long.valueOf(-9223372036854775778l), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testEnumHashCode() {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(false, false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { TimeUnit.DAYS });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        Assert.assertEquals(Long.valueOf(-53035962851l), key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testComplexHashCode() throws SecurityException, NoSuchMethodException {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(true, true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                null
                });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        
        Assert.assertEquals(Long.valueOf(-1232013994612058878l), key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testPrimitiveHashCode() throws SecurityException, NoSuchMethodException {
        final HashCodeCacheKeyGenerator generator = new HashCodeCacheKeyGenerator(true, true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod3", int.class, long.class, boolean.class, Integer.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                1, 
                42, 
                false,
                1337
                });
        
        EasyMock.replay(invocation);
        
        final Long key = generator.generateKey(invocation);
        
        Assert.assertEquals(Long.valueOf(5594489449397831184l), key);
        
        EasyMock.verify(invocation);
    }
}
