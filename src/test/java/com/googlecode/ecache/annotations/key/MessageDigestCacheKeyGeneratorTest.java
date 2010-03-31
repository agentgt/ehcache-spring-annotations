/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGeneratorTest {
    @Test
    public void testNegativeOne() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { -1 });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("2b5lJKX1BH21hmgTrPMneJKnowo", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMinimumInt() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MIN_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("4XmDZ9Gx7VRCXpn3kKmGwf6TlBQ", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMaximumInt() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MAX_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("bCghfuzbdaxTeNICQwKWlvXmM6E", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testComplexDigest() throws NoSuchAlgorithmException, SecurityException, NoSuchMethodException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator("SHA-512", true);
        
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
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "ZI9eEvnS2_1rOZTHOfjcvyOrLTbt5SIMHpifrxFX_GAf1S5RGtzeAUtB_swzzWAHOMdvFGs1QkVBl39f_TFnVg";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
