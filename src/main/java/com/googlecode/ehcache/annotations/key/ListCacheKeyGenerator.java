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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ListCacheKeyGenerator extends AbstractDeepCacheKeyGenerator<ListCacheKeyGenerator.ListKeyGenerator, ReadOnlyList<?>> {
    /**
     * Name of the bean this generator is registered under using the default constructor.
     */
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.ListCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    public static class ListKeyGenerator {
        private final LinkedList<ArrayList<Object>> keyStack = new LinkedList<ArrayList<Object>>();
        private ArrayList<Object> current;
        
        private ListKeyGenerator() {
        }
    }
    
    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator() 
     */
    public ListCacheKeyGenerator() {
    }

    /**
     * @see AbstractCacheKeyGenerator#AbstractCacheKeyGenerator(boolean, boolean) 
     */
    public ListCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }

    @Override
    public ListKeyGenerator getGenerator(Object... data) {
        return new ListKeyGenerator();
    }
    
    @Override
    public ReadOnlyList<?> generateKey(ListKeyGenerator generator) {
        final ArrayList<Object> key = generator.keyStack.getFirst();
        return new ReadOnlyList<Object>(key);
    }
    
    @Override
    protected void beginRecursion(ListKeyGenerator generator, Object e) {
        //Track the previous list
        final ArrayList<Object> previous = generator.current;
        
        //Create the new list doing best effort to create it with the correct size
        if (e.getClass().isArray()) {
            final int size = Array.getLength(e);
            generator.current = new ArrayList<Object>(size);
        }
        else if (e instanceof Collection<?>) {
            final int size = ((Collection<?>)e).size();
            generator.current = new ArrayList<Object>(size);
        }
        else if (e instanceof Map.Entry<?, ?>) {
            generator.current = new ArrayList<Object>(2);
        }
        else {
            generator.current = new ArrayList<Object>();
        }
        
        //Stick the new list on the stack
        generator.keyStack.addFirst(generator.current);
        
        //If there was a previous list add the new list to it
        if (previous != null) {
            previous.add(generator.current);
        }
    }

    @Override
    protected void endRecursion(ListKeyGenerator generator, Object e) {
        if (generator.keyStack.size() > 1) {
            generator.keyStack.removeFirst();
            generator.current = generator.keyStack.peek();
        }
    }

    @Override
    protected void appendGraphCycle(ListKeyGenerator generator, Object o) {
        this.append(generator, (Object)null);
    }

    @Override
    protected void appendNull(ListKeyGenerator generator) {
        this.append(generator, (Object)null);
    }

    @Override
    protected void append(ListKeyGenerator generator, Object e) {
        if (generator.current == null) {
            this.beginRecursion(generator, e);
        }
        
        generator.current.add(e);
    }
}
