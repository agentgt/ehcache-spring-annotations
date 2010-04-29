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

package com.googlecode.ehcache.annotations.key;

/**
 * Little key object that doesn't implement {@link #hashCode()}, {@link #equals(Object)}, or {@link #toString()}
 * 
 * The static and transient fields should be ignored and the random values ensure this since if they are included
 * the key will change on every execution
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class RequiresReflectionKey {
    public static final double RANDOM_CONSTANT = Math.random();
    public transient final double randomField = Math.random();
    public Object key;
    
    public RequiresReflectionKey() {
    }

    public RequiresReflectionKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public double getRandomField() {
        return this.randomField;
    }

    public static double getRandomConstant() {
        return RANDOM_CONSTANT;
    }
}
