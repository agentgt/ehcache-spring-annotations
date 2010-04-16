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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ListCacheKeyGeneratorTest {

    @Test
    public void testCircularReference() {
        final ListCacheKeyGenerator generator = new ListCacheKeyGenerator(false, false);
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
        
        final ReadOnlyList<Serializable> key = generator.generateKey(invocation);
        final List<?> expectedKey = Arrays.asList(
                Arrays.asList(
                    Arrays.asList(null, "childArgString"),
                    "argString")
                );
        
        Assert.assertEquals(expectedKey, key);
        Assert.assertEquals(expectedKey.hashCode(), key.hashCode());
        
        EasyMock.verify(invocation);
    }

    @Test
    public void testForDocs() throws SecurityException, NoSuchMethodException {
        final ListCacheKeyGenerator generator = new ListCacheKeyGenerator();
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { "49931" });
        
        EasyMock.replay(invocation);
        
        final ReadOnlyList<Serializable> key = generator.generateKey(invocation);
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList("49931"));
        Assert.assertEquals(expected, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testGenerateArgumentWithoutMethodKey() {
        final ListCacheKeyGenerator generator = new ListCacheKeyGenerator(false, false);
        
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
    public void testEnumHashCode() {
        final ListCacheKeyGenerator generator = new ListCacheKeyGenerator(false, false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { TimeUnit.DAYS });
        
        EasyMock.replay(invocation);
        
        final ReadOnlyList<Serializable> key = generator.generateKey(invocation);
        
        final List<?> expectedKey = Arrays.asList(TimeUnit.DAYS);
        
        Assert.assertEquals(expectedKey.hashCode(), key.hashCode());
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testGenerateArgumentWithMethodKey() throws SecurityException, NoSuchMethodException {
        final ListCacheKeyGenerator generator = new ListCacheKeyGenerator(true, true);
        
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
                Arrays.asList(
                    Arrays.asList(1, 2, 3, 4),
                    "foo",
                    Arrays.asList(false, true),
                    null)
                );
        
        Assert.assertEquals(expectedKey.hashCode(), key.hashCode());
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
