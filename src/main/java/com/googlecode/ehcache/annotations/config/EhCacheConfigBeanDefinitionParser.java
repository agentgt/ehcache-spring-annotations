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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.googlecode.ehcache.annotations.impl.ExpiredElementEvictor;

/**
 * {@link BeanDefinitionParser} implementation to process the
 * "annotation-config" element.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public final class EhCacheConfigBeanDefinitionParser implements
BeanDefinitionParser {

	public static final String XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS = "evict-expired-elements";
	public static final String XSD_ATTRIBUTE__INTERVAL = "interval";
	public static final String XSD_ELEMENT__INCLUDE = "include";
	public static final String XSD_ELEMENT__EXCLUDE = "exclude";
	public static final String XSD_ATTRIBUTE__NAME = "name";
	public static final String XSD_ATTRIBUTE__PATTERN = "pattern";

	public static final String EHCACHE_CONFIG_EVICTION_TIMER_BEAN_NAME = EhCacheConfigBeanDefinitionParser.class.getPackage().getName() + ".internalEhCacheEvictionTimer";


	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	public BeanDefinition parse(final Element element, final ParserContext parserContext) {
		final Object elementSource = parserContext.extractSource(element);

		final NodeList evictExpiredElements = element.getElementsByTagName(XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS);
		if (evictExpiredElements.getLength() > 1) {
			throw new BeanCreationException("Only one '" + XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS + "' is allowed");
		}

		if (evictExpiredElements.getLength() == 1) {
			final Element evictExpiredElement = (Element)evictExpiredElements.item(0);

			final int interval = Integer.parseInt(evictExpiredElement.getAttribute(XSD_ATTRIBUTE__INTERVAL));

			List<CacheNameMatcher> cacheNameMatchers = parseEvictExpiredElement(evictExpiredElement);

			// make RuntimeBeanReference for cacheNameMatchers
			// make RuntimeBeanReference for ExpiredElementEvictor instance, wire cacheNameMatchers reference and 
			// make RuntimeBeanReference for Timer instance
		}

		return null;
	}

	/**
	 * 
	 * @param evictExpiredElement
	 * @return
	 */
	protected List<CacheNameMatcher> parseEvictExpiredElement(final Element evictExpiredElement) {
		List<CacheNameMatcher> cacheNameMatchers = new ArrayList<CacheNameMatcher>();
		final NodeList childNodes = evictExpiredElement.getChildNodes();
		final int childNodesLength = childNodes.getLength();
		if(0 == childNodesLength) {
			CacheNameMatcher includeAll = new PatternCacheNameMatcherImpl(".*");
			cacheNameMatchers = Collections.singletonList(includeAll);
		} else {
			for (int index = 0; index < childNodes.getLength(); index++) {
				final Node childNode = childNodes.item(index);
				final String childName = childNode.getLocalName();
				NamedNodeMap childAttributes = childNode.getAttributes();
				Node nameAttr = childAttributes.getNamedItem(XSD_ATTRIBUTE__NAME);
				CacheNameMatcher matcher = null;
				if(null != nameAttr) {
					String matcherValue = nameAttr.getTextContent();
					matcher = new ExactCacheNameMatcherImpl(matcherValue);
				} else {
					Node patternAttr = childAttributes.getNamedItem(XSD_ATTRIBUTE__PATTERN);
					if(null != patternAttr) {
						String matcherValue = patternAttr.getTextContent();
						matcher = new PatternCacheNameMatcherImpl(matcherValue);
					}
				}

				if(null != matcher) {
					if(XSD_ELEMENT__INCLUDE.equals(childName)) {
						cacheNameMatchers.add(matcher);
					} else if(XSD_ELEMENT__EXCLUDE.equals(childName)) {
						cacheNameMatchers.add(new NotCacheNameMatcherImpl(matcher));
					}
				}
			}
		}
		return Collections.unmodifiableList(cacheNameMatchers);
	}
}
