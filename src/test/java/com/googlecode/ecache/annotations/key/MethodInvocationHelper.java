/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.util.Arrays;

class MethodInvocationHelper {
    public void testMethod0() {
    }
    
    public Object testMethod1(Object arg1) {
        return arg1;
    }
    
    public Object testMethod2(int[] arg1, String arg2, boolean[] arg3, Object arg4) {
        return Arrays.asList(arg1, arg2, arg3, arg4);
    }
}