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
package com.googlecode.ehcache.annotations.integration.resolver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/com/googlecode/ehcache/annotations/integration/resolver/cacheResolverTestContext.xml")
public class CacheResolverTest {
    @Autowired
    private CacheResolverTestInterface cacheResolverTestInterface;
    
    @Autowired @Qualifier("testCacheResolverFactory")
    private LoggingCacheResolverFactory cacheResolverFactory;
    
    @Autowired @Qualifier("defaultCacheResolverFactory")
    private LoggingCacheResolverFactory defaultCacheResolverFactory;
    
    
    @Test
    public void testCacheResolver() {
        assertEquals(0, cacheResolverTestInterface.getInterfaceCachedCount());
        assertEquals(0, cacheResolverFactory.getResolvedKeys().size());
        
        final String result1 = cacheResolverTestInterface.interfaceCached("test");
        assertEquals("interfaceCached[test]", result1);
        assertEquals(1, cacheResolverTestInterface.getInterfaceCachedCount());
        assertEquals(1, cacheResolverFactory.getResolvedKeys().size());
        
        final String result2 = cacheResolverTestInterface.interfaceCached("test");
        assertEquals("interfaceCached[test]", result2);
        assertEquals(1, cacheResolverTestInterface.getInterfaceCachedCount());
        assertEquals(1, cacheResolverFactory.getResolvedKeys().size());
        
        
        final String result3 = cacheResolverTestInterface.interfaceCached("foo");
        assertEquals("interfaceCached[foo]", result3);
        assertEquals(2, cacheResolverTestInterface.getInterfaceCachedCount());
        assertEquals(2, cacheResolverFactory.getResolvedKeys().size());
    }
    
    @Test
    public void testDefaultCacheResolver() {
        assertEquals(0, cacheResolverTestInterface.getInterfaceCachedDefaultResolverCount());
        assertEquals(0, defaultCacheResolverFactory.getResolvedKeys().size());
        
        final String result1 = cacheResolverTestInterface.interfaceCachedDefaultResolver("test");
        assertEquals("interfaceCachedDefaultResolver[test]", result1);
        assertEquals(1, cacheResolverTestInterface.getInterfaceCachedDefaultResolverCount());
        assertEquals(1, defaultCacheResolverFactory.getResolvedKeys().size());
        
        final String result2 = cacheResolverTestInterface.interfaceCachedDefaultResolver("test");
        assertEquals("interfaceCachedDefaultResolver[test]", result2);
        assertEquals(1, cacheResolverTestInterface.getInterfaceCachedDefaultResolverCount());
        assertEquals(1, defaultCacheResolverFactory.getResolvedKeys().size());
        
        final String result3 = cacheResolverTestInterface.interfaceCachedDefaultResolver("foo");
        assertEquals("interfaceCachedDefaultResolver[foo]", result3);
        assertEquals(2, cacheResolverTestInterface.getInterfaceCachedDefaultResolverCount());
        assertEquals(2, defaultCacheResolverFactory.getResolvedKeys().size());
    }
}
