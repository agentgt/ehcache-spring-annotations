/**
 * Copyright 2010 The Board of Regents of the University of Wisconsin System.
 */
package edu.wisc.services.cache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class TransactionAnnotationTest {

	private TransactionalTestInterface transactionalTestInterface;
	
	/**
	 * @param transactionalTestInterface the transactionalTestInterface to set
	 */
	@Autowired
	public void setTransactionalTestInterface(
			TransactionalTestInterface transactionalTestInterface) {
		this.transactionalTestInterface = transactionalTestInterface;
	}

	/**
     * Test where Cacheable annotations exist only on the underlying implementation.
     */
    @Test
    public void testCachingOnTheImpl() {
    	 Assert.assertEquals(0, transactionalTestInterface.getaCallCount());
    	 Assert.assertEquals(0, transactionalTestInterface.getbCallCount());
    	 
    	 Assert.assertEquals("methodA says foo", transactionalTestInterface.methodA("foo"));
    	 //Assert.assertEquals(1, transactionalTestInterface.getaCallCount());
    	 //Assert.assertEquals(0, transactionalTestInterface.getbCallCount());
    	 Assert.assertEquals("methodB says bar", transactionalTestInterface.methodB("bar"));
    	 //Assert.assertEquals(1, transactionalTestInterface.getaCallCount());
    	 //Assert.assertEquals(1, transactionalTestInterface.getbCallCount());
    	 
    	 Assert.assertEquals("methodA says foo", transactionalTestInterface.methodA("foo"));
    	 //Assert.assertEquals(2, transactionalTestInterface.getaCallCount());
    	 Assert.assertEquals("methodA says foo", transactionalTestInterface.methodA("foo"));
    	 //Assert.assertEquals(3, transactionalTestInterface.getaCallCount());
    	 
    	 Assert.assertEquals("methodB says bar", transactionalTestInterface.methodB("bar"));
    	 //Assert.assertEquals(1, transactionalTestInterface.getbCallCount());
    	 //Assert.assertEquals("methodB says bar", transactionalTestInterface.methodB("bar"));
    	 //Assert.assertEquals(1, transactionalTestInterface.getbCallCount());
    }
}
