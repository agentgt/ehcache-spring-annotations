/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
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

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Spring Expression Language cache key generator.
 * Usage: <pre>@Property(key="expression" value="args[ARGUMENT_INDEX].propertyOfModel"</pre>
 * 
 * <pre>
 * @Cacheable(cacheName = "testCache", 
 *            keyGenerator = @KeyGenerator(name = KEY_GENERATOR, 
 *                                         properties = @Property(name = "expression", value = "args[0].id")))
 * 	public String get(MyModel model) {
 * </pre>
 * 
 * The SpEL {@link EvaluationContext} has three variables registered on it:
 * <ul>
 *  <li><b>#invocation</b> - The {@link MethodInvocation} that is being cached</li>
 *  <li><b>#args</b> - Shortcut to the Object[] returned by {@link MethodInvocation#getArguments()}</li>
 *  <li><b>#key</b> - Key generation object where other {@link CacheKeyGenerator} implementation are registered as functions. No matter
 *      what is set via {@link #setKeyGenerators(Map)} the following functions will always be available:
 *      <ul>
 *        <li><b>#key.hash</b> - {@link HashCodeCacheKeyGenerator#generateKey(Object...)}</li>
 *        <li><b>#key.hashR</b> - {@link HashCodeCacheKeyGenerator#generateKey(Object...)} with {@link HashCodeCacheKeyGenerator#setUseReflection(boolean)} set to true</li>
 *        <li><b>#key.string</b> - {@link StringCacheKeyGenerator#generateKey(Object...)}</li>
 *        <li><b>#key.stringR</b> - {@link StringCacheKeyGenerator#generateKey(Object...)} with {@link StringCacheKeyGenerator#setUseReflection(boolean)} set to true</li>
 *        <li><b>#key.list</b> - {@link ListCacheKeyGenerator#generateKey(Object...)}</li>
 *        <li><b>#key.listR</b> - {@link ListCacheKeyGenerator#generateKey(Object...)} with {@link ListCacheKeyGenerator#setUseReflection(boolean)} set to true</li>
 *        <li><b>#key.digest</b> - {@link MessageDigestCacheKeyGenerator#generateKey(Object...)}</li>
 *        <li><b>#key.digestR</b> - {@link MessageDigestCacheKeyGenerator#generateKey(Object...)} with {@link MessageDigestCacheKeyGenerator#setUseReflection(boolean)} set to true</li>
 *      </ul>
 * </ul> 
 * 
 * @author Timothy Freyne
 */
public class SpELCacheKeyGenerator implements CacheKeyGenerator<Serializable>, BeanFactoryAware, InitializingBean, ReflectionHelperAware {
    
    private static final Map<String, Class<?>> DEFAULT_KEY_GENERATORS;
    static {
        final LinkedHashMap<String, Class<?>> keyGenerators = new LinkedHashMap<String, Class<?>>();
        
        keyGenerators.put("hash", HashCodeCacheKeyGenerator.class);
        keyGenerators.put("string", StringCacheKeyGenerator.class);
        keyGenerators.put("list", ListCacheKeyGenerator.class);
        keyGenerators.put("digest", MessageDigestCacheKeyGenerator.class);
        
        DEFAULT_KEY_GENERATORS = Collections.unmodifiableMap(keyGenerators);
    }
    
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final KeyGeneratorMethodResolver methodResolver = new KeyGeneratorMethodResolver();
    private final Object keyCallbackObject = new Object();
    
    //Used to create auto-registered key generators for function calls
    private DefaultListableBeanFactory cacheKeyBeanFactory;
    private Map<String, CacheKeyGenerator<Serializable>> registeredKeyGenerators;
	private Expression expression;
	private BeanFactory beanFactory;
	private ReflectionHelper reflectionHelper;

	/**
	 * The SpEL Expression evaluated to generate the cache key. The expression must return a {@link Serializable} object
	 */
	public void setExpression(String expression) {
	    this.expression = parseExpression(expression);
	}
    
    /**
     * A Map of {@link CacheKeyGenerator}s to register as functions on the #key object in the SpEL {@link EvaluationContext}.
     * The map key is used as the function name on the #key object.
     */
    public void setKeyGenerators(Map<String, CacheKeyGenerator<Serializable>> keyGenerators) {
        this.registeredKeyGenerators = new LinkedHashMap<String, CacheKeyGenerator<Serializable>>(keyGenerators);
    }
	
	public void setReflectionHelper(ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }
	
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.cacheKeyBeanFactory = new DefaultListableBeanFactory(this.beanFactory);
    }

    public void afterPropertiesSet() throws Exception {
        if (this.registeredKeyGenerators == null) {
            this.registeredKeyGenerators = new LinkedHashMap<String, CacheKeyGenerator<Serializable>>();
        }
        
        //Make sure the default generators are all configured
        this.registerDefaultKeyGenerators();
    }

    /**
     * Check that all {@link CacheKeyGenerator}s defined in the {@link #DEFAULT_KEY_GENERATORS} Map are registered
     * in the SpEL context as key functions.
     */
    @SuppressWarnings("unchecked")
    protected final void registerDefaultKeyGenerators() {
        for (final Entry<String, Class<?>> defaultGeneratorEntry : DEFAULT_KEY_GENERATORS.entrySet()) {
            final String name = defaultGeneratorEntry.getKey();
            final Class<CacheKeyGenerator<Serializable>> keyGeneratorClass = (Class<CacheKeyGenerator<Serializable>>)defaultGeneratorEntry.getValue();
            
            if (!this.registeredKeyGenerators.containsKey(name)) {
                final MutablePropertyValues properties = new MutablePropertyValues();
                final CacheKeyGenerator<Serializable> keyGenerator = createKeyGenerator(name, keyGeneratorClass, properties);
                this.registeredKeyGenerators.put(name, keyGenerator);
            }
            final String reflectionName = name + "R";
            if (keyGeneratorClass.isAssignableFrom(AbstractDeepCacheKeyGenerator.class) && !this.registeredKeyGenerators.containsKey(reflectionName)) {
                final MutablePropertyValues properties = new MutablePropertyValues();
                properties.addPropertyValue("useReflection", true);
                final CacheKeyGenerator<Serializable> keyGenerator = createKeyGenerator(reflectionName, keyGeneratorClass, properties);
                this.registeredKeyGenerators.put(reflectionName, keyGenerator);
            }
        }
    }

    /**
     * Create a new key generator with the specified name.
     */
    @SuppressWarnings("unchecked")
    protected CacheKeyGenerator<Serializable> createKeyGenerator(
            String name, Class<CacheKeyGenerator<Serializable>> keyGeneratorClass,
            MutablePropertyValues properties) {
        
        final AbstractBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(keyGeneratorClass);
        
        if (this.reflectionHelper != null && ReflectionHelperAware.class.isAssignableFrom(beanDefinition.getBeanClass())) {
            properties.addPropertyValue("reflectionHelper", this.reflectionHelper);
        }
        beanDefinition.setPropertyValues(properties);
        
        this.cacheKeyBeanFactory.registerBeanDefinition(name, beanDefinition);
        
        return this.cacheKeyBeanFactory.getBean(name, CacheKeyGenerator.class);
    }
    
    public Serializable generateKey(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        
        final EvaluationContext evaluationContext = this.getEvaluationContext(methodInvocation, arguments);
        return this.expression.getValue(evaluationContext, Serializable.class);
    }

    public Serializable generateKey(Object... data) {
	    final EvaluationContext evaluationContext = getEvaluationContext(null, data);
	    return this.expression.getValue(evaluationContext, Serializable.class);
	}

    /**
     * Parse the specified String into a SpEL Expression
     */
    protected Expression parseExpression(String expression) {
        return this.expressionParser.parseExpression(expression);
    }

    /**
     * Get the {@link EvaluationContext} to use to evaluate the configured {@link Expression}
     */
    protected EvaluationContext getEvaluationContext(MethodInvocation methodInvocation, Object... args) {
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
	    evaluationContext.setBeanResolver(new BeanFactoryResolver(this.beanFactory));
	    evaluationContext.setVariable("invocation", methodInvocation);
	    evaluationContext.setVariable("args", args);
	    evaluationContext.setVariable("key", keyCallbackObject);
	    evaluationContext.addMethodResolver(this.methodResolver);
        return evaluationContext;
    }
    
    /**
     * Special method resolver that uses the keys from the Map of registered key generators as method names. Allows
     * for easy access to various key generation schemes from within the SpEL Expression
     */
    private class KeyGeneratorMethodResolver implements MethodResolver {
        public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
            //Only resolve methods for our fake callback object
            if (keyCallbackObject != targetObject) {
                return null;
            }
            
            final CacheKeyGenerator<Serializable> cacheKeyGenerator = registeredKeyGenerators.get(name);
            if (cacheKeyGenerator == null) {
                return null;
            }
            
            return new MethodExecutor() {
                public TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException {
                    return new TypedValue(cacheKeyGenerator.generateKey(arguments));
                }
            };
        }
    }
}
