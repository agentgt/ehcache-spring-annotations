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
import java.util.Map;

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
    public static final int HASH_CODE_BYTE_SIZE = 4;
    
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
    

    @Override
    protected String generateKey(Object... data) {
        MessageDigest digester;
        try {
            digester = (MessageDigest) this.messageDigest.clone();
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

        final byte[] hashBytes = new byte[HASH_CODE_BYTE_SIZE];
        this.deepDigest(hashBytes, digester, data);


        final byte[] digest = digester.digest();
        return Base64.encodeBase64URLSafeString(digest);
    }

    /**
     * Tries to clone the {@link MessageDigest} that was created during construction. If the clone fails
     * that is remembered and from that point on new {@link MessageDigest} instances will be created on
     * every call.
     * 
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

    /**
     * Does a deep traversal, iterating over arrays, collections and maps and map entries. When
     * a leaf in the object graph is found the appropriate digest method is called.
     * 
     * If {@link #setCheckforCycles(boolean)} is true and a cycle is found nothing is appeneded
     * to the {@link MessageDigest} the second time the object is encountered.
     * 
     * @param hashBytes A byte array of length {@link #HASH_CODE_BYTE_SIZE} used when converting a hash code to byte array. Passed around to avoid extra object creation.
     * @param messageDigest The {@link MessageDigest} used to generate the file hash key
     * @param o The object to inspect
     */
    protected final void deepDigest(byte[] hashBytes, MessageDigest messageDigest, Object o) {
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
                this.digest(hashBytes, messageDigest, (Class<?>) o);
            }
            else if (o.getClass().isArray()) {
                final int length = Array.getLength(o);
                for (int index = 0; index < length; index++) {
                    final Object arrayValue = Array.get(o, index);
                    this.deepDigest(hashBytes, messageDigest, arrayValue);
                }
            }
            else if (o instanceof Iterable<?>) {
                for (final Object e : ((Iterable<?>) o)) {
                    this.deepDigest(hashBytes, messageDigest, e);
                }
            }
            else if (o instanceof Map<?, ?>) {
                this.deepDigest(hashBytes, messageDigest, ((Map<?, ?>) o).entrySet());
            }
            else if (o instanceof Map.Entry<?, ?>) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                this.deepDigest(hashBytes, messageDigest, entry.getKey());
                this.deepDigest(hashBytes, messageDigest, entry.getValue());
            }
            else {
                this.digest(hashBytes, messageDigest, o);
            }
        }
        finally {
            unregister(o);
        }
    }

    /**
     * Special handling for digesting a {@link Class} which does not implement hashCode.
     * 
     * Default implmentation is to digest {@link Class#getName()}
     */
    protected void digest(byte[] hashBytes, MessageDigest messageDigest, Class<?> c) {
        this.digest(hashBytes, messageDigest, c.getName());
    }

    /**
     * Add an object to the digest. Default adds the object's hashCode to the digest
     */
    protected void digest(byte[] hashBytes, MessageDigest messageDigest, Object o) {
        intToBytes(hashBytes, o.hashCode());
        messageDigest.update(hashBytes, 0, HASH_CODE_BYTE_SIZE);
    }

    /**
     * Convert an int to a byte array. The passed in byte[] must be at least
     * 4 bytes long, if longer the bytes above the fourth are ignored.
     * 
     * @return the same byte[] that was passed in
     */
    protected final byte[] intToBytes(byte[] hashBytes, int value) {
        hashBytes[0] = (byte) (value >>> 24);
        hashBytes[1] = (byte) (value >>> 16);
        hashBytes[2] = (byte) (value >>> 8);
        hashBytes[3] = (byte) value;

        return hashBytes;
    }
}
