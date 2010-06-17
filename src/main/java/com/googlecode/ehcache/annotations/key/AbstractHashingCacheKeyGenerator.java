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

import java.io.Serializable;

/**
 * Common base class for key generators that primarily rely on {@link Object#hashCode()}. Has special handling for
 * {@link Class} and {@link Enum} types as their default hashCode implementations are not portable between JVM
 * instances or versions.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class AbstractHashingCacheKeyGenerator<G, T extends Serializable> extends AbstractDeepCacheKeyGenerator<G, T> {

    public AbstractHashingCacheKeyGenerator() {
    }

    public AbstractHashingCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
        super(includeMethod, includeParameterTypes);
    }

    /**
     * Uses {@link Class#getName()} for the hash code
     */
    protected void appendClass(G generator, Class<?> e) {
        this.append(generator, ((Class<?>)e).getName());
    }

    /**
     * Uses an object array of {@link Enum#getClass()} and {@link Enum#name()} for the hash code
     */
    protected void appendEnum(G generator, Enum<?> e) {
        //Handling for JDK5, enums are anonymous inner classes in JDK5 and we only want the base enum class name
        if (e.getClass().isAnonymousClass()) {
            this.deepHashCode(generator, new Object[] { e.getClass().getEnclosingClass(), e.name() });
        }
        else {
            this.deepHashCode(generator, new Object[] { e.getClass(), e.name() });
        }
    }
    
    @Override
    protected final void append(G generator, Object e) {
        if (e instanceof Class<?>) {
            this.appendClass(generator, (Class<?>)e);
        }
        else if (e instanceof Enum<?>) {
            this.appendEnum(generator, (Enum<?>)e);
        }
        else {
            this.appendHash(generator, e);
        }
    }

    @Override
    protected boolean shouldReflect(Object element) {
        return !this.getReflectionHelper().implementsHashCode(element);
    }
    
    protected abstract void appendHash(G generator, Object e);

}