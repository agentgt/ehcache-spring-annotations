package com.googlecode.ehcache.annotations.aspectj;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;

import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.interceptor.EhCacheInterceptor;

@SuppressWarnings("unused") // cacheable annotation
@Aspect
public class AnnotationEhCacheAspect {
	
	private EhCacheInterceptor interceptor = new EhCacheInterceptor();
	private CacheAttributeSource cacheAttributeSource;
	
	@SuppressAjWarnings("adviceDidNotMatch")
	@Around("execution(@Cacheable * * (..))")
	public Object aroundCache(final ProceedingJoinPoint jp) throws Throwable {
		AspectJAopAllianceMethod m = new AspectJAopAllianceMethod(jp);
		return interceptor.invoke(m);
	}
	
	@SuppressAjWarnings("adviceDidNotMatch")
	@Around("execution(@TriggersRemove * * (..))")
	public Object aroundTriggersRemove(final ProceedingJoinPoint jp) throws Throwable {
		AspectJAopAllianceMethod m = new AspectJAopAllianceMethod(jp);
		return interceptor.invoke(m);
	}
	
	protected CacheAttributeSource getCacheAttributeSource() {
		return cacheAttributeSource;
	}

	public void setCacheAttributeSource(CacheAttributeSource cacheAttributeSource) {
		this.cacheAttributeSource = cacheAttributeSource;
		interceptor.setCacheAttributeSource(cacheAttributeSource);
	}
	
	private static class AspectJAopAllianceMethod implements MethodInvocation {
		private ProceedingJoinPoint jp;
		
		public AspectJAopAllianceMethod(ProceedingJoinPoint jp) {
			super();
			this.jp = jp;
		}

		public Object[] getArguments() {
			return jp.getArgs();
		}

		public Object proceed() throws Throwable {
			return jp.proceed();
		}

		public Object getThis() {
			return jp.getThis();
		}

		public AccessibleObject getStaticPart() {
			throw new UnsupportedOperationException("getStaticPart is not yet supported");
		}

		public Method getMethod() {
			MethodSignature methodSignature = (MethodSignature) jp.getSignature();
			return methodSignature.getMethod();

		}
	
	}
}
