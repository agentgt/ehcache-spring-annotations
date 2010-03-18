/* 
 * Created on Apr 13, 2006
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
package edu.wisc.services.cache.impl;

/**
 * Exception thrown if an object cannot be copied using serialization.
 * 
 * @author Alex Ruiz
 */
public class ObjectCannotBeCopiedException extends CachingException {

  private static final long serialVersionUID = 8361559127636476834L;

  /**
   * Constructor.
   * 
   * @param detailMessage
   *          the detail message
   */
  public ObjectCannotBeCopiedException(String detailMessage) {
    super(detailMessage);
  }

  /**
   * Constructor.
   * 
   * @param detailMessage
   *          the detail message
   * @param nested
   *          the nested exception
   */
  public ObjectCannotBeCopiedException(String detailMessage, Throwable nested) {
    super(detailMessage, nested);
  }

}
