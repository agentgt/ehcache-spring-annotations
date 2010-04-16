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

import java.io.DataOutputStream;
import java.io.IOException;
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
    protected static final int DEFAULT_BYTE_BUFFER_SIZE = 64;
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final MessageDigest baseMessageDigest;
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
        this.baseMessageDigest = MessageDigest.getInstance(algorithm);
    }
    

    @Override
    public String generateKey(Object... data) {
        final MessageDigest messageDigest = this.getMessageDigest();
        
        final MessageDigestOutputStream messageDigestOutputStream = new MessageDigestOutputStream(messageDigest);
        final DataOutputStream dataOutputStream = new DataOutputStream(messageDigestOutputStream);
        
        try {
            this.deepDigest(dataOutputStream, data);
        }
        catch (IOException ioe) {
            throw new IllegalStateException("IOExceptions should not be possible from this key generator");
        }

        final byte[] digest = messageDigest.digest();
        return this.encodeHash(digest);
    }

    /**
     * Encode the digested hash bytes as a String
     */
    protected String encodeHash(final byte[] digest) {
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
            final String algorithm = this.baseMessageDigest.getAlgorithm();
            try {
                return MessageDigest.getInstance(algorithm);
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("MessageDigest algorithm '" + algorithm + "' was supported when " + this.getClass().getSimpleName() + " was created but is not now. This should not be possible.", e);
            }
        }
        
        try {
            return (MessageDigest)this.baseMessageDigest.clone();
        }
        catch (CloneNotSupportedException e) {
            this.cloneNotSupported = true;
            this.logger.warn("Could not clone MessageDigest using algorithm '" + this.baseMessageDigest.getAlgorithm() + "'. MessageDigest.getInstance will be used from now on which will be much more expensive.", e);
            return this.getMessageDigest();
        }
    }

    /**
     * Does a deep traversal, iterating over arrays, collections and maps and map entries. When
     * a leaf in the object graph is found the appropriate digest method is called.
     * 
     * If {@link #setCheckforCycles(boolean)} is true and a cycle is found nothing is appended
     * to the {@link MessageDigest} the second time the object is encountered.
     * 
     * @param o The object to inspect
     */
    protected final void deepDigest(DataOutputStream dataOutputStream, Object o) throws IOException {
        if (o == null || !register(o)) {
            dataOutputStream.write(0);
            return;
        }
        
        try {
            if (o instanceof Class<?>) {
                this.digest(dataOutputStream, (Class<?>) o);
            }
            else if (o instanceof Enum<?>) {
                this.digest(dataOutputStream, (Enum<?>) o);
            }
            else if (o.getClass().isArray()) {
                final int length = Array.getLength(o);
                for (int index = 0; index < length; index++) {
                    final Object arrayValue = Array.get(o, index);
                    this.deepDigest(dataOutputStream, arrayValue);
                }
            }
            else if (o instanceof Iterable<?>) {
                for (final Object e : ((Iterable<?>) o)) {
                    this.deepDigest(dataOutputStream, e);
                }
            }
            else if (o instanceof Map<?, ?>) {
                this.deepDigest(dataOutputStream, ((Map<?, ?>) o).entrySet());
            }
            else if (o instanceof Map.Entry<?, ?>) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                this.deepDigest(dataOutputStream, entry.getKey());
                this.deepDigest(dataOutputStream, entry.getValue());
            }
            else if (o instanceof String) {
                dataOutputStream.writeUTF((String)o);
            }
            else if (o instanceof Boolean) {
                dataOutputStream.writeBoolean(((Boolean)o).booleanValue());
            }
            else if (o instanceof Byte) {
                dataOutputStream.writeByte(((Byte)o).byteValue());
            }
            else if (o instanceof Character) {
                dataOutputStream.writeChar(((Character)o).charValue());
            }
            else if (o instanceof Double) {
                dataOutputStream.writeDouble(((Double)o).doubleValue());
            }
            else if (o instanceof Float) {
                dataOutputStream.writeFloat(((Float)o).floatValue());
            }
            else if (o instanceof Integer) {
                dataOutputStream.writeInt(((Integer)o).intValue());
            }
            else if (o instanceof Long) {
                dataOutputStream.writeLong(((Long)o).longValue());
            }
            else if (o instanceof Short) {
                dataOutputStream.writeShort(((Short)o).shortValue());
            }
            else {
                this.digest(dataOutputStream, o);
            }
        }
        finally {
            unregister(o);
        }
    }

    /**
     * Special handling for digesting a {@link Class} which does not implement hashCode.
     * 
     * Default implementation is to digest {@link Class#getName()}
     */
    protected void digest(DataOutputStream dataOutputStream, Class<?> c) throws IOException {
        this.digest(dataOutputStream, c.getName());
    }
    
    /**
     * Generate hash code for an Enum, uses a combination of the Class and name to generate a consistent hash code
     */
    protected void digest(DataOutputStream dataOutputStream, Enum<?> e) throws IOException {
        this.deepDigest(dataOutputStream, new Object[] { e.getClass(), e.name() });
    }

    /**
     * Add an object to the digest. Default adds the object's hashCode to the digest
     */
    protected void digest(DataOutputStream dataOutputStream, Object o) throws IOException {
        dataOutputStream.writeInt(o.hashCode());
    }
}
