/* 
 * Created on Oct 19, 2004
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
 * Copyright @2004 the original author or authors.
 */

package edu.wisc.services.cache.interceptor.flush;

import java.lang.reflect.Method;

/**
 * <p>
 * Interface used by <code>{@link MetadataFlushingInterceptor}</code> to
 * retrieve the flushing metadata attributes bound to intercepted methods.
 * </p>
 * 
 * @author Alex Ruiz
 */
public interface FlushingAttributeSource {

  /**
   * Returns the flushing metadata attribute bound to the intercepted method.
   * 
   * @param m
   *          the intercepted method
   * @param c
   *          the target class. May be <code>null</code>, in which case the
   *          declaring class of the method must be used
   * @return the flushing metadata attribute bound the intercepted method.
   */
  FlushCache attribute(Method m, Class c);
}