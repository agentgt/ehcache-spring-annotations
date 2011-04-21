/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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
package com.googlecode.ehcache.annotations.performance;

import static junit.framework.Assert.assertEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.LiveCacheStatistics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.ehcache.annotations.key.AbstractDeepCacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.CachingReflectionHelper;
import com.googlecode.ehcache.annotations.key.ReflectionHelperAware;
import com.googlecode.ehcache.annotations.key.RequiresReflectionKey;
import com.googlecode.ehcache.annotations.util.ThreadGroupRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/performanceTestContext.xml")
public class CacheKeyGeneratorPerformanceTest {
    private static final Random TASK_PICKER = new Random(0);
    
    @Autowired
    private PerformanceTestDao performanceTestDao;
    @Autowired
    private SwitchingCacheKeyGenerator switchingCacheKeyGenerator;
    @Autowired
    private CacheManager cacheManager;
    
    //Class scoped variables used to control the test threads
    private final AtomicLong totalKeyCount = new AtomicLong();
    private CyclicBarrier testThreadStateLatch;
    private volatile boolean runTest = false;
    
    private final Object testCompleteMutex = new Object();
    private boolean testComplete = false;
    
    @Test
    public void testCacheKeyGeneratorPerformance() throws Exception {
        final ThreadGroupRunner threadGroupRunner = new ThreadGroupRunner("CacheKeyGeneratorPerformanceTest-", true);

        final int threads = 20;
        this.testThreadStateLatch = new CyclicBarrier(threads + 1);
        threadGroupRunner.addTask(threads, new KeyGenerationRunnable());
        threadGroupRunner.start();
        
        final CachingReflectionHelper reflectionHelper = new CachingReflectionHelper();
        final String[] cacheNames = sort(this.cacheManager.getCacheNames());

        final StringBuilder header = new StringBuilder();
        header.append("Key Count|Duration|");
        for (final String cacheName : cacheNames) {
            header.append(cacheName).append("|Size|Hits|Misses|");
        }
        header.append("generator|include method|include parameter types|use reflection");
        System.out.println(header);
        
        for (int totalLoopCount = 1; totalLoopCount <= 16; totalLoopCount++) {
            int duration = 10;
            if (totalLoopCount == 1) {
                duration = 3;
            }
            else {
                System.err.println("Sleeping 5s Before: " + duration);
                TimeUnit.SECONDS.sleep(5);
            }

            this.switchingCacheKeyGenerator.reset();
            CacheKeyGenerator<Serializable> cacheKeyGenerator;
            while ((cacheKeyGenerator = this.switchingCacheKeyGenerator.nextCacheKeyGenerator()) != null) {
                if (cacheKeyGenerator instanceof ReflectionHelperAware) {
                    ((ReflectionHelperAware)cacheKeyGenerator).setReflectionHelper(reflectionHelper);
                }
                
                for (int configIndex = 0; configIndex < 4; configIndex++) {
                    final String generatorConfig;
                    if (cacheKeyGenerator instanceof AbstractDeepCacheKeyGenerator) {
                        @SuppressWarnings("unchecked")
                        final AbstractDeepCacheKeyGenerator<?, Serializable> deepCacheKeyGenerator = (AbstractDeepCacheKeyGenerator<?, Serializable>)cacheKeyGenerator;
    
                        switch (configIndex) {
                            case 0:
                                deepCacheKeyGenerator.setCheckforCycles(true);
                                deepCacheKeyGenerator.setIncludeMethod(false);
                                deepCacheKeyGenerator.setIncludeParameterTypes(false);
                                deepCacheKeyGenerator.setUseReflection(false);
                                generatorConfig = "|false|false|false";
                            break;
                            
                            case 1:
                                deepCacheKeyGenerator.setCheckforCycles(true);
                                deepCacheKeyGenerator.setIncludeMethod(true);
                                deepCacheKeyGenerator.setIncludeParameterTypes(false);
                                deepCacheKeyGenerator.setUseReflection(false);
                                generatorConfig = "|true|false|false";
                            break;
                            
                            case 2:
                                deepCacheKeyGenerator.setCheckforCycles(true);
                                deepCacheKeyGenerator.setIncludeMethod(true);
                                deepCacheKeyGenerator.setIncludeParameterTypes(true);
                                deepCacheKeyGenerator.setUseReflection(false);
                                generatorConfig = "|true|true|false";
                            break;
                            
                            case 3:
                                deepCacheKeyGenerator.setCheckforCycles(true);
                                deepCacheKeyGenerator.setIncludeMethod(true);
                                deepCacheKeyGenerator.setIncludeParameterTypes(true);
                                deepCacheKeyGenerator.setUseReflection(true);
                                generatorConfig = "|true|true|true";
                            break;
                            
                            default:
                                throw new IllegalStateException();
                        }
                    }
                    else {
                        generatorConfig = "|basicKeyGenerator";
                        configIndex = 4;
                    }
                    
                    TASK_PICKER.setSeed(0);
                    reflectionHelper.clearCache();
                    
                    for (final String cacheName : this.cacheManager.getCacheNames()) {
                        final Ehcache ehcache = this.cacheManager.getEhcache(cacheName);
                        final LiveCacheStatistics statistics = ehcache.getLiveCacheStatistics();
                        statistics.clearStatistics();
                        ehcache.removeAll();
                        assertEquals(0, ehcache.getSize());
                        assertEquals(0, statistics.getCacheHitCount());
                        assertEquals(0, statistics.getCacheMissCount());
                    }
                    
                    //Setup state and start threads
                    this.totalKeyCount.set(0);
                    this.runTest = true;
                    this.testThreadStateLatch.await();
                    
                    //Sleep main thread for duration of test
                    TimeUnit.SECONDS.sleep(duration);
                    
                    //Switch the test running flag and wait for threads to update the key count
                    this.runTest = false;
                    this.testThreadStateLatch.await();
                    
                    
                    final StringBuilder cacheStats = new StringBuilder();
                    cacheStats
                        .append(this.totalKeyCount.get()).append("|")
                        .append(duration).append("|");
                    
                    for (final String cacheName : cacheNames) {
                        final Ehcache ehcache = this.cacheManager.getEhcache(cacheName);
                        final LiveCacheStatistics statistics = ehcache.getLiveCacheStatistics();

                        cacheStats
                            .append(cacheName).append("|")
                            .append(ehcache.getSize()).append("|")
                            .append(statistics.getCacheHitCount()).append("|")
                            .append(statistics.getCacheMissCount()).append("|");
                    }
                    
                    cacheStats.append(cacheKeyGenerator.getClass().getName()).append(generatorConfig);
                    
                    System.out.println(cacheStats);
                }
            }
        }
        
        synchronized (this.testCompleteMutex) {
            this.testThreadStateLatch.await();
            this.testComplete = true;
        }
        
        threadGroupRunner.join();
    }
    
    private <T> T[] sort(T[] a) {
        Arrays.sort(a);
        return a;
    }
    
    private class KeyGenerationRunnable implements Runnable {
        private final StringBuilder twoArg1 = new StringBuilder("foobar");
        
        private final int[] threeArg1 = new int[] {1, 2, 3, 4};
        private final String threeArg2 = "foo";
        private final boolean[] threeArg3 = new boolean[] {false, true};
        private final LinkedHashSet<String> threeArg4 = new LinkedHashSet<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        
        private final Object[] argArrayOne = new Object[] { 
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
            };
        
        private final Object[] argArrayTwo = new Object[] { 
                new LinkedHashSet<Object>(Arrays.asList("foo", "bar", "bop")),
                new HashMap<Object, Object>() {
                    private static final long serialVersionUID = 1L;
                    {
                        this.put("A", 123);
                        this.put("B", new String[] {"hello", "world"});
                    }
                }
            };
    
        private final Object[] argArrayThree = new Object[] { 
                new LinkedHashSet<Object>(Arrays.asList("foo", "bar", "bop")),
                new RequiresReflectionKey(new HashMap<Object, Object>() {
                    private static final long serialVersionUID = 1L;
                    {
                        this.put("A", 123);
                        this.put("B", new String[] {"hello", "world"});
                    }
                })
            };
        

        private final Object[] arrayArgFour = new Object[] { 
                (byte)1,
                (short)2,
                (int)3,
                (long)4l,
                (char)'a',
                (float)6.8f,
                (double)7.9d,
                (boolean)true
            };

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
                    final int task = TASK_PICKER.nextInt(20);
                    switch (task) {
                        case 0:
                            performanceTestDao.performanceTestOne();
                            break;
                        case 1:
                            performanceTestDao.performanceTestTwo(twoArg1);
                            break;
                        case 2:
                            performanceTestDao.performanceTestThree(threeArg1, threeArg2, threeArg3, threeArg4);
                            break;
                        case 3:
                            performanceTestDao.performanceTestFour(argArrayOne);
                            break;
                        case 4:
                            performanceTestDao.performanceTestFour(argArrayTwo);
                            break;
                        case 5:
                            performanceTestDao.performanceTestFour(argArrayThree);
                            break;
                        case 6:
                            performanceTestDao.performanceTestFour(arrayArgFour);
                            break;
                        case 7:
                            performanceTestDao.performanceTestRemoveFour(argArrayOne);
                            break;
                        case 8:
                            performanceTestDao.performanceTestRemoveFour(argArrayTwo);
                            break;
                        case 9:
                            performanceTestDao.performanceTestRemoveFour(argArrayThree);
                            break;
                        case 10:
                            performanceTestDao.performanceTestRemoveFour(arrayArgFour);
                            break;
                        case 11:
                            performanceTestDao.performanceTestFive(argArrayOne);
                            break;
                        case 12:
                            performanceTestDao.performanceTestFive(argArrayTwo);
                            break;
                        case 13:
                            performanceTestDao.performanceTestFive(argArrayThree);
                            break;
                        case 14:
                            performanceTestDao.performanceTestFive(arrayArgFour);
                            break;
                        case 15:
                            performanceTestDao.performanceTestRemoveFive(argArrayOne);
                            break;
                        case 16:
                            performanceTestDao.performanceTestRemoveFive(argArrayTwo);
                            break;
                        case 17:
                            performanceTestDao.performanceTestRemoveFive(argArrayThree);
                            break;
                        case 18:
                            performanceTestDao.performanceTestRemoveFive(arrayArgFour);
                            break;
                        case 19:
                            performanceTestDao.performanceTestSix(threeArg1, threeArg2, threeArg3, threeArg4);
                            break;
                        default:
                            throw new IllegalStateException("No test configured for task " + task);
                    }
                    keyCount++;
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
