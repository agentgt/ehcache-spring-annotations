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

import java.util.Arrays;

/**
 * Represents a mask for method parameters, tracking which parameters are included in the
 * mask by a boolean array. Two utility methods are included which can mask or unmask a
 * parameter array.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ParameterMask {
    public static final ParameterMask NO_MASK = new ParameterMask();
    
    public static ParameterMask create(boolean[] mask) {
        int parameterCount = mask.length;
        for (final boolean b : mask) {
            if (!b) {
                parameterCount--;
            }
        }
        
        if (parameterCount == 0 || parameterCount == mask.length) {
            return NO_MASK;
        }
        
        return new ParameterMask(mask, parameterCount);
    }
    
    private final boolean[] mask;
    private final int parameterCount;
    private final boolean shouldMask;
    
    private ParameterMask() {
        this.mask = new boolean[0];
        this.parameterCount = 0;
        this.shouldMask = false;
    }

    private ParameterMask(boolean[] mask, int parameterCount) {
        this.mask = mask;
        this.parameterCount = parameterCount;
        this.shouldMask = true;
    }
    
    /**
     * If no parameters are marked to be masked or all parameters are marked to be masked masking
     * serves no purpose. In that case this method will return false and {@link #maskParameters(Object[])}
     * will simply return its argument.
     * 
     * @return true if this mask will result in a change to a masked parameter array. False if not.
     */
    public boolean shouldMask() {
        return this.shouldMask;
    }
    
    /**
     * @return A copy of the mask array
     */
    public boolean[] getMask() {
        if (this.mask.length == 0) {
            return this.mask;
        }
        
        final boolean[] maskCopy = new boolean[this.mask.length];
        System.arraycopy(this.mask, 0, maskCopy, 0, this.mask.length);
        return maskCopy;
    }

    /**
     * Masks a parameter array. The returned array will have size equal to the number of true values
     * in the mask. Only the parameter values from the source array that have a corresponding true
     * value in the mask will be in the returned array.
     * 
     * @see #shouldMask
     */
    public Object[] maskParameters(Object[] parameters) {
        if (!this.shouldMask) {
            return parameters;
        }
        
        int maskedIndex = 0;
        final Object[] maskedParameters = new Object[this.parameterCount];
        
        for (int index = 0; index < parameters.length; index++) {
            if (this.mask[index]) {
                maskedParameters[maskedIndex++] = parameters[index];
            }
        }
        
        return maskedParameters;
    }
    
    /**
     * Mutates the originalParameters array by copying values from the maskedParameters array back
     * into their original locations.
     */
    public void unmaskParameters(Object[] originalParameters, Object[] maskedParameters) {
        if (!this.shouldMask || originalParameters == maskedParameters) {
            return;
        }
        
        int maskedIndex = 0;
        
        for (int index = 0; index < originalParameters.length; index++) {
            if (this.mask[index]) {
                originalParameters[index] = maskedParameters[maskedIndex++];
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.mask);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ParameterMask other = (ParameterMask) obj;
        if (!Arrays.equals(this.mask, other.mask)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ParameterMask [mask=" + Arrays.toString(this.mask) + "]";
    }
}