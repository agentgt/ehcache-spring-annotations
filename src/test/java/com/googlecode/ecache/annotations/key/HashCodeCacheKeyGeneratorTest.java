/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class HashCodeCacheKeyGeneratorTest {
    @Test
    public void testNegativeOne() {
        final SimpleHashCodeCacheKeyGenerator generator = new SimpleHashCodeCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { -1 });
        
        EasyMock.replay(negOneCall);
        
        final Integer key = generator.generateKey(negOneCall);
        Assert.assertEquals(Integer.valueOf(30), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMinimumInt() {
        final SimpleHashCodeCacheKeyGenerator generator = new SimpleHashCodeCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MIN_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Integer key = generator.generateKey(negOneCall);
        Assert.assertEquals(Integer.valueOf(-2147483617), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMaximumInt() {
        final SimpleHashCodeCacheKeyGenerator generator = new SimpleHashCodeCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MAX_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final Integer key = generator.generateKey(negOneCall);
        Assert.assertEquals(Integer.valueOf(-2147483618), key);
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testComplexHashCode() throws SecurityException, NoSuchMethodException {
        final SimpleHashCodeCacheKeyGenerator generator = new SimpleHashCodeCacheKeyGenerator(false);
        
//        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
//        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                null
                });
        
        EasyMock.replay(invocation);
        
        final Integer key = generator.generateKey(invocation);
        
        Assert.assertEquals(Integer.valueOf(-1504712407), key);
        
        EasyMock.verify(invocation);
    }
}
