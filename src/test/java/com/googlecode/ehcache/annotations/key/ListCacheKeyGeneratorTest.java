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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@SuppressWarnings("unchecked")
public class ListCacheKeyGeneratorTest extends AbstractDeepCacheKeyGeneratorTest<AbstractDeepCacheKeyGenerator<?, ReadOnlyList<?>>, ReadOnlyList<?>> {

    @Override
    protected AbstractDeepCacheKeyGenerator<?, ReadOnlyList<?>> getCacheKeyGenerator() {
        return new ListCacheKeyGenerator();
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(Integer.class);
        
        assertEquals(expected, key);
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestCircularReference(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList(
                    Arrays.asList(
                        Arrays.asList(null, "childArgString"),
                        "argString")
                    )
                );
        
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestCircularReferenceWithReflection(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList(
                    Arrays.asList(
                        Arrays.asList(
                                Arrays.asList(RequiresReflectionKey.class, null), 
                                "childArgString"),
                        "argString")
                    )
                );
         
        assertEquals(expected, key); 
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<Object> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod2",
                Object.class,
                Arrays.asList(
                    Arrays.asList(1, 2, 3, 4),
                    "foo",
                    Arrays.asList(false, true),
                    Arrays.asList(null, new Date(0))
                ));
        
        assertEquals(expected, key);
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(TimeUnit.SECONDS, TestEnum.TEST1);
        
        assertEquals(expected, key);
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList("49931"));
        
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList(
                    Arrays.asList(
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),
                            Arrays.asList(),

                            Arrays.asList((byte)1),
                            Arrays.asList((short)2),
                            Arrays.asList(3),
                            Arrays.asList(4l),
                            Arrays.asList('a'),
                            Arrays.asList(6.8f),
                            Arrays.asList(7.9d),
                            Arrays.asList(true),
                        
                            Arrays.asList((byte)1, (byte)2, (byte)3),
                            Arrays.asList((short)4, (short)5, (short)6),
                            Arrays.asList(7, 8, 9),
                            Arrays.asList(10l, 11l, 12l),
                            Arrays.asList('a', 'b', 'c'),
                            Arrays.asList(16.1f, 17.2f, 18.3f),
                            Arrays.asList(19.4d, 20.5d, 21.6d),
                            Arrays.asList(true, false, false)
                    )
                ));
        
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());        
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList(
                    Arrays.asList(
                        Arrays.asList(
                                "foo", 
                                "bar", 
                                "bop"
                        ),
                        Arrays.asList(
                                Arrays.asList("A", 123),
                                Arrays.asList("B", Arrays.asList("hello", "world"))
                        )
                    )
                ));
        
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList(
                MethodInvocationHelper.class,
                "testMethod1",
                Object.class,
                Arrays.asList(Object.class),
                Arrays.asList(
                    Arrays.asList(
                            (byte)1,
                            (short)2,
                            3,
                            4l,
                            'a',
                            6.8f,
                            7.9d,
                            true
                    )
                ));
        
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());            
    }

    @Override
    protected void verifyTestNoArguments(MethodInvocation invocation, ReadOnlyList<?> key) {
        final List<?> expected = Arrays.asList();
        assertEquals(expected, key);        
        assertEquals(expected.hashCode(), key.hashCode());
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfDoubleParameter(
            MethodInvocation invocation, ReadOnlyList<?> firstKey, ReadOnlyList<?> secondKey) {
        
        final List<?> firstExpected = Arrays.asList(1.5d);
        assertEquals(firstExpected, firstKey);
        assertEquals(firstExpected.hashCode(), firstKey.hashCode());
        
        final List<?> secondExpected = Arrays.asList(1.7d);
        assertEquals(secondExpected, secondKey);
        assertEquals(secondExpected.hashCode(), secondKey.hashCode());
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfFloatParameter(
            MethodInvocation invocation, ReadOnlyList<?> firstKey, ReadOnlyList<?> secondKey) {
        
        final List<?> firstExpected = Arrays.asList(1.5f);
        assertEquals(firstExpected, firstKey);
        assertEquals(firstExpected.hashCode(), firstKey.hashCode());
        
        final List<?> secondExpected = Arrays.asList(1.7f);
        assertEquals(secondExpected, secondKey);
        assertEquals(secondExpected.hashCode(), secondKey.hashCode());        
    }
}
