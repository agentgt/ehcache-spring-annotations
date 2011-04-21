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
package com.googlecode.ehcache.annotations.key;

import java.util.Arrays;

public class MethodInvocationHelper {
    public void testMethod0() {
    }
    
    public Object testMethod1(Object arg1) {
        return arg1;
    }
    
    public Object testMethod2(int[] arg1, String arg2, boolean[] arg3, Object arg4) {
        return Arrays.asList(arg1, arg2, arg3, arg4);
    }
    
    @SuppressWarnings("unchecked")
    public Object testMethod3(int arg1, long arg2, boolean arg3, Integer arg4) {
        return Arrays.asList(arg1, arg2, arg3, arg4);
    }
}