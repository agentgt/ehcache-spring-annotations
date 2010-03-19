/**
 * 
 */
package edu.wisc.services.cache.annotations;

import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.TriggersRemoveAttributeSource;

/**
 * {@link StaticMethodMatcherPointcut} that delegates to the 
 * required {@link TriggersRemoveAttributeSource}; if the delegate returns
 * a non-null value, {@link #matches(Method, Class)} will return true.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class TriggersRemoveStaticMethodMatcherPointcut extends
		StaticMethodMatcherPointcut {

	private TriggersRemoveAttributeSource flushableAttributeSource;
	/**
	 * @param flushableAttributeSource the flushableAttributeSource to set
	 */
	public void setFlushableAttributeSource(
			TriggersRemoveAttributeSource flushableAttributeSource) {
		this.flushableAttributeSource = flushableAttributeSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.aop.MethodMatcher#matches(java.lang.reflect.Method, java.lang.Class)
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		TriggersRemoveAttribute attr = this.flushableAttributeSource.getFlushableAttribute(method, targetClass);
		return null != attr;
	}

}
