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

import junit.framework.Assert;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractCacheKeyGeneratorTest<KG extends AbstractCacheKeyGenerator<T>, T extends Serializable>
    extends BaseCacheKeyGeneratorTest<KG, T> {
    
    /**
     * Test circular reference handling
     */
    @Test
    public final void testCircularReferenceNoCheck() throws SecurityException, NoSuchMethodException {
        final KG generator = this.getCacheKeyGenerator();

        this.setupTestCircularReferenceNoCheck(generator);
        generator.setCheckforCycles(false);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = arg;
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = createMock(MethodInvocation.class);
        expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        expect(invocation.getMethod()).andReturn(testMethod);
        
        replay(invocation);

        try {
            generator.generateKey(invocation);
            Assert.fail("Should have thrown a StackOverflowError");
        }
        catch (StackOverflowError e) {
            //expected
        }
        
        verify(invocation);
    }
    protected void setupTestCircularReferenceNoCheck(KG generator) {}
    
    /**
     * Test circular reference handling
     */
    @Test
    public final void testCircularReference() throws SecurityException, NoSuchMethodException {
        final KG generator = this.getCacheKeyGenerator();
        
        this.setupTestCircularReference(generator);
        generator.setCheckforCycles(true);
        
        final Object[] arg = new Object[2];
        final Object[] childArg = new Object[2];
        arg[0] = childArg;
        arg[1] = "argString";
        childArg[0] = arg;
        childArg[1] = "childArgString";
        
        final MethodInvocation invocation = createMock(MethodInvocation.class);
        expect(invocation.getArguments()).andReturn(new Object[] { arg });
        
        final Method testMethod = MethodInvocationHelper.class.getMethod("testMethod1", Object.class);
        expect(invocation.getMethod()).andReturn(testMethod);
        
        replay(invocation);
        
        final T key = generator.generateKey(invocation);
        
        this.verifyTestCircularReference(invocation, key);
        
        verify(invocation);
    }
    protected void setupTestCircularReference(KG generator) {}
    protected abstract void verifyTestCircularReference(MethodInvocation invocation, T key);
    

    @Override
    protected void setupTestForDocs(KG generator) {
        generator.setIncludeMethod(true);
        generator.setIncludeParameterTypes(true);
        generator.setCheckforCycles(true);
    }

    @Override
    protected void setupTestEnumHashCode(KG generator) {
        generator.setIncludeMethod(false);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(true);
    }
    
    @Override
    protected void setupTestClassHashCode(KG generator) {
        generator.setIncludeMethod(false);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(true);
    }

    @Override
    protected void setupTestComplexHashCode(KG generator) {
        generator.setIncludeMethod(true);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(true);
    }

    @Override
    protected void setupTestPrimitiveArrayHandling(KG generator) {
        generator.setIncludeMethod(true);
        generator.setIncludeParameterTypes(true);
        generator.setCheckforCycles(true);
    }
    
    @Override
    protected void setupTestPrimitiveHandling(KG generator) {
        generator.setIncludeMethod(true);
        generator.setIncludeParameterTypes(true);
        generator.setCheckforCycles(true);
    }
    
    @Override
    protected void setupTestCollectionHandling(KG generator) {
        generator.setIncludeMethod(true);
        generator.setIncludeParameterTypes(true);
        generator.setCheckforCycles(true);
    }

    @Override
    protected void setupTestNoArguments(KG generator) {
        generator.setIncludeMethod(false);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(false);
    }
    
    @Override
    protected void setupTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfFloatParameter(KG generator) {
        generator.setIncludeMethod(false);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(false);
    }

    @Override
    protected void setupTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfDoubleParameter(KG generator) {
        generator.setIncludeMethod(false);
        generator.setIncludeParameterTypes(false);
        generator.setCheckforCycles(false);
    }
}
