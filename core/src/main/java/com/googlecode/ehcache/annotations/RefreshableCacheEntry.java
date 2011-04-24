/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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

import org.aopalliance.intercept.MethodInvocation;

/**
 * Holder class that tracks a cached object and the {@link MethodInvocation} that was
 * invoked to create the object.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class RefreshableCacheEntry {
	private final MethodInvocation methodInvocation;
	private volatile Object value;
	
	public RefreshableCacheEntry(MethodInvocation mi, Object value) {
		this.methodInvocation = mi;
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public MethodInvocation getMethodInvocation(){
		return methodInvocation;
	}

    @Override
    public String toString() {
        return "RefreshableCacheEntry [methodInvocation=" + this.methodInvocation + ", value=" + this.value + "]";
    }
}