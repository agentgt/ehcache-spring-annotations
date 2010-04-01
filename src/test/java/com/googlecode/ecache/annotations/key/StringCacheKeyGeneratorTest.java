/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StringCacheKeyGeneratorTest {

    @Test
    public void testCircularReference() {
        final StringCacheKeyGenerator generator = new StringCacheKeyGenerator(false, false);
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

        final String key = generator.generateKey(invocation);
        final String expectedKey = "[[[\"CIRCULAR_REFERENCE:[Ljava.lang.Object;\", childArgString], argString]]";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    
    @Test
    public void testGenerateArgumentWithoutMethodKey() {
        final StringCacheKeyGenerator generator = new StringCacheKeyGenerator(false, false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                null
                });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "[[1, 2, 3, 4], foo, [false, true], null]";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testGenerateArgumentWithMethodKey() throws SecurityException, NoSuchMethodException {
        final StringCacheKeyGenerator generator = new StringCacheKeyGenerator(true, true);
        
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
        final String expectedKey = "[class com.googlecode.ecache.annotations.key.MethodInvocationHelper, testMethod2, class java.lang.Object, [class [I, class java.lang.String, class [Z, class java.lang.Object], [[1, 2, 3, 4], foo, [false, true], null]]";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
