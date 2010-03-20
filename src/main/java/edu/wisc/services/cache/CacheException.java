/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.services.cache;


/**
 * Base runtime exception for this project.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version 
 */
public abstract class CacheException extends RuntimeException {
	private static final long serialVersionUID = 53706L;

	public CacheException(String msg) {
		super(msg);
	}

	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CacheException() {
		super();
	}

	public CacheException(Throwable cause) {
		super(cause);
	}
}
