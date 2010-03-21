/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache.key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ObjectUtils;

import edu.wisc.services.cache.annotations.Cacheable;
import edu.wisc.services.cache.annotations.TriggersRemove;
import edu.wisc.services.cache.util.Reflections;

/**
 * If you plan on using the {@link TriggersRemove} annotation,
 * you should use this as the {@link CacheKeyGenerator} instead.
 * 
 * This implementation will generate cache keys based on the method arguments
 * only.
 * 
 * There are 2 primary requirements to using this implementation:
 * <ol>
 * <li>Your {@link TriggersRemove} and {@link Cacheable} annotated methods MUST have the same arguments: type, number and order.</li>
 * <li>Correctly determine whether this implementation should use your arguments' implementations of #hashCode() or allow this class to calculate a valid hashCode via reflection.</li>
 * </ol>
 * 
 * The default behavior of this class is to trust your argument's implementations of #hashCode() (e.g. {@link #isOverrideArgumentHashCode()} returns false}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
public class ArgumentsOnlyCacheKeyGenerator implements CacheKeyGenerator {

	private boolean overrideArgumentHashCode = false;
	/**
	 * @param overrideArgumentHashCode the overrideArgumentHashCode to set
	 */
	public void setOverrideArgumentHashCode(boolean overrideArgumentHashCode) {
		this.overrideArgumentHashCode = overrideArgumentHashCode;
	}
	/**
	 * @return the overrideArgumentHashCode
	 */
	public boolean isOverrideArgumentHashCode() {
		return overrideArgumentHashCode;
	}

	/**
	 * This method returns an {@link ArrayList} containing a {@link Serializable} object ({@link HashCodeCacheKey} 
	 * for each of the Objects in {@link MethodInvocation#getArguments()}.
	 * 
	 * The returned object is an {@link ArrayList} because a) it is {@link Serializable} and b) the 
	 * contract for {@link List#hashCode()} incorporates the hashCode of all of it's contained elements.
	 * 
	 * @see List#hashCode()
	 * @see ArrayList
	 * @see CacheKeyGenerator#generateKey(MethodInvocation)
	 */
	@Override
	public Serializable generateKey(MethodInvocation methodInvocation) {
		Object [] arguments = methodInvocation.getArguments();
		ArrayList<Serializable> resultList = new ArrayList<Serializable>();
		for(Object o : arguments) {
			int objectHashCode;
			if(overrideArgumentHashCode) {
				objectHashCode = Reflections.reflectionHashCode(o);
			} else {
				objectHashCode = ObjectUtils.nullSafeHashCode(o);
			}
			
			HashCodeCalculator calculator = new HashCodeCalculator();
			calculator.append(objectHashCode);
			long checkSum = calculator.getCheckSum();
			int hashCode = calculator.getHashCode();

		    Serializable objectKey = new HashCodeCacheKey(checkSum, hashCode);
		    resultList.add(objectKey);
		}
		return resultList;
	}

}
