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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This key generator is a good option when a secure hash of the arguments is needed or when a
 * larger final key space is desired. As with {@link HashCodeCacheKeyGenerator} the argument
 * hashCode is used for key generation data but instead of the simple Java hash algorithm being
 * used any {@link MessageDigest} can be used, SHA-1 is used by default. The generated hash is
 * base64 encoded using {@link Base64#encodeBase64URLSafeString(byte[])}
 * 
 * <table>
 *  <tr>
 *      <th>Pros</th>
 *      <th>Cons</th>
 *  </tr>
 *  <tr>
 *      <td>
 *          Better assurance than {@link HashCodeCacheKeyGenerator} against key collisions though
 *          with the argument hashCodes being the source of the data it is still not perfect.
 *      </td>
 *      <td>
 *          Slower than {@link HashCodeCacheKeyGenerator}
 *      </td>
 *  </tr>
 * </table>
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGenerator extends AbstractCacheKeyGenerator<String> {
    public static final String DEFAULT_BEAN_NAME = "com.googlecode.ehcache.annotations.key.MessageDigestCacheKeyGenerator.DEFAULT_BEAN_NAME";
    public static final String DEFAULT_ALGORITHM = "SHA-1";
    
    private static final byte[] ZERO_AS_BYTES = new byte[] {0, 0, 0, 0};
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final MessageDigest messageDigest;
    private boolean cloneNotSupported;
    
    public MessageDigestCacheKeyGenerator() throws NoSuchAlgorithmException {
        this(DEFAULT_ALGORITHM, true, false);
    }
    
    public MessageDigestCacheKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
        this(algorithm, true, false);
    }
    
    public MessageDigestCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) throws NoSuchAlgorithmException {
        this(DEFAULT_ALGORITHM, includeMethod, includeParameterTypes);
    }
    
    public MessageDigestCacheKeyGenerator(String algorithm, boolean includeMethod, boolean includeParameterTypes) throws NoSuchAlgorithmException {
        super(includeMethod, includeParameterTypes);
        this.messageDigest = MessageDigest.getInstance(algorithm);
    }
    
    /**
     * @return Generates a {@link MessageDigest} to use during a call to {@link #generateKey(Object...)}
     */
    protected MessageDigest getMessageDigest() {
        if (this.cloneNotSupported) {
            final String algorithm = this.messageDigest.getAlgorithm();
                try {
                return MessageDigest.getInstance(algorithm);
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("MessageDigest algorithm '" + algorithm + "' was supported when " + this.getClass().getSimpleName() + " was created but is not now. This should not be possible.", e);
            }
        }
        
        try {
            return (MessageDigest)this.messageDigest.clone();
        }
        catch (CloneNotSupportedException e) {
            this.cloneNotSupported = true;
            this.logger.warn("Could not clone MessageDigest using algorithm '" + this.messageDigest.getAlgorithm() + "'. MessageDigest.getInstance will be used from now on which will be much more expensive.", e);
            return this.getMessageDigest();
        }
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
                final byte[] bytes = intToBytes(o.hashCode());
                digest(messageDigest, bytes);
            }
        }
        finally {
            unregister(o);
        }
    }

    protected byte[] intToBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
