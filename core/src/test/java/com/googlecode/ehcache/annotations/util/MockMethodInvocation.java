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
package com.googlecode.ehcache.annotations.util;

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
