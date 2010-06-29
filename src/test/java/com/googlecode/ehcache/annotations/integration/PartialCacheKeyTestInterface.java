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

package com.googlecode.ehcache.annotations.integration;


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface PartialCacheKeyTestInterface {
    public String cacheableMethodOne(String arg1, int arg2, long arg3, Object arg4);
    public int cacheableMethodOneCount();
    
    public String cacheableMethodTwo(int arg2, long arg3);
    public int cacheableMethodTwoCount();
    
    public String cacheableMethodThree(String arg1, int arg2, long arg3);
    public int cacheableMethodThreeCount();
    
    public String triggersRemoveMethodOne(String arg1, int arg2, long arg3, Object arg4);
    public int triggersRemoveMethodOneCount();
    
    public String triggersRemoveMethodTwo(int arg2, long arg3);
    public int triggersRemoveMethodTwoCount();
    
    public String triggersRemoveMethodThree(String arg1, int arg2, long arg3);
    public int triggersRemoveMethodThreeCount();
    
    public void reset();
}