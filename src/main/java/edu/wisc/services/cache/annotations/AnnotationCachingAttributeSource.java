/* 
 * Created on Apr 29, 2005
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright @2005 the original author or authors.
 */
package edu.wisc.services.cache.annotations;

import static org.springframework.util.ObjectUtils.isEmpty;
import static edu.wisc.services.cache.interceptor.caching.CachingUtils.isCacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import edu.wisc.services.cache.CacheAttribute;
import edu.wisc.services.cache.interceptor.MetadataCacheAttributeSource;
import edu.wisc.services.cache.interceptor.MetadataCacheAttributeSource.MetadataFinder;
import edu.wisc.services.cache.interceptor.caching.Cached;
import edu.wisc.services.cache.interceptor.caching.CachingAttributeSource;

/**
 * Binds caching metadata annotations to methods.
 * 
 * @author Alex Ruiz
 */
public class AnnotationCachingAttributeSource implements CachingAttributeSource {

  private final MetadataFinder finder = new MetadataFinder() {
    public CacheAttribute find(Method m) {
      return find(m.getAnnotations());
    }

    private CacheAttribute attribute(Cacheable a) {
      return new Cached(a.modelId());
    }

    private CacheAttribute find(Annotation[] annotations) {
      if (isEmpty(annotations)) return null;
      for (Annotation a : annotations)
        if (a instanceof Cacheable) return attribute((Cacheable)a);
      return null;
    }
  };

  private final MetadataCacheAttributeSource source;

  public AnnotationCachingAttributeSource() {
    source = new MetadataCacheAttributeSource(finder);
  }

  public Cached attribute(Method m, Class t) {
    if (isCacheable(m)) return (Cached)source.attribute(m, t);
    return null;
  }
}
