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

import java.util.Set;


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface PerformanceTestDao {
    
    public String performanceTestOne();
    
    public String performanceTestTwo(StringBuilder arg1);
    
    public String performanceTestThree(int[] arg1, String arg2, boolean[] arg3, Set<String> arg4);
    
    public String performanceTestFour(Object... args);
    
    public String performanceTestRemoveFour(Object... args);
    
    public String performanceTestFive(Object... args);
    
    public String performanceTestRemoveFive(Object... args);
    
    public String performanceTestSix(int[] arg1, String arg2, boolean[] arg3, Set<String> arg4);
}
