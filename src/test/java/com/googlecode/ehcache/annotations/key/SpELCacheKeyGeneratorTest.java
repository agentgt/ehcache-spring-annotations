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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spELCacheKeyGeneratorTestContext.xml")
public class SpELCacheKeyGeneratorTest extends BaseCacheKeyGeneratorTest<CacheKeyGenerator<Serializable>, Serializable> {
    private SpELCacheKeyGenerator cacheKeyGenerator;

    @Autowired
    public void setCacheKeyGenerator(SpELCacheKeyGenerator cacheKeyGenerator) {
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Override
    protected CacheKeyGenerator<Serializable> getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    @Override
    protected void verifyTestForDocs(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [49931]]", key);
    }

    @Override
    protected void verifyTestEnumHashCode(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [SECONDS, TEST1]]", key);
    }

    @Override
    protected void verifyClassHashCode(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [class java.lang.Integer]]", key);
    }

    @Override
    protected void verifyTestComplexHashCode(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod2, [[1, 2, 3, 4], foo, [false, true], [null, java.util.GregorianCalendar[time=0,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"GMT\",offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=1970,MONTH=0,WEEK_OF_YEAR=1,WEEK_OF_MONTH=1,DAY_OF_MONTH=1,DAY_OF_YEAR=1,DAY_OF_WEEK=5,DAY_OF_WEEK_IN_MONTH=1,AM_PM=0,HOUR=0,HOUR_OF_DAY=0,MINUTE=0,SECOND=0,MILLISECOND=0,ZONE_OFFSET=0,DST_OFFSET=0]]]]", key);
    }

    @Override
    protected void verifyTestNoArguments(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod0,]]", key);
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfFloatParameter(
            MethodInvocation invocation, Serializable firstKey, Serializable secondKey) {
        assertEquals("[testMethod1, [1.5]]", firstKey);
        assertEquals("[testMethod1, [1.7]]", secondKey);
        assertNotSame(firstKey, secondKey);
    }

    @Override
    protected void verifyTestGeneratesDifferentKeysWithDifferentNonIntegerPartsOfDoubleParameter(
            MethodInvocation invocation, Serializable firstKey, Serializable secondKey) {
        assertEquals("[testMethod1, [1.5]]", firstKey);
        assertEquals("[testMethod1, [1.7]]", secondKey);
        assertNotSame(firstKey, secondKey);
    }

    @Override
    protected void verifyTestPrimitiveArrayHandling(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [[1, 2, 3, 4, a, 6.8, 7.9, true]]]", key);
    }

    @Override
    protected void verifyTestPrimitiveHandling(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [[1, 2, 3, 4, a, 6.8, 7.9, true]]]", key);
    }

    @Override
    protected void verifyTestCollectionHandling(MethodInvocation invocation, Serializable key) {
        assertEquals("[testMethod1, [[[foo, bar, bop], [[A, 123], [B, [hello, world]]]]]]", key);
    }
}
