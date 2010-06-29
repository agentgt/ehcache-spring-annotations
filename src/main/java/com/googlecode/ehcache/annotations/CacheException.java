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

package com.googlecode.ehcache.annotations;


/**
 * Base runtime exception for this project.
 * 
 * @author Nicholas Blair
 * @version $Revision$
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
