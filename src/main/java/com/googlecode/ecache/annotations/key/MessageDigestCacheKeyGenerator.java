/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

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
    private static final byte[] ZERO_AS_BYTES = new byte[] {0, 0, 0, 0};
    
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
