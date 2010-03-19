/**
 * 
 */
package edu.wisc.services.cache.annotations;

import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import edu.wisc.services.cache.FlushableAttribute;
import edu.wisc.services.cache.FlushableAttributeSource;

/**
 * {@link StaticMethodMatcherPointcut} that delegates to the 
 * required {@link FlushableAttributeSource}; if the delegate returns
 * a non-null value, {@link #matches(Method, Class)} will return true.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class FlushableStaticMethodMatcherPointcut extends
		StaticMethodMatcherPointcut {

	private FlushableAttributeSource flushableAttributeSource;
	/**
	 * @param flushableAttributeSource the flushableAttributeSource to set
	 */
	public void setFlushableAttributeSource(
			FlushableAttributeSource flushableAttributeSource) {
		this.flushableAttributeSource = flushableAttributeSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.aop.MethodMatcher#matches(java.lang.reflect.Method, java.lang.Class)
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		FlushableAttribute attr = this.flushableAttributeSource.getFlushableAttribute(method, targetClass);
		return null != attr;
	}

}
