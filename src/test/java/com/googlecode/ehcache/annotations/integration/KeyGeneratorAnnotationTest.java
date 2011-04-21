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
package com.googlecode.ehcache.annotations.integration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/keyGeneratorAnnotationTestContext.xml")
public class KeyGeneratorAnnotationTest {
    private KeyGeneratorAnnotationTestInterface keyGeneratorAnnotationTestInterface;

    @Autowired
    public void setKeyGeneratorAnnotationTestInterface(KeyGeneratorAnnotationTestInterface keyGeneratorAnnotationTestInterface) {
        this.keyGeneratorAnnotationTestInterface = keyGeneratorAnnotationTestInterface;
    }

    @Test
    public void testIncludedKeyGenerator() {
        Assert.assertEquals(0, keyGeneratorAnnotationTestInterface.listCacheKeyGeneratorCount());
        
        Assert.assertEquals("listCacheKeyGenerator()", keyGeneratorAnnotationTestInterface.listCacheKeyGenerator());
        Assert.assertEquals(1, keyGeneratorAnnotationTestInterface.listCacheKeyGeneratorCount());
        
        Assert.assertEquals("listCacheKeyGenerator()", keyGeneratorAnnotationTestInterface.listCacheKeyGenerator());
        Assert.assertEquals(1, keyGeneratorAnnotationTestInterface.listCacheKeyGeneratorCount());
    }
    
    @Test
    public void testCustomKeyGenerator() {
        keyGeneratorAnnotationTestInterface.customGeneratorOne();
        keyGeneratorAnnotationTestInterface.customGeneratorTwo();
        keyGeneratorAnnotationTestInterface.customGeneratorThree();
        keyGeneratorAnnotationTestInterface.customGeneratorFour();
        
        final List<MockCacheKeyGenerator> instances = MockCacheKeyGenerator.getInstances();
        
        Assert.assertEquals(3, instances.size());
        
        boolean foundOne = false;
        boolean foundTwoThree = false;
        boolean foundFour = false;
        for (final MockCacheKeyGenerator generator : instances) {
            Assert.assertTrue(generator.isInitialized());
            final TimeUnit enumProperty = generator.getEnumProperty();
            final List<Object> listProperty = generator.getListProperty();
            if (enumProperty == null && listProperty == null) {
                if (foundOne) {
                    Assert.fail("MockCacheKeyGenerator for method customGeneratorOne was already found once");
                }
                foundOne = true;
            }
            else if (enumProperty == TimeUnit.SECONDS && Arrays.asList("a", "1", "c").equals(listProperty)) {
                if (foundTwoThree) {
                    Assert.fail("MockCacheKeyGenerator for methods customGeneratorTwo and customGeneratorThree was already found once");
                }
                foundTwoThree = true;
            }
            else if (enumProperty == TimeUnit.SECONDS && listProperty == null) {
                if (foundFour) {
                    Assert.fail("MockCacheKeyGenerator for method customGeneratorFour was already found once");
                }
                foundFour = true;
            }
        }
        
        Assert.assertTrue("Did not find MockCacheKeyGenerator for method customGeneratorOne", foundOne);
        Assert.assertTrue("Did not find MockCacheKeyGenerator for methods customGeneratorTwo and customGeneratorThree", foundTwoThree);
        Assert.assertTrue("Did not find MockCacheKeyGenerator for method customGeneratorFour", foundFour);
    }
}


