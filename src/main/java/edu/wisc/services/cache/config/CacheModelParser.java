/* 
 * Created on Jan 21, 2006
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
 * Copyright @2006 the original author or authors.
 */
package edu.wisc.services.cache.config;

import org.w3c.dom.Element;

import edu.wisc.services.cache.CachingModel;
import edu.wisc.services.cache.FlushingModel;

/**
 * <p>
 * Creates cache models by parsing an XML element.
 * </p>
 * 
 * @author Alex Ruiz
 */
public interface CacheModelParser {

  /**
   * Creates a new <code>{@link CachingModel}</code> by parsing the given XML
   * element.
   * 
   * @param element
   *          the XML element to parse
   * @return the created caching model
   */
  CachingModel parseCachingModel(Element element);

  /**
   * Creates a new <code>{@link FlushingModel}</code> by parsing the given XML
   * element.
   * 
   * @param element
   *          the XML element to parse
   * @return the created flushing model
   */
  FlushingModel parseFlushingModel(Element element);

}
