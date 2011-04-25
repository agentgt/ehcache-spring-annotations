/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.googlecode.ehcache.annotations.support;

import org.springframework.scheduling.TaskScheduler;

/**
 * Simply wraps a Spring 3 {@link TaskScheduler}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class Spring3TaskSchedulerAdapter implements TaskSchedulerAdapter {
    private final TaskScheduler taskScheduler;
    
    public Spring3TaskSchedulerAdapter(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ehcache.annotations.support.TaskSchedulerAdapter#scheduleAtFixedRate(java.lang.Runnable, long)
     */
    public void scheduleAtFixedRate(Runnable task, long period) {
        this.taskScheduler.scheduleAtFixedRate(task, period);
    }
}
