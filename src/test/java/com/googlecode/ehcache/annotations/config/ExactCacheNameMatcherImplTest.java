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

package com.googlecode.ehcache.annotations.config;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.ehcache.annotations.impl.Vote;

/**
 * Tests for {@link ExactCacheNameMatcherImpl}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public class ExactCacheNameMatcherImplTest {

    @Test
    public void testMatches() {
        ExactCacheNameMatcherImpl m = new ExactCacheNameMatcherImpl("foo");
        Assert.assertEquals(Vote.YEA, m.matches("foo"));
        Assert.assertEquals(Vote.ABSTAIN, m.matches("bar"));
    }
}
