/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package com.googlecode.ecache.annotations.key;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestCacheKeyGenerator implements CacheKeyGenerator<String> {
    private static final byte[] ZERO_AS_BYTES = intToByteArray(0);
    
    private final MessageDigest messageDigest;
    private boolean includeMethod = true;
    
    public MessageDigestCacheKeyGenerator() throws NoSuchAlgorithmException {
        this("SHA-1", true);
    }
    
    public MessageDigestCacheKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
        this(algorithm, true);
    }
    
    public MessageDigestCacheKeyGenerator(boolean includeMethod) throws NoSuchAlgorithmException {
        this("SHA-1", includeMethod);
    }
    
    /**
     * @see MessageDigestCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public MessageDigestCacheKeyGenerator(String algorithm, boolean includeMethod) throws NoSuchAlgorithmException {
        this.includeMethod = includeMethod;
        this.messageDigest = MessageDigest.getInstance(algorithm);
    }

    /**
     * @see MessageDigestCacheKeyGenerator#setIncludeMethod(boolean)
     */
    public boolean isIncludeMethod() {
        return includeMethod;
    }

    /**
     * @param includeMethod true If the {@link Method} from the {@link MethodInvocation} should be included in the generated key. Defaults to true.
     */
    public void setIncludeMethod(boolean includeMethod) {
        this.includeMethod = includeMethod;
    }

    /* (non-Javadoc)
     * @see com.googlecode.ecache.annotations.key.CacheKeyGenerator#generateKey(org.aopalliance.intercept.MethodInvocation)
     */
    public String generateKey(MethodInvocation methodInvocation) {
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
        
        final Object[] arguments = methodInvocation.getArguments();
        
        if (this.includeMethod) {
            final Method method = methodInvocation.getMethod();
            digestObject(digester, method.getDeclaringClass());
            digestObject(digester, method.getName());
            digestObject(digester, method.getReturnType());
            digestObject(digester, method.getParameterTypes());
        }
        
        for (final Object arg : arguments) {
            digestObject(digester, arg);
        }
        
        final byte[] digest = digester.digest();
        return Base64.encodeBase64URLSafeString(digest);
    }
    
    protected void digestObject(MessageDigest messageDigest, Object o) {
        if (o == null) {
            messageDigest.update(ZERO_AS_BYTES);
        }
        else if (o instanceof Class<?>) {
            this.digestObject(messageDigest, ((Class<?>)o).getCanonicalName());
        }
        else if (o.getClass().isArray()) {
            final int length = Array.getLength(o);
            for (int index = 0; index < length; index++) {
                final Object arrayValue = Array.get(o, index);
                this.digestObject(messageDigest, arrayValue);
            }
        }
        else {
            final byte[] hashBytes = intToByteArray(o.hashCode());
            messageDigest.update(hashBytes);
        }
    }
    
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

}
