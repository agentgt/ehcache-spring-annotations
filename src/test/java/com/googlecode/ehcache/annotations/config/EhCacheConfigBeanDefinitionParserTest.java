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


import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.googlecode.ehcache.annotations.impl.ExpiredElementEvictor;

/**
 * Tests for {@link EhCacheConfigBeanDefinitionParser}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cacheableTestContext.xml")
public class EhCacheConfigBeanDefinitionParserTest {

	/**
	 * Validate an empty evict-expired-elements Element.
	 */
	@Test
	public void testParseEmptyEvictExpiredElement() {
		NodeList childNodes = EasyMock.createMock(NodeList.class);
		EasyMock.expect(childNodes.getLength()).andReturn(0);
		
		Element evictExpired = EasyMock.createMock(Element.class);
		EasyMock.expect(evictExpired.getChildNodes()).andReturn(childNodes);
		EasyMock.replay(childNodes, evictExpired);
		
		EhCacheConfigBeanDefinitionParser parser = new EhCacheConfigBeanDefinitionParser();
		List<CacheNameMatcher> matchers = parser.parseEvictExpiredElement(evictExpired);
		Assert.assertNotNull(matchers);
		Assert.assertEquals(1, matchers.size());
		CacheNameMatcher matcher = matchers.get(0);
		Assert.assertTrue(matcher instanceof PatternCacheNameMatcherImpl);
		PatternCacheNameMatcherImpl casted = (PatternCacheNameMatcherImpl) matcher;
		Assert.assertEquals(".*", casted.getPattern().toString());
		
		EasyMock.verify(childNodes, evictExpired);
	}
	
	/**
	 * Validate an evict-expired-elements Element with a single include element.
	 */
	@Test
	public void testParseEvictExpiredSingleInclude() {
		Node nameAttribute = EasyMock.createMock(Node.class);
		EasyMock.expect(nameAttribute.getTextContent()).andReturn("foo");
		NamedNodeMap includeAttributes = EasyMock.createMock(NamedNodeMap.class);
		EasyMock.expect(includeAttributes.getNamedItem(EhCacheConfigBeanDefinitionParser.XSD_ATTRIBUTE__NAME)).andReturn(nameAttribute);
		
		Node includeNode = EasyMock.createMock(Node.class);
		EasyMock.expect(includeNode.getNodeType()).andReturn(Node.ELEMENT_NODE);
		EasyMock.expect(includeNode.getLocalName()).andReturn("include");
		EasyMock.expect(includeNode.getAttributes()).andReturn(includeAttributes);
		
		NodeList childNodes = EasyMock.createMock(NodeList.class);
		EasyMock.expect(childNodes.getLength()).andReturn(1);
		EasyMock.expect(childNodes.item(0)).andReturn(includeNode);
		
		Element evictExpired = EasyMock.createMock(Element.class);
		EasyMock.expect(evictExpired.getChildNodes()).andReturn(childNodes);
		EasyMock.replay(nameAttribute, includeAttributes, includeNode, childNodes, evictExpired);
		
		EhCacheConfigBeanDefinitionParser parser = new EhCacheConfigBeanDefinitionParser();
		List<CacheNameMatcher> matchers = parser.parseEvictExpiredElement(evictExpired);
		Assert.assertNotNull(matchers);
		Assert.assertEquals(1, matchers.size());
		CacheNameMatcher matcher = matchers.get(0);
		Assert.assertTrue(matcher instanceof ExactCacheNameMatcherImpl);
		ExactCacheNameMatcherImpl casted = (ExactCacheNameMatcherImpl) matcher;
		Assert.assertEquals("foo", casted.getName());
		
		EasyMock.verify(nameAttribute, includeAttributes, includeNode, childNodes, evictExpired);
	}
	
	/**
	 * Validate an evict-expired-elements Element with a single exclude element.
	 * Presence of exclude element will prepend a Include All matcher to the front of the list.
	 */
	@Test
	public void testParseEvictExpiredSingleExclude() {
		Node nameAttribute = EasyMock.createMock(Node.class);
		EasyMock.expect(nameAttribute.getTextContent()).andReturn("foo");
		NamedNodeMap excludeAttributes = EasyMock.createMock(NamedNodeMap.class);
		EasyMock.expect(excludeAttributes.getNamedItem(EhCacheConfigBeanDefinitionParser.XSD_ATTRIBUTE__NAME)).andReturn(nameAttribute);
		
		Node excludeNode = EasyMock.createMock(Node.class);
		EasyMock.expect(excludeNode.getNodeType()).andReturn(Node.ELEMENT_NODE);
		EasyMock.expect(excludeNode.getLocalName()).andReturn("exclude");
		EasyMock.expect(excludeNode.getAttributes()).andReturn(excludeAttributes);
		
		NodeList childNodes = EasyMock.createMock(NodeList.class);
		EasyMock.expect(childNodes.getLength()).andReturn(1);
		EasyMock.expect(childNodes.item(0)).andReturn(excludeNode);
		
		Element evictExpired = EasyMock.createMock(Element.class);
		EasyMock.expect(evictExpired.getChildNodes()).andReturn(childNodes);
		EasyMock.replay(nameAttribute, excludeAttributes, excludeNode, childNodes, evictExpired);
		
		EhCacheConfigBeanDefinitionParser parser = new EhCacheConfigBeanDefinitionParser();
		List<CacheNameMatcher> matchers = parser.parseEvictExpiredElement(evictExpired);
		Assert.assertNotNull(matchers);
		Assert.assertEquals(2, matchers.size());
		Assert.assertEquals(EhCacheConfigBeanDefinitionParser.INCLUDE_ALL_CACHE_NAME_MATCHER, matchers.get(0));
		
		CacheNameMatcher matcher = matchers.get(1);
		Assert.assertTrue(matcher instanceof NotCacheNameMatcherImpl);
		NotCacheNameMatcherImpl casted = (NotCacheNameMatcherImpl) matcher;
		ExactCacheNameMatcherImpl wrapped = (ExactCacheNameMatcherImpl) casted.getWrapped();
		Assert.assertEquals("foo", wrapped.getName());
		
		EasyMock.verify(nameAttribute, excludeAttributes, excludeNode, childNodes, evictExpired);
	}
	
	/**
	 * control, no includes/excludes defined
	 */
	@Test
	public void testLoadControlContext() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/com/googlecode/ehcache/annotations/config/evictExpiredElementsTestControl.xml");
		ExpiredElementEvictor evictor = (ExpiredElementEvictor) applicationContext.getBean(EhCacheConfigBeanDefinitionParser.EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME);
		Assert.assertNotNull(evictor);
		// minutes from configuration gets converted into milliseconds
		Assert.assertEquals(1200000L, evictor.getInterval());
		Assert.assertEquals(1, evictor.getCacheNameMatchers().size());
	}
	
	/**
	 * 3 includes defined
	 */
	@Test
	public void testLoadContextTest1() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/com/googlecode/ehcache/annotations/config/evictExpiredElementsTest1.xml");
		ExpiredElementEvictor evictor = (ExpiredElementEvictor) applicationContext.getBean(EhCacheConfigBeanDefinitionParser.EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME);
		Assert.assertNotNull(evictor);
		// minutes from configuration gets converted into milliseconds
		Assert.assertEquals(1200000L, evictor.getInterval());
		Assert.assertEquals(3, evictor.getCacheNameMatchers().size());
	}
	
	/**
	 * 3 excludes defined (will result in IncludeAll matcher being appended to front)
	 */
	@Test
	public void testLoadContextTest2() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/com/googlecode/ehcache/annotations/config/evictExpiredElementsTest2.xml");
		ExpiredElementEvictor evictor = (ExpiredElementEvictor) applicationContext.getBean(EhCacheConfigBeanDefinitionParser.EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME);
		Assert.assertNotNull(evictor);
		// minutes from configuration gets converted into milliseconds
		Assert.assertEquals(1200000L, evictor.getInterval());
		Assert.assertEquals(4, evictor.getCacheNameMatchers().size());
	}
	
	/**
	 * 2 includes, 1 excludes defined
	 */
	@Test
	public void testLoadContextTest3() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/com/googlecode/ehcache/annotations/config/evictExpiredElementsTest3.xml");
		ExpiredElementEvictor evictor = (ExpiredElementEvictor) applicationContext.getBean(EhCacheConfigBeanDefinitionParser.EHCACHE_CONFIG_EVICTION_TASK_BEAN_NAME);
		Assert.assertNotNull(evictor);
		// minutes from configuration gets converted into milliseconds
		Assert.assertEquals(1200000L, evictor.getInterval());
		Assert.assertEquals(3, evictor.getCacheNameMatchers().size());
	}
}
