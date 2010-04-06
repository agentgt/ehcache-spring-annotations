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

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.ehcache.annotations.key.StringCacheKeyGenerator;

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
        final String expectedKey = "[[[[...], childArgString], argString]]";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }

    @Test
    public void testForDocs() throws SecurityException, NoSuchMethodException {
        final StringCacheKeyGenerator generator = new StringCacheKeyGenerator();
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { "49931" });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        Assert.assertEquals("[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod1, class java.lang.Object, [class java.lang.Object], [49931]]", key);
        
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
        final String expectedKey = "[class com.googlecode.ehcache.annotations.key.MethodInvocationHelper, testMethod2, class java.lang.Object, [class [I, class java.lang.String, class [Z, class java.lang.Object], [[1, 2, 3, 4], foo, [false, true], null]]";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
