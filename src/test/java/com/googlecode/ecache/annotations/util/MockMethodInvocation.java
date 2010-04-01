/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MockMethodInvocation implements MethodInvocation {
    private Method method;
    private Object[] arguments;
    

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInvocation#getMethod()
     */
    public Method getMethod() {
        return this.method;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Invocation#getArguments()
     */
    public Object[] getArguments() {
        return this.arguments;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
     */
    public AccessibleObject getStaticPart() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#getThis()
     */
    public Object getThis() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#proceed()
     */
    public Object proceed() throws Throwable {
        throw new UnsupportedOperationException();
    }
}
