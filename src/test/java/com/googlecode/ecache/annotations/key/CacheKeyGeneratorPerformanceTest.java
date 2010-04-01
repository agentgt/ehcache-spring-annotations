/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.ecache.annotations.util.MockMethodInvocation;
import com.googlecode.ecache.annotations.util.ThreadGroupRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheKeyGeneratorPerformanceTest {
    private final List<MethodInvocation> invocations = new LinkedList<MethodInvocation>();
    
    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        final Method testMethod0 = MethodInvocationHelper.class.getMethod("testMethod0");
        
        final MockMethodInvocation invocation0 = new MockMethodInvocation();
        invocation0.setMethod(testMethod0);
        invocation0.setArguments(new Object[] { });
        
        this.invocations.add(invocation0); 
        
        
        final Method testMethod1 = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        
        final MockMethodInvocation invocation1 = new MockMethodInvocation();
        invocation1.setMethod(testMethod1);
        invocation1.setArguments(new Object[] { new StringBuilder("foobar") });
        
        this.invocations.add(invocation1); 
        
        
        
        final Method testMethod2 = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        
        final MockMethodInvocation invocation2 = new MockMethodInvocation();
        invocation2.setMethod(testMethod2);
        invocation2.setArguments(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                new LinkedHashSet<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"))
                });
        
        this.invocations.add(invocation2); 
    }
    
    @Test
    public void testCacheKeyGeneratorPerformance() throws NoSuchAlgorithmException, InterruptedException, BrokenBarrierException {
        final Map<String, CacheKeyGenerator<? extends Serializable>> generators = new LinkedHashMap<String, CacheKeyGenerator<? extends Serializable>>();
        
        generators.put("ArgumentListCacheKeyGenerator(true, true)", new ArgumentListCacheKeyGenerator(true, true));
        generators.put("ArgumentListCacheKeyGenerator(true, false)", new ArgumentListCacheKeyGenerator(true, false));
        generators.put("ArgumentListCacheKeyGenerator(false, false)", new ArgumentListCacheKeyGenerator(false, false));
        generators.put("StringCacheKeyGenerator(true, true)", new StringCacheKeyGenerator(true, true));
        generators.put("StringCacheKeyGenerator(true, false)", new StringCacheKeyGenerator(true, false));
        generators.put("StringCacheKeyGenerator(false, false)", new StringCacheKeyGenerator(false, false));
        generators.put("MessageDigestCacheKeyGenerator(\"MD5\", true, true)", new MessageDigestCacheKeyGenerator("MD5", true, true));
        generators.put("MessageDigestCacheKeyGenerator(\"MD5\", true, false)", new MessageDigestCacheKeyGenerator("MD5", true, false));
        generators.put("MessageDigestCacheKeyGenerator(\"MD5\", false, false)", new MessageDigestCacheKeyGenerator("MD5", false, false));
        generators.put("SimpleHashCodeCacheKeyGenerator(true, true)", new HashCodeCacheKeyGenerator(true, true));
        generators.put("SimpleHashCodeCacheKeyGenerator(true, false)", new HashCodeCacheKeyGenerator(true, false));
        generators.put("SimpleHashCodeCacheKeyGenerator(false, false)", new HashCodeCacheKeyGenerator(false, false));
        generators.put("ReflectionHashCodeCacheKeyGenerator(true, true)", new ReflectionHashCodeCacheKeyGenerator(true, true));
        generators.put("ReflectionHashCodeCacheKeyGenerator(true, false)", new ReflectionHashCodeCacheKeyGenerator(true, false));
        generators.put("ReflectionHashCodeCacheKeyGenerator(false, false)", new ReflectionHashCodeCacheKeyGenerator(false, false));
        
        final ThreadGroupRunner threadGroupRunner = new ThreadGroupRunner("CacheKeyGeneratorPerformanceTest-", true);

        final int threads = 20;
        this.testThreadStateLatch = new CyclicBarrier(threads + 1);
        threadGroupRunner.addTask(threads, new KeyGenerationRunnable());
        threadGroupRunner.start();
        
        
        for (int totalLoopCount = 1; totalLoopCount <= 4; totalLoopCount++) {
            final long duration = 1000 * totalLoopCount;
            System.out.println("Sleeping Before: " + duration);
            Thread.sleep(5*1000);
            
            for (final Map.Entry<String, CacheKeyGenerator<? extends Serializable>> generatorEntry : generators.entrySet()) {
                //Setup state and start threads
                this.totalKeyCount.set(0);
                this.generator = generatorEntry.getValue();
                this.runTest = true;
                this.testThreadStateLatch.await();
                
                //Sleep main thread for duration of test
                Thread.sleep(duration);
                
                //Switch the test running flag and wait for threads to update the key count
                this.runTest = false;
                this.testThreadStateLatch.await();
                
                final long keyCount = this.totalKeyCount.get();
                System.out.println(keyCount + "|" + duration + "|" + keyCount/((double)duration) + "|" + generatorEntry.getKey());
            }
        }
        
        synchronized (this.testCompleteMutex) {
            this.testThreadStateLatch.await();
            this.testComplete = true;
        }
        
        threadGroupRunner.join();
    }
    
    //Class scoped variables used to control the test threads
    private final AtomicLong totalKeyCount = new AtomicLong();
    private CacheKeyGenerator<? extends Serializable> generator;
    private CyclicBarrier testThreadStateLatch;
    private volatile boolean runTest = false;
    
    private final Object testCompleteMutex = new Object();
    private boolean testComplete = false;
    
    private class KeyGenerationRunnable implements Runnable {
        public void run() {
            while (!testComplete) {
                await();
                synchronized (testCompleteMutex) {
                    if (testComplete) {
                        testCompleteMutex.notifyAll();
                        return;
                    }
                }
                
                long keyCount = 0;
                while (runTest) {
                    for (final MethodInvocation invocation : invocations) {
                        final Serializable key = generator.generateKey(invocation);
                        //include cost of generated keys hashCode API
                        for (int index = 0; index < 10; index++) {
                            key.hashCode(); 
                        }
                        keyCount++;
                    }
                }
             
                totalKeyCount.addAndGet(keyCount);
                await();
            }
        }

        private void await() {
            if (testComplete) {
                return;
            }
            try {
                testThreadStateLatch.await();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
