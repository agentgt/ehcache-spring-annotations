/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ehcache.annotations.integration;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MockCacheKeyGenerator implements CacheKeyGenerator<Serializable> {
    private static final List<MockCacheKeyGenerator> INSTANCES = new LinkedList<MockCacheKeyGenerator>();
    
    public static List<MockCacheKeyGenerator> getInstances() {
        return INSTANCES;
    }

    public MockCacheKeyGenerator() {
        synchronized (INSTANCES) {
            INSTANCES.add(this);
        }
    }
    
    private List<Object> listProperty;
    private TimeUnit enumProperty;
    
    public void setListProperty(List<Object> listProperty) {
        this.listProperty = listProperty;
    }

    public void setEnumProperty(TimeUnit enumProperty) {
        this.enumProperty = enumProperty;
    }
    
    public List<Object> getListProperty() {
        return this.listProperty;
    }

    public TimeUnit getEnumProperty() {
        return this.enumProperty;
    }

    public Serializable generateKey(MethodInvocation methodInvocation) {
        return "MyOneAndOnlyKey";
    }
}
