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
package com.googlecode.ehcache.annotations.support;

import org.springframework.scheduling.TaskScheduler;

/**
 * Utility interface used internally to hide the Spring 3.0 {@link TaskScheduler} from
 * direct reference in the core code. This is needed to maintain Spring 2.5 compatibility.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface TaskSchedulerAdapter {
    /**
     * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
     */
    public void scheduleAtFixedRate(Runnable task, long period);
}
