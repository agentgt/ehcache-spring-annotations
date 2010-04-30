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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link BeanDefinitionParser} implementation to process the
 * "annotation-config" element.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public final class AnnotationConfigEhCacheBeanDefinitionParser implements
		BeanDefinitionParser {

	public static final String XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS = "evict-expired-elements";
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	public BeanDefinition parse(final Element element, final ParserContext parserContext) {
		final Object elementSource = parserContext.extractSource(element);
		NodeList evictExpiredElements = element.getElementsByTagName(XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS);
		if(evictExpiredElements.getLength() > 0) {
			//

			for(int i = 0; i < evictExpiredElements.getLength(); i++) {
				Node evictExpiredElement = evictExpiredElements.item(i);
			
			}
		}
		return null;
	}

	
}
