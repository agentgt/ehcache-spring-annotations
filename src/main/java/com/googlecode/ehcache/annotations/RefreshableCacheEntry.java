package com.googlecode.ehcache.annotations;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Holder class that tracks a cached object and the {@link MethodInvocation} that was
 * invoked to create the object.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class RefreshableCacheEntry {
	private final MethodInvocation methodInvocation;
	private volatile Object value;
	
	public RefreshableCacheEntry(MethodInvocation mi, Object value) {
		this.methodInvocation = mi;
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public MethodInvocation getMethodInvocation(){
		return methodInvocation;
	}

    @Override
    public String toString() {
        return "RefreshableCacheEntry [methodInvocation=" + this.methodInvocation + ", value=" + this.value + "]";
    }
}