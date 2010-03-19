/**
 * 
 */
package edu.wisc.services.cache.interceptor.caching;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.TriggersRemoveAttributeSource;
import edu.wisc.services.cache.key.CacheKeyGenerator;

/**
 * {@link MethodInterceptor} that depends on a {@link TriggersRemoveAttributeSource}.
 * If a {@link TriggersRemoveAttribute} is associated with the {@link MethodInvocation}, 
 * {@link Ehcache#flush()} is called, an optionally {@link Ehcache#removeAll()} if
 * {@link TriggersRemoveAttribute#isRemoveAll()} is true.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 */
public class TriggersRemoveInterceptor implements MethodInterceptor {

	private TriggersRemoveAttributeSource flushableAttributeSource;
	
	/**
	 * @param flushableAttributeSource the flushableAttributeSource to set
	 */
	public void setFlushableAttributeSource(
			TriggersRemoveAttributeSource flushableAttributeSource) {
		this.flushableAttributeSource = flushableAttributeSource;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		final TriggersRemoveAttribute flushableAttribute = this.flushableAttributeSource.getFlushableAttribute(methodInvocation.getMethod(), methodInvocation.getClass());
		if(null == flushableAttribute) {
			return methodInvocation.proceed();
		}
		
		Ehcache cache = flushableAttribute.getCache();
		if(flushableAttribute.isRemoveAll()) {
			cache.removeAll();
		} else {
			CacheKeyGenerator cacheKeyGenerator = flushableAttribute.getCacheKeyGenerator();
			Serializable cacheKey = cacheKeyGenerator.generateKey(methodInvocation);
			cache.remove(cacheKey);
		}
		
		Object result = methodInvocation.proceed();
		return result;
	}

}
