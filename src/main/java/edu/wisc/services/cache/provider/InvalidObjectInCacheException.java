/*
 * Created on Aug 2, 2005
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
 * Copyright @2007 the original author or authors.
 */
package edu.wisc.services.cache.provider;

import edu.wisc.services.cache.CacheException;

/**
 * Exception thrown when an object retrieved from the cache is of an inexpected type.
 *
 * @author Omar Irbouh
 * @see edu.wisc.services.cache.serializable.SerializableFactory
 * @see edu.wisc.services.cache.provider.AbstractCacheProviderFacade
 */
public class InvalidObjectInCacheException extends CacheException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3686847813364769426L;

	/**
	 * Construct a <code>InvalidObjectInCacheException</code> with the specified
	 * detail message.
	 *
	 * @param msg the detail message
	 */
	public InvalidObjectInCacheException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public InvalidObjectInCacheException(String msg, Throwable cause) {
		super(msg, cause);
	}
	

}