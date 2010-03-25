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

/**
 * 
 */
package edu.wisc.services.cache.integration;

import edu.wisc.services.cache.annotations.TriggersRemove;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class TriggersRemoveTestImpl implements TriggersRemoveTestInterface {
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#implMethodTriggersFlush()
	 */
	@Override
	@TriggersRemove(cacheName="triggersRemoveCountingCache")
    public void implMethodTriggersRemove() {
    }

    /* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#methodTriggersFlush()
	 */
	@Override
	public void methodTriggersRemove() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#methodTriggersFlushAndRemoveAll()
	 */
	@Override
	public void methodTriggersRemoveAll() {
	}

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.TriggersRemoveTestInterface#notFlushableMethod()
	 */
	@Override
	public void notTriggersRemoveMethod() {
	}
}
