/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ehcache.annotations.key;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.ehcache.annotations.key.ReflectionHashCodeCacheKeyGenerator;

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
    
    private class ReflectionKey {
        private final Object hiddenKey;

        public ReflectionKey(Object hiddenKey) {
            this.hiddenKey = hiddenKey;
        }
    }
}
