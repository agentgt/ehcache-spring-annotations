/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ArgumentListCacheKeyGeneratorTest {
    @Test
    public void testGenerateArgumentWithoutMethodKey() {
        final ArgumentListCacheKeyGenerator generator = new ArgumentListCacheKeyGenerator(false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                null
                });
        
        EasyMock.replay(invocation);
        
        final ReadOnlyList<Serializable> key = generator.generateKey(invocation);
        final List<Object> expectedKey = Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                "foo",
                Arrays.asList(false, true),
                null);
        
        Assert.assertEquals(expectedKey.hashCode(), key.hashCode());
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testGenerateArgumentWithMethodKey() throws SecurityException, NoSuchMethodException {
        final ArgumentListCacheKeyGenerator generator = new ArgumentListCacheKeyGenerator();
        
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
        
        final ReadOnlyList<Serializable> key = generator.generateKey(invocation);
        
        final List<Object> expectedKey = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod2",
                Object.class,
                Arrays.asList(int[].class, String.class, boolean[].class, Object.class),
                Arrays.asList(1, 2, 3, 4),
                "foo",
                Arrays.asList(false, true),
                null);
        
        Assert.assertEquals(expectedKey.hashCode(), key.hashCode());
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
