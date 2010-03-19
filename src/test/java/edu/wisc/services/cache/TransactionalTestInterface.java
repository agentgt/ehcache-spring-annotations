package edu.wisc.services.cache;

import org.springframework.transaction.annotation.Transactional;

public interface TransactionalTestInterface {

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#getaCallCount()
	 */
	public abstract int getaCallCount();

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#getbCallCount()
	 */
	public abstract int getbCallCount();

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodA(java.lang.String)
	 */
	public abstract String methodA(String arg);

	/* (non-Javadoc)
	 * @see edu.wisc.services.cache.PlainTestInterface#methodB(java.lang.String)
	 */
	@Transactional
	public abstract String methodB(String arg);

}