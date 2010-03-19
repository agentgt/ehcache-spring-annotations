/**
 * 
 */
package edu.wisc.services.cache.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import edu.wisc.services.cache.TriggersRemoveAttribute;
import edu.wisc.services.cache.TriggersRemoveAttributeSource;
import edu.wisc.services.cache.annotations.TriggersRemove;
import edu.wisc.services.cache.config.AnnotationDrivenEhCacheBeanDefinitionParser;
import edu.wisc.services.cache.key.CacheKeyGenerator;
import edu.wisc.services.cache.provider.CacheNotFoundException;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class TriggersRemoveAttributeSourceImpl implements TriggersRemoveAttributeSource, BeanFactoryAware {
	private static final TriggersRemoveAttribute NULL_FLUSHABLE_ATTRIBUTE = new TriggersRemoveAttribute() {
		@Override
		public boolean isRemoveAll() {
			return false;
		}
		@Override
		public Ehcache getCache() {
			return null;
		}
		@Override
		public CacheKeyGenerator getCacheKeyGenerator() {
			return null;
		}
	};
	/**
	 * Cache of {@link TriggersRemove}, keyed by DefaultCacheKey (Method + target Class).
	 */
	private final Map<Object, TriggersRemoveAttribute> attributeCache = new ConcurrentHashMap<Object, TriggersRemoveAttribute>();

	private BeanFactory beanFactory;
	private CacheManager cacheManager;
	private String cacheManagerBeanName;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.FlushableAttributeSource#getFlushableAttribute(java.lang.reflect.Method, java.lang.Class)
	 */
	@Override
	public TriggersRemoveAttribute getFlushableAttribute(Method method,
			Class<?> targetClass) {
		 // First, see if we have a cached value.
        Object cacheKey = getCacheKey(method, targetClass);
        final TriggersRemoveAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            // Value will either be canonical value indicating there is no transaction attribute,
            // or an actual cacheable attribute.
            if (cached == NULL_FLUSHABLE_ATTRIBUTE) {
                return null;
            }
            return cached;
        }
        final TriggersRemoveAttribute att = computeFlushableAttribute(method, targetClass);
        // Put it in the cache.
        if (att == null) {
            this.attributeCache.put(cacheKey, NULL_FLUSHABLE_ATTRIBUTE);
        }
        else {
            this.log.debug("Adding Flushable method '{}' under key '{}' with attribute: {}", new Object[] { method.getName(), cacheKey, att });
            this.attributeCache.put(cacheKey, att);
        }
        return att;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
     * Looks up the CacheManager by the configured cacheManagerBeanName if set. If not set calls
     * {@link BeanFactory#getBean(Class)} to locate a CacheManager.
     * 
     * @return The lazy-loaded CacheManager.
     */
    protected CacheManager getCacheManager() {
        if (this.cacheManager == null) {
            if (this.cacheManagerBeanName != null) {
                this.cacheManager = this.beanFactory.getBean(this.cacheManagerBeanName, CacheManager.class);
            }
            else {
                this.cacheManager = this.beanFactory.getBean(CacheManager.class);
            }
        }
        
        return this.cacheManager;
    }
	/**
	 * Get or create the specified cache if it does not exist and createCaches is set to true. 
	 * 
	 * @param cacheName The name of the cache to retrieve
	 * @return The cache
	 * @throws RuntimeException if the cache does not exist and createCaches is false.
	 */
	protected Ehcache getCache(final String cacheName) {
		final CacheManager cacheManager = this.getCacheManager();

		Ehcache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			throw new CacheNotFoundException("Cache '" + cacheName + "' does not exist");
		}
		return cache;
	}
	/**
	 * 
	 * @param method
	 * @param targetClass
	 * @return
	 */
	protected Object getCacheKey(Method method, Class<?> targetClass) {
		return new DefaultCacheKey(method, targetClass);
	}
	/**
     * Subclasses need to implement this to return the transaction attribute
     * for the given method, if any.
     * @param method the method to retrieve the attribute for
     * @return all transaction attribute associated with this method
     * (or <code>null</code> if none)
     */
    protected TriggersRemoveAttribute findFlushableAttribute(AnnotatedElement ae) {
        TriggersRemove ann = ae.getAnnotation(TriggersRemove.class);
        if (ann == null) {
            for (Annotation metaAnn : ae.getAnnotations()) {
                ann = metaAnn.annotationType().getAnnotation(TriggersRemove.class);
                if (ann != null) {
                    break;
                }
            }
        }
        if (ann != null) {
            return parseFlushableAnnotation(ann);
        }

        return null;
    }


    protected TriggersRemoveAttribute parseFlushableAnnotation(TriggersRemove ann) {
        final Ehcache cache = this.getCache(ann.cacheName());

        final CacheKeyGenerator cacheKeyGenerator;
        if (StringUtils.hasLength(ann.keyGeneratorName())) {
            cacheKeyGenerator = this.beanFactory.getBean(ann.keyGeneratorName(), CacheKeyGenerator.class);
        }
        else {
            cacheKeyGenerator = this.beanFactory.getBean(AnnotationDrivenEhCacheBeanDefinitionParser.DEFAULT_CACHE_KEY_GENERATOR, CacheKeyGenerator.class);
        }
        
        return new TriggersRemoveAttributeImpl(cache, cacheKeyGenerator, ann.removeAll());
    }


    /**
     * Should only public methods be allowed to have Flushable semantics?
     * <p>The default implementation returns <code>false</code>.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
    }
    
	private TriggersRemoveAttribute computeFlushableAttribute(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        TriggersRemoveAttribute att = findFlushableAttribute(specificMethod);
        if (att != null) {
            return att;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            att = findFlushableAttribute(method);
            if (att != null) {
                return att;
            }
            // Last fallback is the class of the original method.
            return findFlushableAttribute(method.getDeclaringClass());
        }
        
        return null;
    }

}
