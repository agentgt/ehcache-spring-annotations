/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class AnnotationDrivenEhCacheNamespaceHandler extends NamespaceHandlerSupport {

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
	 */
	@Override
	public void init() {
		this.registerBeanDefinitionParser("config", new AnnotationDrivenEhCacheBeanDefinitionParser());
	}

}
