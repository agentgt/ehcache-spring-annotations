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
package com.googlecode.ehcache.annotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.ehcache.annotations.util.ThreadGroupRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SelfPopulatingCacheTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final AtomicInteger invocationCount = new AtomicInteger(0);
    
    @Test
    public void testSelfPopulatingCache() throws Exception {
        final int threads = 10;
        
        final CacheManager cacheManager = new CacheManager();
        
        cacheManager.addCache("testCache");
        final Ehcache ehcache = cacheManager.getEhcache("testCache");
        
        final CountDownLatch threadRunningLatch = new CountDownLatch(threads + 1);
        final CountDownLatch proceedLatch = new CountDownLatch(1);
        invocationCount.set(0);
        
        final SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(ehcache, new CacheEntryFactory() {
            public Object createEntry(Object key) throws Exception {
                logger.trace("Enter createEntry({})", key);
                threadRunningLatch.countDown();
                try {
                    logger.trace("Waiting in createEntry({})", key);
                    proceedLatch.await();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                invocationCount.incrementAndGet();
                logger.trace("Returning from createEntry({})", key);
                return "Created: " + key;
            }
        });
        
        final ThreadGroupRunner threadGroup = new ThreadGroupRunner("SelfPopulatingTest-", true);
        
        threadGroup.addTask(threads, new Runnable() { 
            public void run() {
                threadRunningLatch.countDown();
                logger.trace("Calling get(test1)");
                selfPopulatingCache.get("test1");
            }
        });
        
        // start the threads
        logger.trace("Starting threads");
        threadGroup.start();
        
        // wait for both threads to get going
        logger.trace("Waiting for threads to start");
        threadRunningLatch.await();
        
        // Let both threads complete
        logger.trace("Signal threads to proceed");
        proceedLatch.countDown();
        
        logger.trace("Waiting for threads to complete");
        threadGroup.join();
        
        // verify only 1 call
        Assert.assertEquals(1, invocationCount.get());
    }
}
