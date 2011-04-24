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
package com.googlecode.ehcache.annotations.impl;

import java.util.Comparator;

import com.googlecode.ehcache.annotations.Property;

/**
 * Utility class for sorting the {@link Property} annotations in a consistent manner.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
class PropertyComparator implements Comparator<Property> {
    public static final PropertyComparator INSTANCE = new PropertyComparator();

    public int compare(Property o1, Property o2) {
        int r = o1.name().compareTo(o2.name());
        if (r != 0) {
            return r;
        }
        
        r = o1.value().compareTo(o2.value());
        if (r != 0) {
            return r;
        }
        
        return o1.ref().compareTo(o2.ref());
    }
}
