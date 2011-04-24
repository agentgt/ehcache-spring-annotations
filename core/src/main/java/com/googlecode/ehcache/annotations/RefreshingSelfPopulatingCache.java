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

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.event.CacheManagerEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import com.google.common.collect.MapMaker;

/**
 * Extension of SelfPopulatingCache that schedules a periodic call of {@link #refresh()} via the specified
 * {@link TaskScheduler}. Also overrides {@link #refreshElement(Element, Ehcache, boolean)} to allow for
 * asynchronous refresh of elements iff an {@link Executor} was provided to the constructor.
 */
public class RefreshingSelfPopulatingCache extends SelfPopulatingCache {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //Use a weak key concurrent map to track async refreshes without danger of memory leaks
    private final ConcurrentMap<Serializable, Long> refreshQueue = new MapMaker().weakKeys().makeMap();
    private final TaskScheduler scheduler;
    private final TaskExecutor executer;
    private final long refreshInterval;
    
    public RefreshingSelfPopulatingCache(Ehcache cache, CacheEntryFactory cacheEntryFactory,
            TaskScheduler scheduler, TaskExecutor executer,
            long refreshInterval) {
        super(cache, cacheEntryFactory);

        this.scheduler = scheduler;
        this.executer = executer;
        this.refreshInterval = refreshInterval;
        
        this.scheduleRefreshTask();
        
        this.getCacheManager().setCacheManagerEventListener(new CacheManagerEventListener() {
            public void notifyCacheRemoved(String cacheName) {
            }
            public void notifyCacheAdded(String cacheName) {
            }
            public void init() throws CacheException {
            }
            public Status getStatus() {
                return null;
            }
            public void dispose() throws CacheException {
                
            }
        });
    }
    
    public long getRefreshInterval() {
        return refreshInterval;
    }

    protected void scheduleRefreshTask() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    refresh();
                }
                catch (Throwable t) {
                    logger.error("An exception was thrown while refreshing the cache. Review the previous log statements for errors related to individual cache entries.", t);
                }
            }
        }, refreshInterval);
    }

    @Override
    protected Element refreshElement(final Element element, final Ehcache backingCache, final boolean quiet) throws Exception {
        final Serializable key = element.getKey();
        
        //For refresh track that the element is being refreshed so we don't queue up multiple refresh attempts
        final long now = System.currentTimeMillis();
        final Long existingRefreshStart = this.refreshQueue.putIfAbsent(key, now);
        if (existingRefreshStart != null) {
            this.logger.warn("Key {} in cache {} is already being refreshed started {}ms ago, it will be skipped for this refresh iteration.", new Object[] { key, backingCache.getName(), now - existingRefreshStart });
            return null;
        }

        boolean clearRefreshFlag = true;
        try {
            if (element.isExpired()) {
                this.logger.debug("Element for key {} has expired, this key will not be refreshed", key);
                return null;
            }

            //Check if the element is old enough to try refreshing it
            final long age = now - element.getLatestOfCreationAndUpdateTime();
            if (age < refreshInterval) {
                this.logger.debug("Element for key {} is only {}ms old and will not be refreshed. Refresh age is {}", new Object[] { key, age, refreshInterval });
                return null;
            }
            
            final Object value = element.getObjectValue();
            if (!(value instanceof RefreshableCacheEntry)) {
                this.logger.warn("RefreshingSelfPopulatingCache contains an entry which is not a RefreshableCacheEntry for key {} this entry will be ignored during refresh.", key);
                return null;
            }
            
            //If no executor refresh synchronously
            if (this.executer == null) {
                super.refreshElement(element, backingCache, quiet);
            }
            //If executor refresh via thread-pool
            else {
                clearRefreshFlag = false;
                //Submit the refresh task to the executor
                executer.execute(new Runnable() {
                    public void run() {
                        try {
                            RefreshingSelfPopulatingCache.super.refreshElement(element, backingCache, quiet);
                        }
                        catch (Throwable e) {
                            logger.error("An exception was thrown while refreshing the ca", e);
                        }
                        finally {
                            //Clear out the refresh tracker reference
                            refreshQueue.remove(key);
                        }
                    }
                });
            }
        }
        catch (final Exception e) {
            this.logger.warn("An exception was thrown while refreshing the cache for " + element + ". This element may not have been refreshed", e);
        }
        finally {
            //Clear out the refresh tracker reference if sync refresh was done
            if (clearRefreshFlag) {
                refreshQueue.remove(key);
            }
        }
        
        return element;
    }
}