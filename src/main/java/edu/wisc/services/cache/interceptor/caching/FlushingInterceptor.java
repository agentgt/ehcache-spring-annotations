/**
 * 
 */
package edu.wisc.services.cache.interceptor.caching;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import edu.wisc.services.cache.FlushableAttribute;
import edu.wisc.services.cache.FlushableAttributeSource;

/**
 * {@link MethodInterceptor} that depends on a {@link FlushableAttributeSource}.
 * If a {@link FlushableAttribute} is associated with the {@link MethodInvocation}, 
 * {@link Ehcache#flush()} is called, an optionally {@link Ehcache#removeAll()} if
 * {@link FlushableAttribute#isRemoveAll()} is true.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 */
public class FlushingInterceptor implements MethodInterceptor {

	private FlushableAttributeSource flushableAttributeSource;
	
	/**
	 * @param flushableAttributeSource the flushableAttributeSource to set
	 */
	public void setFlushableAttributeSource(
			FlushableAttributeSource flushableAttributeSource) {
		this.flushableAttributeSource = flushableAttributeSource;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		final FlushableAttribute flushableAttribute = this.flushableAttributeSource.getFlushableAttribute(methodInvocation.getMethod(), methodInvocation.getClass());
		if(null == flushableAttribute) {
			return methodInvocation.proceed();
		}
		
		Ehcache cache = flushableAttribute.getCache();
		cache.flush();
		
		if(flushableAttribute.isRemoveAll()) {
			cache.removeAll();
		}
		
		Object result = methodInvocation.proceed();
		return result;
	}

}
