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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Utility for dealing with the 'fun' of supporting a class that only exists in Spring 3.0. Uses reflection to
 * determine if TaskScheduler is available and wraps it to the project-specific task scheduler api. Also supports
 * Timer use.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public final class TaskSchedulerAdapterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulerAdapterFactory.class);
    
    private static Class<?> TASK_SCHEDULER_CLASS = null;
    private static Class<?> SPRING3_TASK_SCHEDULER_ADAPTOR_CLASS = null;
    private static Constructor<?> SPRING3_TASK_SCHEDULER_ADAPTOR_CONSTRUCTOR = null;
    private static boolean SPRING3 = false;
    
    static {
        try {
            TASK_SCHEDULER_CLASS = Class.forName("org.springframework.scheduling.TaskScheduler");
            SPRING3_TASK_SCHEDULER_ADAPTOR_CLASS = Class.forName("com.googlecode.ehcache.annotations.support.Spring3TaskSchedulerAdapter");
            SPRING3_TASK_SCHEDULER_ADAPTOR_CONSTRUCTOR = SPRING3_TASK_SCHEDULER_ADAPTOR_CLASS.getConstructor(TASK_SCHEDULER_CLASS);
            
            SPRING3 = TASK_SCHEDULER_CLASS != null && 
                SPRING3_TASK_SCHEDULER_ADAPTOR_CLASS != null && 
                SPRING3_TASK_SCHEDULER_ADAPTOR_CONSTRUCTOR != null;
            
            LOGGER.debug("Found Spring 3.0 TaskScheduler, will use Spring3TaskSchedulerAdapter if provided with a TaskScheduler");
        }
        catch (Exception e) {
            //Ignore, assume we're not running in Spring 3.0
            LOGGER.debug("Could not find Spring 3.0 TaskScheduler, will use TimerTaskSchedulerAdapter if provided with a Timer");
        }
    }
    
    public static TaskSchedulerAdapter createTaskSchedulerAdapter(Object scheduler) {
        Assert.notNull(scheduler);
        
        //See if the scheduler implements our adapter interface directly, if so just return it
        if (scheduler instanceof TaskSchedulerAdapter) {
            return (TaskSchedulerAdapter)scheduler;
        }
        
        //Check if a Spring 3 TaskScheduler was provided and wrap it
        if (SPRING3 && TASK_SCHEDULER_CLASS.isAssignableFrom(scheduler.getClass())) {
            try {
                return (TaskSchedulerAdapter)SPRING3_TASK_SCHEDULER_ADAPTOR_CONSTRUCTOR.newInstance(scheduler);
            }
            catch (InstantiationException e) {
                throw new IllegalStateException("Failed to create Spring3TaskSchedulerAdapter instance by reflection to wrap " + scheduler.getClass(), e);
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to create Spring3TaskSchedulerAdapter instance by reflection to wrap " + scheduler.getClass(), e);
            }
            catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to create Spring3TaskSchedulerAdapter instance by reflection to wrap " + scheduler.getClass(), e);
            }
        }
        
        //Check if a Timer was provided
        if (scheduler instanceof Timer) {
            return new TimerTaskSchedulerAdapter((Timer)scheduler);
        }
        
        throw new IllegalArgumentException("Provided scheduler object of type " + scheduler.getClass() + " is not supported. Please provide a TaskScheduler, Timer or TaskSchedulerAdapter");
    }
    
    private TaskSchedulerAdapterFactory() {}
}
