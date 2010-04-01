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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGenerator extends AbstractCacheKeyGenerator<String> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.MessageDigestCacheKeyGenerator.DEFAULT_BEAN_NAME";
    
    protected static final byte[] ZERO_AS_BYTES = new byte[] {0, 0, 0, 0};
    
    private final MessageDigest messageDigest;
    
    public MessageDigestCacheKeyGenerator() throws NoSuchAlgorithmException {
        this("SHA-1", true, false);
    }
    
    public MessageDigestCacheKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
        this(algorithm, true, false);
    }
    
    public MessageDigestCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) throws NoSuchAlgorithmException {
        this("SHA-1", includeMethod, includeParameterTypes);
    }
    
    public MessageDigestCacheKeyGenerator(String algorithm, boolean includeMethod, boolean includeParameterTypes) throws NoSuchAlgorithmException {
        super(includeMethod, includeParameterTypes);
        this.messageDigest = MessageDigest.getInstance(algorithm);
    }
    
    @Override
    protected String generateKey(Object... data) {
        MessageDigest digester;
        try {
            digester = (MessageDigest)this.messageDigest.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Cannot clone MessageDigest");
            try {
                digester = MessageDigest.getInstance(this.messageDigest.getAlgorithm());
            }
            catch (NoSuchAlgorithmException e1) {
                throw new RuntimeException(e1);
            }
        }
        
        for (final Object arg : data) {
            digest(digester, arg);
        }
        
        final byte[] digest = digester.digest();
        return Base64.encodeBase64URLSafeString(digest);
    }
    
    protected void digest(MessageDigest messageDigest, Object o) {
        if (o == null) {
            messageDigest.update(ZERO_AS_BYTES);
            return;
        }
        
        if (!register(o)) {
            //Return without digesting anything in the case of a circular reference
            return;
        }
        try {
            if (o instanceof Class<?>) {
                this.digest(messageDigest, ((Class<?>)o).getName());
            }
            else if (o.getClass().isArray()) {
                final int length = Array.getLength(o);
                for (int index = 0; index < length; index++) {
                    final Object arrayValue = Array.get(o, index);
                    this.digest(messageDigest, arrayValue);
                }
            }
            else {
                digest(messageDigest, o.hashCode());
            }
        }
        finally {
            unregister(o);
        }
    }
    
    protected void digest(MessageDigest messageDigest, int value) {
        messageDigest.update((byte)(value >>> 24));
        messageDigest.update((byte)(value >>> 16));
        messageDigest.update((byte)(value >>> 8));
        messageDigest.update((byte)value);
    }
}
