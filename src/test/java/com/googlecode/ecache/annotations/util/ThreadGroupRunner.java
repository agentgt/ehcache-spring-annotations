/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.util;

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
