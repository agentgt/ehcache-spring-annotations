/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;


/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public final class IDKey implements Comparable<IDKey> {
    private final Object value;
    private final int id;

    /**
     * Constructor for IDKey
     * @param _value The value
     */
    public IDKey(Object _value) {
        // This is the Object hashcode 
        id = System.identityHashCode(_value);
        // There have been some cases (LANG-459) that return the 
        // same identity hash code for different objects.  So 
        // the value is also added to disambiguate these cases.
        value = _value;
    }
    
    public int compareTo(IDKey o) {
        return id < o.id ? -1 : (id == o.id ? 0 : 1);
    }

    /**
     * returns hashcode - i.e. the system identity hashcode.
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * checks if instances are equal
     * @param other The other object to compare to
     * @return if the instances are for the same object
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        final IDKey idKey = (IDKey) other;
        if (id != idKey.id) {
            return false;
        }
        // Note that identity equals is used.
        return value == idKey.value;
    }
}
