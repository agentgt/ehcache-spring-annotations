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

package com.googlecode.ehcache.annotations.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility for running several threads in a test
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ThreadGroupRunner {
    private final List<Thread> threads = new LinkedList<Thread>();
    
    private final String namePrefix;
    private final boolean daemon;
    
    public ThreadGroupRunner(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    /**
     * Add a Runnable that will be executed in its own thread.
     */
    public synchronized void addTask(Runnable r) {
        final Thread t = new Thread(r , this.namePrefix + threads.size());
        t.setDaemon(this.daemon);
        threads.add(t);
    }
    

    public synchronized void addTask(int threadCount, Runnable r) {
        for (int index = 0; index < threadCount; index++) {
            this.addTask(r);
        }
    }
    
    /**
     * Start all threads (start is in order of added runnable)
     */
    public synchronized void start() {
        for (final Thread t : this.threads) {
            t.start();
        }
    }
    
    /**
     * Join on all threads (join is in order of added runnable)
     */
    public synchronized void join() throws InterruptedException {
        for (final Thread t : this.threads) {
            t.join();
        }
    }
}
