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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
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

import com.googlecode.ehcache.annotations.util.MockMethodInvocation;
import com.googlecode.ehcache.annotations.util.ThreadGroupRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CacheKeyGeneratorPerformanceTest {
    private final List<MethodInvocation> invocations = new LinkedList<MethodInvocation>();
    
    @Before
    @SuppressWarnings("unused")
    public void setup() throws SecurityException, NoSuchMethodException {
        final Method testMethod0 = MethodInvocationHelper.class.getMethod("testMethod0");
        final Method testMethod1 = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        final Method testMethod2 = MethodInvocationHelper.class.getMethod("testMethod2", int[].class, String.class, boolean[].class, Object.class);
        final Method testMethod3 = MethodInvocationHelper.class.getMethod("testMethod3", int.class, long.class, boolean.class, Integer.class);
        
        
        /* ********** Invocation 0 ********** */ 
        final MockMethodInvocation invocation0 = new MockMethodInvocation();
        invocation0.setMethod(testMethod0);
        invocation0.setArguments(new Object[] { });
        
        this.invocations.add(invocation0); 
        
        
        /* ********** Invocation 1 ********** */
        final MockMethodInvocation invocation1 = new MockMethodInvocation();
        invocation1.setMethod(testMethod1);
        invocation1.setArguments(new Object[] { new StringBuilder("foobar") });
        
        this.invocations.add(invocation1); 
        
        
        /* ********** Invocation 2 ********** */
        final MockMethodInvocation invocation2 = new MockMethodInvocation();
        invocation2.setMethod(testMethod2);
        invocation2.setArguments(new Object[] { 
                new int[] {1, 2, 3, 4}, 
                "foo", 
                new boolean[] {false, true},
                new LinkedHashSet<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"))
                });
        
        this.invocations.add(invocation2); 
        
        
        /* ********** Invocation 3 ********** */
        final MockMethodInvocation invocation3 = new MockMethodInvocation();
        invocation3.setMethod(testMethod1);
        invocation3.setArguments(new Object[] { 
                    new Object[] { 
                        new byte[] {},
                        new short[] {},
                        new int[] {},
                        new long[] {},
                        new char[] {},
                        new float[] {},
                        new double[] {},
                        new boolean[] {},

                        new byte[] {1},
                        new short[] {2},
                        new int[] {3},
                        new long[] {4l},
                        new char[] {'a'},
                        new float[] {6.8f},
                        new double[] {7.9d},
                        new boolean[] {true},
                        
                        new byte[] {1, 2, 3},
                        new short[] {4, 5, 6},
                        new int[] {7, 8, 9},
                        new long[] {10l, 11l, 12l},
                        new char[] {'a', 'b', 'c'},
                        new float[] {16.1f, 17.2f, 18.3f},
                        new double[] {19.4, 20.5, 21.6},
                        new boolean[] {true, false, false}
                    }
                });
        this.invocations.add(invocation3); 
        
        
        /* ********** Invocation 4 ********** */
        final MockMethodInvocation invocation4 = new MockMethodInvocation();
        invocation4.setMethod(testMethod1);
        invocation4.setArguments(new Object[] { 
                    new Object[] { 
                        (byte)1,
                        (short)2,
                        (int)3,
                        (long)4l,
                        (char)'a',
                        (float)6.8f,
                        (double)7.9d,
                        (boolean)true
                    }
                });
        this.invocations.add(invocation4); 
        
        
        /* ********** Invocation 5 ********** */
        final Map<Object, Object> testMap = new HashMap<Object, Object>();
        testMap.put("A", 123);
        testMap.put("B", new String[] {"hello", "world"});
        
        final MockMethodInvocation invocation5 = new MockMethodInvocation();
        invocation5.setMethod(testMethod1);
        invocation5.setArguments(new Object[] { 
                    new Object[] { 
                        new LinkedHashSet<Object>(Arrays.asList("foo", "bar", "bop")),
                        testMap
                    }
                });
        this.invocations.add(invocation5);  
        
        
        /* ********** Invocation 6 ********** */
        final MockMethodInvocation invocation6 = new MockMethodInvocation();
        invocation6.setMethod(testMethod1);
        invocation6.setArguments(new Object[] { 
                    new Object[] { 
                        new LinkedHashSet<Object>(Arrays.asList("foo", "bar", "bop")),
                        new RequiresReflectionKey(testMap)
                    }
                });
        this.invocations.add(invocation6); 
    }
    
    @Test
    public void testCacheKeyGeneratorPerformance() throws NoSuchAlgorithmException, InterruptedException, BrokenBarrierException {
        final Map<String, AbstractDeepCacheKeyGenerator<?, ? extends Serializable>> generators = new LinkedHashMap<String, AbstractDeepCacheKeyGenerator<?, ? extends Serializable>>();
        
        generators.put("ListCacheKeyGenerator", new ListCacheKeyGenerator());
        generators.put("StringCacheKeyGenerator", new StringCacheKeyGenerator());
        generators.put("MessageDigestCacheKeyGenerator_\"MD5\"", new MessageDigestCacheKeyGenerator("MD5"));
        generators.put("HashCodeCacheKeyGenerator", new HashCodeCacheKeyGenerator());
        
        final ThreadGroupRunner threadGroupRunner = new ThreadGroupRunner("CacheKeyGeneratorPerformanceTest-", true);

        final int threads = 20;
        this.testThreadStateLatch = new CyclicBarrier(threads + 1);
        threadGroupRunner.addTask(threads, new KeyGenerationRunnable());
        threadGroupRunner.start();
        
        
        for (int totalLoopCount = 2; totalLoopCount <= 8; totalLoopCount*=2) {
            final long duration = 1000 * totalLoopCount;
            System.out.println("Sleeping 5s Before: " + duration);
            Thread.sleep(5*1000);
            
            for (final Map.Entry<String, AbstractDeepCacheKeyGenerator<?, ? extends Serializable>> generatorEntry : generators.entrySet()) {
                
                for (int configIndex = 0; configIndex < 3; configIndex++) {
                    //Setup state and start threads
                    this.totalKeyCount.set(0);
                    this.generator = generatorEntry.getValue();
                    this.runTest = true;
                    this.testThreadStateLatch.await();
                    
                    final String generatorConfig;
                    switch (configIndex) {
                        case 0:
                            this.generator.setCheckforCycles(false);
                            this.generator.setIncludeMethod(false);
                            this.generator.setIncludeParameterTypes(false);
                            this.generator.setUseReflection(false);
                            generatorConfig = ",setIncludeMethod(false),setIncludeParameterTypes(false),setUseReflection(false)";
                        break;
                        
                        case 1:
                            this.generator.setCheckforCycles(false);
                            this.generator.setIncludeMethod(true);
                            this.generator.setIncludeParameterTypes(true);
                            this.generator.setUseReflection(false);
                            generatorConfig = ",setIncludeMethod(true),setIncludeParameterTypes(true),setUseReflection(false)";
                        break;
                        
                        case 2:
                            AbstractDeepCacheKeyGenerator.IMPLEMENTS_CACHE.clear();
                            this.generator.setCheckforCycles(false);
                            this.generator.setIncludeMethod(true);
                            this.generator.setIncludeParameterTypes(true);
                            this.generator.setUseReflection(true);
                            generatorConfig = ",setIncludeMethod(true),setIncludeParameterTypes(true),setUseReflection(true)";
                        break;
                        
                        default:
                            throw new IllegalStateException();
                    }
                    
                    //Sleep main thread for duration of test
                    Thread.sleep(duration);
                    
                    //Switch the test running flag and wait for threads to update the key count
                    this.runTest = false;
                    this.testThreadStateLatch.await();
                    
                    final long keyCount = this.totalKeyCount.get();
                    System.out.println(keyCount + "|" + duration + "|" + keyCount/((double)duration) + "|" + generatorEntry.getKey() + generatorConfig);
                }
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
    private AbstractDeepCacheKeyGenerator<?, ? extends Serializable> generator;
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
                    Serializable lastKey = null;
                    for (final MethodInvocation invocation : invocations) {
                        final Serializable key = generator.generateKey(invocation);
                        //include cost of generated keys hashCode API
                        for (int index = 0; index < 10; index++) {
                            key.hashCode();
                        }
                        key.equals(lastKey);
                        lastKey = key;
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
