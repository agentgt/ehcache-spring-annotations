/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ehcache.annotations.impl;

import java.util.Comparator;

import com.googlecode.ehcache.annotations.Parameter;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
class ParameterComparator implements Comparator<Parameter> {
    public static final ParameterComparator INSTANCE = new ParameterComparator();
    

    public int compare(Parameter o1, Parameter o2) {
        int r = o1.name().compareTo(o2.name());
        if (r != 0) {
            return r;
        }
        
        r = o1.value().compareTo(o2.value());
        if (r != 0) {
            return r;
        }
        
        return o1.ref().compareTo(o2.ref());
    }

}
