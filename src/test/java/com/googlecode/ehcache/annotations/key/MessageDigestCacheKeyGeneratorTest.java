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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

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
    public void testCircularReference() throws NoSuchAlgorithmException, SecurityException, NoSuchMethodException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator("SHA-512", true, true);
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = arg;
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "tFOlrpBVee4gmdnwd7L-9LoIJcJf8c3HqssUURGQxFEZAb31WSswkWPhDcZMP9byXFnBqCLd4t02csJwXAlKeQ";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }

    @Test
    public void testForDocs() throws SecurityException, NoSuchMethodException, NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator();
        generator.setCheckforCycles(true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { "49931" });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "C45-E3ptVV3dSkoFYwakb44EjhU";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testNegativeOne() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { -1 });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("2b5lJKX1BH21hmgTrPMneJKnowo", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMinimumInt() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MIN_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("4XmDZ9Gx7VRCXpn3kKmGwf6TlBQ", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testMaximumInt() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false, false);
        
        final MethodInvocation negOneCall = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(negOneCall.getArguments()).andReturn(new Object[] { Integer.MAX_VALUE });
        
        EasyMock.replay(negOneCall);
        
        final String key = generator.generateKey(negOneCall);
        Assert.assertEquals("bCghfuzbdaxTeNICQwKWlvXmM6E", key.toString());
        
        EasyMock.verify(negOneCall);
    }
    
    @Test
    public void testEnumHashCode() throws NoSuchAlgorithmException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator(false, false);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { TimeUnit.DAYS });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "ot8QS38FbTmuTyyjINgue90M58o";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
    
    @Test
    public void testComplexDigest() throws NoSuchAlgorithmException, SecurityException, NoSuchMethodException {
        final MessageDigestCacheKeyGenerator generator = new MessageDigestCacheKeyGenerator("SHA-512", true, true);
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MethodInvocation invocation = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod);
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                null,
                Arrays.asList(new boolean[] {false, true},
                        new HashSet<Object>(Arrays.asList("1", "2", "3")))
                });
        
        EasyMock.replay(invocation);
        
        final String key = generator.generateKey(invocation);
        final String expectedKey = "8bnDbflY3hQKyvbB6VAP8iPyIfb3Ia9tmgDXJ7tLP-q8fA13-mpDkRyh2jmsBny00GschmkRP8Eg5NlvN8FniQ";
        
        Assert.assertEquals(expectedKey, key);
        
        EasyMock.verify(invocation);
    }
}
