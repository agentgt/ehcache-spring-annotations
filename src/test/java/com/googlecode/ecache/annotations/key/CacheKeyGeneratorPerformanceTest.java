/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheKeyGeneratorPerformanceTest {
    private MethodInvocation invocation1;
    
    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MethodInvocation invocation = EasyMock.createNiceMock(MethodInvocation.class);
        EasyMock.expect(invocation.getMethod()).andReturn(testMethod).anyTimes();
        EasyMock.expect(invocation.getArguments()).andReturn(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                null
                }).anyTimes();
        
        EasyMock.replay(invocation);
        
        this.invocation1 = invocation; 
    }
    
    @Test
    public void testCacheKeyGeneratorPerformance() throws NoSuchAlgorithmException {
        final Map<String, CacheKeyGenerator<? extends Serializable>> generators = new LinkedHashMap<String, CacheKeyGenerator<? extends Serializable>>();
        
        generators.put("ArgumentListCacheKeyGenerator(false)", new ArgumentListCacheKeyGenerator(false));
        generators.put("ArgumentListCacheKeyGenerator(true)", new ArgumentListCacheKeyGenerator(true));
        generators.put("StringCacheKeyGenerator(false)", new StringCacheKeyGenerator(false));
        generators.put("StringCacheKeyGenerator(true)", new StringCacheKeyGenerator(true));
        generators.put("MessageDigestCacheKeyGenerator(\"SHA-1\", false)", new MessageDigestCacheKeyGenerator("SHA-1", false));
        generators.put("MessageDigestCacheKeyGenerator(\"SHA-1\", true)", new MessageDigestCacheKeyGenerator("SHA-1", true));
        generators.put("SimpleHashCodeCacheKeyGenerator(false)", new SimpleHashCodeCacheKeyGenerator(false));
        generators.put("SimpleHashCodeCacheKeyGenerator(true)", new SimpleHashCodeCacheKeyGenerator(true));
        generators.put("HashCodeCacheKeyGenerator(true)", new HashCodeCacheKeyGenerator(true));
        generators.put("HashCodeCacheKeyGenerator(false)", new HashCodeCacheKeyGenerator(false));
        generators.put("ArgumentsOnlyCacheKeyGenerator(true)", new ArgumentsOnlyCacheKeyGenerator(true));
        generators.put("ArgumentsOnlyCacheKeyGenerator(false)", new ArgumentsOnlyCacheKeyGenerator(false));
        
        
        
        for (int totalLoopCount = 1; totalLoopCount <= 3; totalLoopCount++) {
            final long duration = 1000 * totalLoopCount;
            System.out.println("---------- START " + duration + "ms TEST RUN ----------");
            
            for (final Map.Entry<String, CacheKeyGenerator<? extends Serializable>> generatorEntry : generators.entrySet()) {
                final String generatorName = generatorEntry.getKey();
                final CacheKeyGenerator<? extends Serializable> generator = generatorEntry.getValue();

                long keyCount = 0;
                final long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < duration) {
                    generator.generateKey(this.invocation1);
                    keyCount++;
                }
                
                System.out.println(keyCount + " keys in " + duration + "ms, " + keyCount/((double)duration) + " keys/ms for " + generatorName);
            }
            
            
            System.out.println("----------- END " + duration + "ms TEST RUN -----------");
        }
    }
}
