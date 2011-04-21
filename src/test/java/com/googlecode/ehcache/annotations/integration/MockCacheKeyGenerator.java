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
package com.googlecode.ehcache.annotations.integration;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MockCacheKeyGenerator implements CacheKeyGenerator<Serializable>, InitializingBean {
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
    private boolean initialized = false;
    
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void afterPropertiesSet() throws Exception {
        this.initialized = true;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }

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

    public Serializable generateKey(Object... data) {
        return "MyOneAndOnlyKey";
    }
}
