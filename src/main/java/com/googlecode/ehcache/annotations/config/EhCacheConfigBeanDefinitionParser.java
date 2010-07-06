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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.googlecode.ehcache.annotations.impl.CacheNameMatcher;
import com.googlecode.ehcache.annotations.impl.ExpiredElementEvictor;

/**
 * {@link BeanDefinitionParser} implementation to process the
 * "config" element.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public final class EhCacheConfigBeanDefinitionParser implements
BeanDefinitionParser {

    public static final String XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS = "evict-expired-elements";
    public static final String XSD_ATTRIBUTE__INTERVAL = "interval";
    public static final String XSD_ELEMENT__INCLUDE = "include";
    public static final String XSD_ELEMENT__EXCLUDE = "exclude";
    public static final String XSD_ATTRIBUTE__NAME = "name";
    public static final String XSD_ATTRIBUTE__PATTERN = "pattern";

    public static final String EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME = EhCacheConfigBeanDefinitionParser.class.getPackage().getName() + ".internalEhCacheEvictionTask";

    public static final CacheNameMatcher INCLUDE_ALL_CACHE_NAME_MATCHER = new PatternCacheNameMatcherImpl(".*");
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final Object elementSource = parserContext.extractSource(element);

        final NodeList evictExpiredElements = element.getElementsByTagNameNS(element.getNamespaceURI(),XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS);
        if (evictExpiredElements.getLength() > 1) {
            throw new BeanCreationException("Only one '" + XSD_ELEMENT__EVICT_EXPIRED_ELEMENTS + "' is allowed");
        }

        final int evictExpiredElementsLength = evictExpiredElements.getLength();
        if (evictExpiredElementsLength == 1) {
            final Element evictExpiredElement = (Element)evictExpiredElements.item(0);

            final String interval = evictExpiredElement.getAttribute(XSD_ATTRIBUTE__INTERVAL);

            List<CacheNameMatcher> cacheNameMatchers = parseEvictExpiredElement(evictExpiredElement);

            // get RuntimeBeanReference for cacheManager
            final RuntimeBeanReference cacheManagerReference = new RuntimeBeanReference(element.getAttribute(AnnotationDrivenEhCacheBeanDefinitionParser.XSD_ATTR__CACHE_MANAGER));

            // make RootBeanDefinition, RuntimeBeanReference for ExpiredElementEvictor instance, wire cacheNameMatchers reference and 
            final RootBeanDefinition expiredElementEvictor = new RootBeanDefinition(ExpiredElementEvictor.class);
            expiredElementEvictor.setSource(elementSource);
            expiredElementEvictor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            final MutablePropertyValues propertyValues = expiredElementEvictor.getPropertyValues();
            propertyValues.addPropertyValue("cacheManager", cacheManagerReference);
            propertyValues.addPropertyValue("cacheNameMatchers", cacheNameMatchers);
            propertyValues.addPropertyValue("interval", interval);

            // register expiredElementEvictor
            final BeanDefinitionRegistry registry = parserContext.getRegistry();
            registry.registerBeanDefinition(EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME, expiredElementEvictor);
        }

        return null;
    }

    /**
     * @param evictExpiredElement
     * @return The list of {@link CacheNameMatcher}s to use for finding caches to evict
     */
    protected List<CacheNameMatcher> parseEvictExpiredElement(final Element evictExpiredElement) {
        List<CacheNameMatcher> cacheNameMatchers = new ArrayList<CacheNameMatcher>();
        final NodeList childNodes = evictExpiredElement.getChildNodes();
        final int childNodesLength = childNodes.getLength();
        boolean configContainsExcludes = false;
        boolean configContainsIncludes = false;
        if(0 != childNodesLength) {
            for (int index = 0; index < childNodesLength; index++) {
                final Node childNode = childNodes.item(index);
                if(Node.ELEMENT_NODE == childNode.getNodeType()) {
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
                            configContainsIncludes = true;
                        } else if(XSD_ELEMENT__EXCLUDE.equals(childName)) {
                            cacheNameMatchers.add(new NotCacheNameMatcherImpl(matcher));
                            configContainsExcludes = true;
                        }
                    }
                }
            }
        } 
        
        if(0 == cacheNameMatchers.size()) {
            // no include/exclude elements found
            cacheNameMatchers = Collections.singletonList(INCLUDE_ALL_CACHE_NAME_MATCHER);
        } else if(!configContainsIncludes && configContainsExcludes) {
            //config contains excludes only
            // create a new list with a Include all matcher at the front
            List<CacheNameMatcher> newList = new ArrayList<CacheNameMatcher>();
            newList.add(INCLUDE_ALL_CACHE_NAME_MATCHER);
            newList.addAll(cacheNameMatchers);
            cacheNameMatchers = newList;
        }
        
        return Collections.unmodifiableList(cacheNameMatchers);
    }
}
