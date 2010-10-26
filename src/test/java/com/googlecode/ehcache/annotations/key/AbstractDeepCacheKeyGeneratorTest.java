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

package com.googlecode.ehcache.annotations.key;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractDeepCacheKeyGeneratorTest<KG extends AbstractDeepCacheKeyGenerator<?, T>, T extends Serializable>
        extends AbstractCacheKeyGeneratorTest<KG, T> {
    
    /**
     * Test a circular reference hidden by a field with reflection
     */
    @Test
    public final void testCircularReferenceWithReflection() throws SecurityException, NoSuchMethodException {
        final KG generator = this.getCacheKeyGenerator();
        
        this.setupTestCircularReferenceWithReflection(generator);
        generator.setCheckforCycles(true);
        generator.setUseReflection(true);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = new RequiresReflectionKey(arg);
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = createMock(MethodInvocation.class);
        expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        expect(invocation.getMethod()).andReturn(testMethod);
        
        
        replay(invocation);
        
        final T key = generator.generateKey(invocation);
        
        this.verifyTestCircularReferenceWithReflection(invocation, key);
        
        verify(invocation);
    }
    protected void setupTestCircularReferenceWithReflection(KG generator) {}
    protected abstract void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, T key);
}
